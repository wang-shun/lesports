package com.lesports.sms.data.service.soda;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TeamRankStatistic;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
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
 * Created by ruiyuansheng on 2016/2/24.
 */
@Service("sodaRankingParser")
public class SodaRankingParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaRankingParser.class);

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
//            String competitionId = competitionInfo.element("competition").attributeValue("id");
//            String competitionName = competitionInfo.elementText("competition");
//
//            Competition competition = SbdsInternalApis.getCompetitionById(Constants.sodaSMSCompetittionMap.get(competitionId));
//            if (null == competition) {
//                logger.warn("can not find relative event,name:{}", competitionName);
//                return result;
//            }
//            String currentSeason = competitionInfo.elementText("season");
//            long cid = competition.getId();
//
//
//            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
//            if (null == competitionSeason) {
//                logger.warn("can not find relative event,name:{},season:{}", competitionName, currentSeason);
//                return result;
//            }
//
//            long csid = competitionSeason.getId();
//
//            Long type = 0L;
//            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^积分榜$");
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
//            //球队排名信息
//            Element ranking = rootElmement.element("ranking");
//            Node node = ranking.selectSingleNode("(/root/ranking//round)[last()]");
//            if (node instanceof Element) {
//                Element lastRound = (Element) node;
//                String round = lastRound.attributeValue("round");
//
//                Iterator<Element> leagrankIterator = lastRound.elementIterator("ranking");
//                List<TopList.TopListItem> topListItems = new ArrayList<>();
//                while (leagrankIterator.hasNext()) {
//                    Element leagrank = leagrankIterator.next();
//                    String rank = leagrank.attributeValue("rank");
//                    String teamId = leagrank.attributeValue("id");
//                    String total = leagrank.attributeValue("total");
//                    String win = leagrank.attributeValue("win");
//                    String draw = leagrank.attributeValue("draw");
//                    String lose = leagrank.attributeValue("lose");
//                    String goal = leagrank.attributeValue("goal");
//                    String concede = leagrank.attributeValue("concede");
//                    String goaldiffer = leagrank.attributeValue("goaldiffer");
//                    String score = leagrank.attributeValue("score");
//                    Team team = SbdsInternalApis.getTeamBySodaId(teamId);
//                    if (team == null) {
//                        logger.warn("cannot find team,teamId:{}", teamId);
//                        continue;
//                    }
//                    TopList.TopListItem topListItem = new TopList.TopListItem();
//
//                    topListItem.setCompetitorId(team.getId());
//                    topListItem.setCompetitorType(CompetitorType.TEAM);
//                    topListItem.setRank(Integer.parseInt(rank));
//                    topListItem.setShowOrder(Integer.parseInt(rank));
//
//                    TeamRankStatistic teamRankStatistic = new TeamRankStatistic();
//                    teamRankStatistic.setMatchNumber(LeNumberUtils.toInt(total));
//                    teamRankStatistic.setTeamScore(LeNumberUtils.toInt(score));
//                    teamRankStatistic.setWinMatch(LeNumberUtils.toInt(win));
//                    teamRankStatistic.setLossMatch(LeNumberUtils.toInt(lose));
//                    teamRankStatistic.setFlatMatch(LeNumberUtils.toInt(draw));
//                    teamRankStatistic.setGoal(LeNumberUtils.toInt(goal));
//                    teamRankStatistic.setFumble(LeNumberUtils.toInt(concede));
//                    teamRankStatistic.setGoalDiffer(LeNumberUtils.toInt(goaldiffer));
//
//                    Map<String, Object> map = CommonUtil.convertBeanToMap(teamRankStatistic);
//                    topListItem.setStats(map);
//                    topListItems.add(topListItem);
//                }
//                topList.setItems(topListItems);
//
//            }
//
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
            String competitionId = competitionInfo.attributeValue("id");
            String competitionName = competitionInfo.attributeValue("name");

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

            Long type = 0L;
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^积分榜$");
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

            //球队排名信息
            Node node = competitionInfo.selectSingleNode("(/SoccerFeed/Competition/Round)[last()]");
            if (node instanceof Element) {
                Element lastRound = (Element) node;
                String round = lastRound.attributeValue("round");
                logger.info("soda ranking round:{}", round);

                Iterator<Element> leagrankIterator = lastRound.elementIterator("Team");
                List<TopList.TopListItem> topListItems = new ArrayList<>();
                while (leagrankIterator.hasNext()) {
                    Element leagrank = leagrankIterator.next();
                    String rank = leagrank.attributeValue("rank");
                    String teamId = leagrank.attributeValue("id");
                    String total = leagrank.attributeValue("matchs");
                    String win = leagrank.attributeValue("win");
                    String draw = leagrank.attributeValue("draw");
                    String lose = leagrank.attributeValue("lose");
                    String goal = leagrank.attributeValue("goal");
                    String concede = leagrank.attributeValue("concede");
                    String goaldiffer = leagrank.attributeValue("goaldiffer");
                    String score = leagrank.attributeValue("score");
                    Team team = SbdsInternalApis.getTeamBySodaId(teamId);
                    if (team == null) {
                        logger.warn("cannot find team,teamId:{}", teamId);
                        continue;
                    }
                    TopList.TopListItem topListItem = new TopList.TopListItem();

                    topListItem.setCompetitorId(team.getId());
                    topListItem.setCompetitorType(CompetitorType.TEAM);
                    topListItem.setRank(Integer.parseInt(rank));
                    topListItem.setShowOrder(Integer.parseInt(rank));

                    TeamRankStatistic teamRankStatistic = new TeamRankStatistic();
                    teamRankStatistic.setMatchNumber(LeNumberUtils.toInt(total));
                    teamRankStatistic.setTeamScore(LeNumberUtils.toInt(score));
                    teamRankStatistic.setWinMatch(LeNumberUtils.toInt(win));
                    teamRankStatistic.setLossMatch(LeNumberUtils.toInt(lose));
                    teamRankStatistic.setFlatMatch(LeNumberUtils.toInt(draw));
                    teamRankStatistic.setGoal(LeNumberUtils.toInt(goal));
                    teamRankStatistic.setFumble(LeNumberUtils.toInt(concede));
                    teamRankStatistic.setGoalDiffer(LeNumberUtils.toInt(goaldiffer));

                    Map<String, Object> map = CommonUtil.convertBeanToMap(teamRankStatistic);
                    topListItem.setStats(map);
                    topListItems.add(topListItem);
                }
                topList.setItems(topListItems);

            }

            SbdsInternalApis.saveTopList(topList);
            logger.info("insert toplist success,cId:{},csId:{}", topList.getCid(), topList.getCsid());
            result = true;
        } catch (Exception e) {
            logger.error("insert into toplist  error: ", e);
        }
        return result;
    }

}
