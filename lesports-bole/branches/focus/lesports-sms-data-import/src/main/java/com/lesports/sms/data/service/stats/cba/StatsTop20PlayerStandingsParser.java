package com.lesports.sms.data.service.stats.cba;

import com.google.common.collect.ImmutableMap;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
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
 * Created by qiaohongxin on 2015/9/18.
 */
@Service("StatsTop20PlayerStandingsParser")
public class StatsTop20PlayerStandingsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(StatsTop20PlayerStandingsParser.class);
    private Map<String, String> elementIdentifyMap = ImmutableMap.of("NBA", "nba", "CBACHN", "bk");

    @Override
    public Boolean parseData(String file) {
        logger.info("player standing file  parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        String cName = file.contains("NBA") ? "NBA" : "CBACHN";
        String elementIdentify = elementIdentifyMap.get(cName);
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();
            Element nbaStandings = rootElement.element("sports-rankings");
            String season = nbaStandings.element("season").attributeValue("season");
            CompetitionSeason competitionSeason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(Constants.statsNameMap.get(cName), season);
            if (null == competitionSeason) {
                logger.warn("the competitionSeason  is empty");
                return false;
            }
            Iterator<Element> playerStandings = nbaStandings.element(elementIdentify + "-leaders").element(elementIdentify + "-player-rankings").elementIterator("category");
            while (playerStandings.hasNext()) {
                Element nbaPlayerStanding = playerStandings.next();
                String standingname = nbaPlayerStanding.attributeValue("category").replaceAll(" ", "");
                if (Constants.Top20rankingTypeId.get(standingname) == null)
                    continue;
                TopList playerTopList = getTopList(competitionSeason.getCid(), competitionSeason.getId(), Constants.Top20rankingTypeId.get(standingname));
                List<TopList.TopListItem> playerTopListItems = new ArrayList<TopList.TopListItem>();
                Iterator<Element> rankings = nbaPlayerStanding.elementIterator("ranking");//get division ranking data
                while (rankings.hasNext()) {
                    Element ranking = rankings.next();
                    TopList.TopListItem currentItem = getTopListItem(standingname, ranking);
                    if (currentItem == null) continue;
                    playerTopListItems.add(currentItem);
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
        List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, rankingtypeId);
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
