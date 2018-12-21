package com.lesports.sms.data.service.stats.euro;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.PlayerRandStatistic;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.StatsConstants;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by zhonglin on 2016/5/3.
 */
@Service("EUROPlayerStandingsParser")
public class EUROPlayerStandingsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(EUROPlayerStandingsParser.class);

    @Override
    public Boolean parseData(String file) {
        logger.info("player standing file  parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();
            Element euroStandings = rootElement.element("sports-rankings");
            //get tournament name and season
            String name = euroStandings.element("league").attributeValue("alias");
            String season = euroStandings.element("season").attributeValue("year");
            // get mathseason information
            Competition competition = SbdsInternalApis.getCompetitionById(StatsConstants.COMPETITIONS_ID);
            if(competition == null){
                logger.warn("can not find relative event,name:{}", name);
                return result;
            }
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("cid").is(competition.getId()));
            internalQuery.addCriteria(InternalCriteria.where("season").regex(season));
            List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);
            if (CollectionUtils.isEmpty(competitionSeasons)) {
                logger.warn("can not find relative event,name:{},season:{}", name, season);
                return result;
            }
            Iterator<Element> euroPlayerStandings = euroStandings.element("soccer-ifb-leaders").element("ifb-player-rankings").elementIterator("category");
            while (euroPlayerStandings.hasNext()) {
                Element euroPlayerStanding = euroPlayerStandings.next();
                String standingname = euroPlayerStanding.attributeValue("category").replaceAll(" ", "");
                if (Constants.footballRankingTypeId.get(standingname) == null)
                    continue;
                TopList playerTopList = getTopList(competition.getId(), competitionSeasons.get(0).getId(), Constants.footballRankingTypeId.get(standingname));
                List<TopList.TopListItem> playerTopListItems = new ArrayList<TopList.TopListItem>();
                //get division ranking data
                Iterator<Element> rankings = euroPlayerStanding.elementIterator("ranking");
                while (rankings.hasNext()) {
                    Element ranking = rankings.next();
                    TopList.TopListItem currentPlayerItem = getTopListItem(standingname, ranking);
                    if (currentPlayerItem == null) continue;
                    playerTopListItems.add(currentPlayerItem);
                }
                playerTopList.setItems(playerTopListItems);
                SbdsInternalApis.saveTopList(playerTopList);
                logger.info("Player Ranking  order by " + standingname + "is created sucessfully id: " + playerTopList.getId());
                result = true;
            }
        } catch (Exception e) {
            logger.debug("player ranking is parse fail", e);
        }
        return result;
    }

    private TopList getTopList(Long cid, Long csid, Long rankingtypeId) {
        TopList topList = null;
        List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, rankingtypeId);
        if (CollectionUtils.isNotEmpty(topLists)) {
            topList = topLists.get(0);
            return topList;
        }
        topList = new TopList();
        topList.setDeleted(false);
        topList.setCid(cid);
        topList.setCsid(csid);
        topList.setLatest(true);
        topList.setType(rankingtypeId);
        topList.setAllowCountries(getAllowCountries());
        topList.setOnlineLanguages(getOnlineLanguage());
        return topList;
    }

    private TopList.TopListItem getTopListItem(String standingName, Element ranking) {
        String PlayerPartnerId = ranking.element("player-id").attributeValue("global-id");//ranking.attributeValue("global-id");
        Player player = SbdsInternalApis.getPlayerByStatsId(PlayerPartnerId);
        if (player == null) {
            logger.warn("cannot find player ,playerPartnerId:{}", PlayerPartnerId);
            return null;
        }
        String teamPartnerId = ranking.element("team-info").attributeValue("global-id");// ranking.attributeValue("global-team-id");
        Team team = SbdsInternalApis.getTeamByStatsId(teamPartnerId);
        if (team == null) {
            logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
            return null;
        }
        TopList.TopListItem playerTopListItem = new TopList.TopListItem();

        PlayerRandStatistic playerRandStatistic = new PlayerRandStatistic();
        playerRandStatistic.setGoals(CommonUtil.parseInt(ranking.element("stat").attributeValue("stat"), 0));
        Map<String, Object> map = CommonUtil.convertBeanToMap(playerRandStatistic);
        playerTopListItem.setStats(map);
        playerTopListItem.setCompetitorId(player.getId());
        playerTopListItem.setCompetitorType(CompetitorType.PLAYER);
        playerTopListItem.setTeamId(team.getId());
        playerTopListItem.setRank(CommonUtil.parseInt(ranking.element("ranking").attributeValue("ranking"), 0));
        playerTopListItem.setShowOrder(CommonUtil.parseInt(ranking.element("ranking").attributeValue("ranking"), 0));
        logger.info("add  team information to conference list");
        return playerTopListItem;

    }
}
