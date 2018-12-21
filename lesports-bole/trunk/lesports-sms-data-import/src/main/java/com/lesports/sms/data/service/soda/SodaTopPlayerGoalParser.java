package com.lesports.sms.data.service.soda;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.PlayerRandStatistic;
import com.lesports.sms.data.model.TeamRankStatistic;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by ruiyuansheng on 2016/3/1.
 */
@Service("sodaTopPlayerGoalParser")
public class SodaTopPlayerGoalParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaTopPlayerGoalParser.class);

//    @Override
//    public Boolean parseData(String file) {
//
//        Boolean result = false;
//        try {
//            File xmlFile = new File(file);
//            if (!xmlFile.exists()) {
//                logger.warn("parsing the file:{} not exists", file);
//                return result;
//            }
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(xmlFile);
//            Element rootElmement = document.getRootElement();
//
//            // 赛事基本信息，赛事名称、当前赛季、当前轮次
//            Element competitionInfo = rootElmement.element("competitionInfo");
//            String competitionName = competitionInfo.elementText("competition");
//            String competitionId = competitionInfo.element("competition").attributeValue("id");
//
//            Competition competition = SbdsInternalApis.getCompetitionById(Constants.sodaSMSCompetittionMap.get(competitionId));
//            if (null == competition) {
//                logger.warn("can not find relative event,name:{}", competitionName);
//                return result;
//            }
//            String currentSeason = competitionInfo.elementText("season");
//            long cid = competition.getId();
//
//            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
//            if (null == competitionSeason) {
//                logger.warn("can not find relative event,name:{},season:{}", competitionName, currentSeason);
//                return result;
//            }
//            long csid = competitionSeason.getId();
//            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");
//            if (CollectionUtils.isEmpty(dictEntryList)) {
//                logger.warn("can not find gameFType,name:足球");
//                return result;
//            }
//            Long gameFType = dictEntryList.get(0).getId();
//
//            Long type = 0L;
//            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^射手榜$");
//            if (CollectionUtils.isNotEmpty(dictEntries)) {
//                type = dictEntries.get(0).getId();
//            }
//
//            TopList topList = new TopList();
//            List<TopList> topLists = new ArrayList<>();
//            topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, type);
//            if (CollectionUtils.isNotEmpty(topLists)) {
//                topList = topLists.get(0);
//            } else {
//                topList.setAllowCountries(getAllowCountries());
//                topList.setOnlineLanguages(getOnlineLanguage());
//            }
//
//            topList.setDeleted(false);
//            topList.setCid(cid);
//            topList.setCsid(csid);
//            topList.setLatest(true);
//            topList.setType(type);
//
//            //球员排名信息
//            Element goals = rootElmement.element("goals");
//            Iterator<Element> goalIte = goals.elementIterator("goal");
//            List<TopList.TopListItem> topListItems = new ArrayList<>();
//            int order = 0;
//            while (goalIte.hasNext()) {
//
//                Element goalElement = goalIte.next();
//                String playerName = goalElement.attributeValue("playerName");
//                String playerId = goalElement.attributeValue("playerId");
//                String teamName = goalElement.attributeValue("teamName");
//                String teamId = goalElement.attributeValue("teamId");
//                String total = goalElement.attributeValue("total");
//                String goal = goalElement.attributeValue("goal");
//                String penalty = goalElement.attributeValue("penalty");
//                String own = goalElement.attributeValue("own");
//                String freekick = goalElement.attributeValue("freekick");
//                String header = goalElement.attributeValue("header");
//                String caps = goalElement.attributeValue("caps");
//                String counName = goalElement.attributeValue("counName");
//
//                Player player = SbdsInternalApis.getPlayerBySodaId(playerId);
//                if (null == player) {
//                    InternalQuery query = new InternalQuery().addCriteria(new InternalCriteria("name", "regex", "^" + playerName + "$"));
//
//                    List<Player> players = SbdsInternalApis.getPlayersByQuery(query);
//                    if (CollectionUtils.isNotEmpty(players)) player = players.get(0);
//                    if (null == player) {
//                        logger.warn("cannot find player,sodaId:{}", playerId);
//                        player = new Player();
//                        player.setSodaId(playerId);
//                        player.setDeleted(false);
//                        player.setName(playerName);
//                        player.setMultiLangNames(getMultiLang(playerName));
//                        player.setNationality(counName);
//                        player.setMultiLangNationalities(getMultiLang(counName));
//                        player.setGameFType(gameFType);
//                        player.addCids(cid);
//                        player.setAllowCountries(getAllowCountries());
//
//                    } else {
//                        player.setSodaId(playerId);
//                    }
//                    SbdsInternalApis.savePlayer(player);
//                    logger.info("insert into player sucess,sodaId:" + player);
//                }
//                Team team = SbdsInternalApis.getTeamBySodaId(teamId);
//                if (team == null) {
//                    logger.warn("cannot find team,sodaId:{}", teamId);
//                    continue;
//                }
//                TopList.TopListItem topListItem = new TopList.TopListItem();
//
//                topListItem.setCompetitorId(player.getId());
//                topListItem.setTeamId(team.getId());
//                topListItem.setCompetitorType(CompetitorType.PLAYER);
//                order++;
//                topListItem.setRank(order);
//                topListItem.setShowOrder(order);
//                PlayerRandStatistic playerRandStatistic = new PlayerRandStatistic();
//                playerRandStatistic.setGoals(LeNumberUtils.toInt(total));
//                playerRandStatistic.setPenaltyNumber(LeNumberUtils.toInt(penalty));
//                playerRandStatistic.setCaps(LeNumberUtils.toInt(caps));
//                playerRandStatistic.setOwn(LeNumberUtils.toInt(own));
//                Map<String, Object> map = CommonUtil.convertBeanToMap(playerRandStatistic);
//                topListItem.setStats(map);
//                topListItems.add(topListItem);
//
//            }
//            topList.setItems(topListItems);
//            SbdsInternalApis.saveTopList(topList);
//            logger.info("insert toplist success,cId:{},csId:{}", topList.getCid(), topList.getCsid());
//            result = true;
//        } catch (Exception e) {
//            logger.error("insert into toplist  error: ", e);
//        }
//        return result;
//    }

    @Override
    public Boolean parseData(String file) {

        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElmement = document.getRootElement();

            // 赛事基本信息，赛事名称、当前赛季、当前轮次
            Element competitionInfo = rootElmement.element("Competition");
            if(competitionInfo==null){
                logger.warn("competitionInfo is null");
            }
            String competitionName = competitionInfo.attributeValue("name");
            String competitionId = competitionInfo.attributeValue("id");

            Competition competition = SbdsInternalApis.getCompetitionById(Constants.sodaSMSCompetittionMap.get(competitionId));
            if (null == competition) {
                logger.warn("can not find relative event,name:{}", competitionName);
                return result;
            }
            String currentSeason = competitionInfo.attributeValue("season");
            long cid = competition.getId();

            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if (null == competitionSeason) {
                logger.warn("can not find relative event,name:{},season:{}", competitionName, currentSeason);
                return result;
            }
            long csid = competitionSeason.getId();
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");
            if (CollectionUtils.isEmpty(dictEntryList)) {
                logger.warn("can not find gameFType,name:足球");
                return result;
            }
            Long gameFType = dictEntryList.get(0).getId();

            Long type = 0L;
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^射手榜$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                type = dictEntries.get(0).getId();
            }

            TopList topList = new TopList();
            List<TopList> topLists = new ArrayList<>();
            topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, type);
            if (CollectionUtils.isNotEmpty(topLists)) {
                topList = topLists.get(0);
            } else {
                topList.setAllowCountries(getAllowCountries());
                topList.setOnlineLanguages(getOnlineLanguage());
            }

            topList.setDeleted(false);
            topList.setCid(cid);
            topList.setCsid(csid);
            topList.setLatest(true);
            topList.setType(type);

            //球员排名信息
            Element goals = competitionInfo.element("Goal");
            Iterator<Element> goalIte = goals.elementIterator("Player");
            List<TopList.TopListItem> topListItems = new ArrayList<>();
            int order = 0;
            while (goalIte.hasNext()) {

                Element goalElement = goalIte.next();
                String playerName = goalElement.attributeValue("name");
                String playerId = goalElement.attributeValue("id");
                String teamName = goalElement.attributeValue("clubName");
                String teamId = goalElement.attributeValue("clubId");
                String total = goalElement.attributeValue("goal");
                String goal = goalElement.attributeValue("general");
                String penalty = goalElement.attributeValue("penalty");
                String own = goalElement.attributeValue("own");
                String freekick = goalElement.attributeValue("freekick");
                String header = goalElement.attributeValue("header");
                String caps = goalElement.attributeValue("lineup");
                String counName = goalElement.attributeValue("counName");

                Player player = SbdsInternalApis.getPlayerBySodaId(playerId);
                if (null == player) {
                    InternalQuery query = new InternalQuery().addCriteria(new InternalCriteria("name", "regex", "^" + playerName + "$"));
                    query.addCriteria(InternalCriteria.where("deleted").is(false));
                    query.addCriteria(InternalCriteria.where("game_f_type").is(gameFType));
                    List<Player> players = SbdsInternalApis.getPlayersByQuery(query);
                    if (CollectionUtils.isNotEmpty(players)) player = players.get(0);
                    if (null == player) {
                        logger.warn("cannot find player,sodaId:{}", playerId);
                        player = new Player();
                        player.setSodaId(playerId);
                        player.setDeleted(false);
                        player.setName(playerName);
                        player.setMultiLangNames(getMultiLang(playerName));
                        player.setNationality(counName);
                        player.setMultiLangNationalities(getMultiLang(counName));
                        player.setGameFType(gameFType);
                        player.addCids(cid);
                        player.setAllowCountries(getAllowCountries());
                        player.setOnlineLanguages(getOnlineLanguage());

                    } else {
                        player.setSodaId(playerId);
                    }
                    SbdsInternalApis.savePlayer(player);
                    logger.info("insert into player sucess,sodaId:" + player);
                }
                Team team = SbdsInternalApis.getTeamBySodaId(teamId);
                if (team == null) {
                    logger.warn("cannot find team,sodaId:{}", teamId);
                    continue;
                }
                TopList.TopListItem topListItem = new TopList.TopListItem();

                topListItem.setCompetitorId(player.getId());
                topListItem.setTeamId(team.getId());
                topListItem.setCompetitorType(CompetitorType.PLAYER);
                order++;
                topListItem.setRank(order);
                topListItem.setShowOrder(order);
                PlayerRandStatistic playerRandStatistic = new PlayerRandStatistic();
                playerRandStatistic.setGoals(LeNumberUtils.toInt(total));
                playerRandStatistic.setPenaltyNumber(LeNumberUtils.toInt(penalty));
                playerRandStatistic.setCaps(LeNumberUtils.toInt(caps));
                playerRandStatistic.setOwn(LeNumberUtils.toInt(own));
                Map<String, Object> map = CommonUtil.convertBeanToMap(playerRandStatistic);
                topListItem.setStats(map);
                topListItems.add(topListItem);

            }
            topList.setItems(topListItems);
            SbdsInternalApis.saveTopList(topList);
            logger.info("insert toplist success,cId:{},csId:{}", topList.getCid(), topList.getCsid());
            result = true;
        } catch (Exception e) {
            logger.error("insert into toplist  error: ", e);
        }
        return result;
    }
}
