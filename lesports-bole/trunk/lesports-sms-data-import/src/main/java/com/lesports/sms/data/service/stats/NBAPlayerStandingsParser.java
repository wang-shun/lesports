package com.lesports.sms.data.service.stats;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by qiaohongxin on 2015/9/18.
 */
@Service("NBAPlayerStandingsParser")
public class NBAPlayerStandingsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(NBAPlayerStandingsParser.class);

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
            Element nbaStandings = rootElement.element("sports-rankings");
            //get tournament name and season
            String name = nbaStandings.element("league").attributeValue("alias");
            String season = nbaStandings.element("season").attributeValue("season");
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + name, gameFTypeId);
            if (competitions == null || competitions.isEmpty()) {
                logger.warn("can not find relative competions,name is" + name);
                return result;
            }
            CompetitionSeason competitionseason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(competitions.get(0).getId(), season);
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + name + "season is" + season);
                return result;
            }
            Iterator<Element> nbaPlayerStandings = nbaStandings.element("nba-leaders").element("nba-player-rankings").elementIterator("category");
            while (nbaPlayerStandings.hasNext()) {
                Element nbaPlayerStanding = nbaPlayerStandings.next();
                String league = nbaPlayerStanding.attributeValue("league");
                String standingname = nbaPlayerStanding.attributeValue("category").replaceAll(" ", "");
                if (!league.equalsIgnoreCase("NBA") || Constants.Top20rankingTypeId.get(standingname) == null)
                    continue;
                TopList playerTopList = getTopList(competitions.get(0).getId(), competitionseason.getId(), Constants.Top20rankingTypeId.get(standingname));
                List<TopList.TopListItem> playerTopListItems = new ArrayList<TopList.TopListItem>();
                //get division ranking data
                Iterator<Element> rankings = nbaPlayerStanding.elementIterator("ranking");
                while (rankings.hasNext()) {
                    Element ranking = rankings.next();
                    playerTopListItems.add(getTopListItem(standingname, ranking));
                }
                playerTopList.setItems(playerTopListItems);
                SbdsInternalApis.saveTopList(playerTopList);
                logger.info("Player Ranking  order by " + standingname + "is created sucessfully");
                result = true;
            }
        } catch (Exception e) {
            logger.debug("player ranking is parse fail", e);
        }
        return result;
    }

    private TopList getTopList(Long cid, Long csid, Long rankingtypeId) {
        TopList topList = null;
        List<TopList> topLists = new ArrayList<>();
        topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, rankingtypeId);
        if (CollectionUtils.isNotEmpty(topLists)) {
            topList = topLists.get(0);

        } else {
            topList = new TopList();
            topList.setDeleted(false);
            topList.setCid(cid);
            topList.setCsid(csid);
            topList.setLatest(true);
            topList.setType(rankingtypeId);
            topList.setAllowCountries(getAllowCountries());
            topList.setOnlineLanguages(getOnlineLanguage());
        }
        topList.setCompetitorType(CompetitorType.PLAYER);
        topList.setScopType(ScopeType.CONFERENCE);
        topList.setScope(100009000L);

        return topList;
    }

    private TopList.TopListItem getTopListItem(String standingName, Element ranking) {
        String PlayerPartnerId = ranking.element("player-id").attributeValue("global-id");//ranking.attributeValue("global-id");
        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(PlayerPartnerId, Constants.PartnerSourceStaticId);
        if (player == null) {
            logger.warn("cannot find team,teamPartnerId:{}", PlayerPartnerId);
            return null;
        }
        String teamPartnerId = ranking.element("team-code").attributeValue("global-id");// ranking.attributeValue("global-team-id");
        Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Constants.PartnerSourceStaticId);
        if (team == null) {
            logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
            return null;
        }
        TopList.TopListItem playerTopListItem = new TopList.TopListItem();
        Map<String, Object> map = new HashMap<>();
        map.put(standingName, CommonUtil.parseDouble(ranking.element("stat").attributeValue("stat"), 0.0));
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
