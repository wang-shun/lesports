package com.lesports.sms.data.service.soda;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.sms.api.common.*;
import com.lesports.sms.api.vo.TMatchAction;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import freemarker.template.utility.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Created by zhonglin on 2016/3/21.
 */
@Service("sodaTimeLineParser")
public class SodaTimeLineParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaTimeLineParser.class);

    @Override
    public Boolean parseData(String file) {
        logger.info("soda time line parser begin");
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            //比赛信息
            Element matchElement = rootElement.element("Match");
            String mSodaId = matchElement.attributeValue("id");
            String homeSodaId = matchElement.attributeValue("homeId");
            String awaySodaId = matchElement.attributeValue("awayId");
            String homeScore = matchElement.attributeValue("homeScore");
            String awayScore = matchElement.attributeValue("awayScore");
            String homeHalfScore = matchElement.attributeValue("homeHalfScore");
            String awayHalfScore = matchElement.attributeValue("awayHalfScore");
            String periodId = matchElement.attributeValue("periodId");

            Match match = SbdsInternalApis.getMatchBySodaId(mSodaId);

            //如果有锁则不更新
            if (match != null && match.getLock() != null && match.getLock() == true) {
                logger.warn("the match may be locked,sodaId:{}, mid:{}", mSodaId, match.getId());
                return result;
            }

            //主客场球队
            Team homeTeam = SbdsInternalApis.getTeamBySodaId(homeSodaId);
            Team awayTeam = SbdsInternalApis.getTeamBySodaId(awaySodaId);

            if (null == homeTeam || null == awayTeam) {
                logger.warn("the team not found,homeSodaId:{},awaySodaId:{}", homeSodaId, awaySodaId);
                return result;
            }


            //比赛状态
            Byte matchStatusByte = new Byte("0");
            if ("8".equals(periodId)) {
                matchStatusByte = new Byte("2"); //已结束
            } else if ("1".equals(periodId)) {
                matchStatusByte = new Byte("0"); //未开始
            } else {
                matchStatusByte = new Byte("1"); //比赛中
            }
            match.setStatus(MatchStatus.findByValue(matchStatusByte));


            Set<Match.Competitor> competitors = match.getCompetitors();
            Set<Match.Competitor> newCompetitors = new HashSet<>();

            //更新对阵双方的最终比分和阶段比分
            if (null != competitors && competitors.size() > 0) {
                Iterator competitorIterator = competitors.iterator();
                while (competitorIterator.hasNext()) {
                    Match.Competitor competitor = (Match.Competitor) competitorIterator.next();
                    if (competitor.getGround().equals(GroundType.HOME)) {
                        competitor.setFinalResult(homeScore);
                        competitor.setSectionResults(generateSectionResult(periodId, homeScore, homeHalfScore, "home"));
                    } else {
                        competitor.setFinalResult(awayScore);
                        competitor.setSectionResults(generateSectionResult(periodId, homeScore, awayHalfScore, "away"));
                    }
                    newCompetitors.add(competitor);
                }
            } else {
                Match.Competitor homeCompetitor = new Match.Competitor();
                Match.Competitor awayCompetitor = new Match.Competitor();
                homeCompetitor.setCompetitorId(homeTeam.getId());
                homeCompetitor.setGround(GroundType.HOME);
                homeCompetitor.setType(CompetitorType.TEAM);
                awayCompetitor.setCompetitorId(awayTeam.getId());
                awayCompetitor.setType(CompetitorType.TEAM);
                awayCompetitor.setGround(GroundType.AWAY);
                newCompetitors.add(homeCompetitor);
                newCompetitors.add(awayCompetitor);
            }

            match.setCompetitors(newCompetitors);


            //删除原有matchAction
            List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(match.getId() + 2);
            if (CollectionUtils.isNotEmpty(matchActions)) {
                Iterator iterator1 = matchActions.iterator();
                while (iterator1.hasNext()) {
                    MatchAction matchAction = (MatchAction) iterator1.next();
                    SbdsInternalApis.deleteMatchAction(matchAction.getId());
                }
            }

            //获取matchAction和moment
            Element eventsElement = matchElement.element("Events");
            String moment = generateMatchAction(eventsElement, match.getId(), homeTeam, awayTeam);
            if (StringUtils.isNotBlank(moment)) {
                match.setMoment(moment);
                match.setMultiLangMoments(getMultiLang(moment));
            }

            //计算控球率
            Element possessionElement = matchElement.element("Possession");
            Set<Match.CompetitorStat> competitorStats = generatePossession(possessionElement, match.getCompetitorStats(), homeTeam, awayTeam);
            match.setCompetitorStats(competitorStats);

            updateMatchScore(match);
            result = true;
            logger.info("soda time line parser end");
        } catch (Exception e) {
            logger.error("parse time line  file {}  error", file, e);
        }
        return result;
    }

    /**
     * 更新赛事
     */
    private void updateMatchScore(Match match) {
        try {
            boolean isSuccess = false;
            int tryCount = 0;
            while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
                try {
                    isSuccess = SbdsInternalApis.saveMatch(match) > 0;
//                    isSuccess = true;
                    logger.info("match: " + match);
                } catch (Exception e) {
                    logger.error("fail to update match. id : {}. sleep and try again.", match.getId(), e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            logger.info("update delta match success,matchId:" + match.getId() + ",matchName:" + match.getName() +
                    ",matchStatus:" + match.getStatus());
        } catch (Exception e) {
            logger.error("error: ", e);
        }

    }


    //获取matchAction
    private String generateMatchAction(Element eventsElement, Long matchId, Team homeTeam, Team awayTeam) {
        Long yellowType;
        Long redType;
        Long goalType;
        Long subtitutionType;
        Long goalDetailType;
        String moment = "";
        try {
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^黄牌$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                yellowType = dictEntries.get(0).getId();
            } else {
                logger.warn("no dict 黄牌 found");
                return moment;
            }
            List<DictEntry> dictEntries1 = SbdsInternalApis.getDictEntriesByName("^红牌$");
            if (CollectionUtils.isNotEmpty(dictEntries1)) {
                redType = dictEntries1.get(0).getId();
            } else {
                logger.warn("no dict 红牌 found");
                return moment;
            }

            List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^进球$");
            if (CollectionUtils.isNotEmpty(dictEntries2)) {
                goalType = dictEntries2.get(0).getId();
            } else {
                logger.warn("no dict 进球 found");
                return moment;
            }
            List<DictEntry> dictEntries3 = SbdsInternalApis.getDictEntriesByName("^换人$");
            if (CollectionUtils.isNotEmpty(dictEntries3)) {
                subtitutionType = dictEntries3.get(0).getId();
            } else {
                logger.warn("no dict 换人 found");
                return moment;
            }

            long lastGoalTime = 0;
            String lastGoalPeriodId = "0";
            Iterator<Element> eventIterator = eventsElement.elementIterator("Event");
            while (eventIterator.hasNext()) {
                Element eventElement = eventIterator.next();
                String eventName = eventElement.attributeValue("eventName");
                if ("黄牌".equals(eventName) || "红牌".equals(eventName)) {
                    MatchAction matchAction = new MatchAction();
                    matchAction.setAllowCountries(getAllowCountries());
                    matchAction.setPassedTime(Double.parseDouble(eventElement.attributeValue("min")) * 60 + Double.parseDouble(eventElement.attributeValue("sec")));
                    matchAction.setMid(matchId);
                    if ("黄牌".equals(eventName)) {
                        matchAction.setType(yellowType);
                    }
                    if ("红牌".equals(eventName)) {
                        matchAction.setType(redType);
                    }
                    String tSodaId = eventElement.attributeValue("teamId");
                    if (tSodaId.equals(homeTeam.getSodaId())) {
                        matchAction.setTid(homeTeam.getId());
                    }
                    if (tSodaId.equals(awayTeam.getSodaId())) {
                        matchAction.setTid(awayTeam.getId());
                    }
                    matchAction.setType(subtitutionType);

                    Player player = SbdsInternalApis.getPlayerBySodaId(eventElement.attributeValue("playerId"));
                    if (null != player) {
                        matchAction.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,soda playerId:{}", eventElement.attributeValue("playerId"));
                        continue;
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                }

                if ("换人".equals(eventName)) {
                    MatchAction matchAction = new MatchAction();
                    String playerOutId = eventElement.attributeValue("subOnId");
                    String playerInId = eventElement.attributeValue("playerId");
                    matchAction.setPassedTime(Double.parseDouble(eventElement.attributeValue("min")) * 60 + Double.parseDouble(eventElement.attributeValue("sec")));
                    matchAction.setMid(matchId);
                    matchAction.setType(subtitutionType);
                    String tSodaId = eventElement.attributeValue("teamId");
                    if (tSodaId.equals(homeTeam.getSodaId())) {
                        matchAction.setTid(homeTeam.getId());
                    }
                    if (tSodaId.equals(awayTeam.getSodaId())) {
                        matchAction.setTid(awayTeam.getId());
                    }
                    Player playerOut = SbdsInternalApis.getPlayerBySodaId(playerOutId);
                    Player playerIn = SbdsInternalApis.getPlayerBySodaId(playerInId);
                    if (null == playerOut || null == playerIn) {
                        logger.warn("the player not found,soda playerOutId:{},soda playerInId:{}", eventElement.attributeValue("subOnId"), eventElement.attributeValue("playerId"));
                        continue;
                    }
                    matchAction.setPid(playerOut.getId());
                    HashMap<String, Long> map = new HashMap();
                    map.put("playerOut", playerOut.getId());
                    map.put("playerIn", playerIn.getId());
                    matchAction.setContent(JSON.toJSONString(map));
                    logger.info("matchAction: " + matchAction);
                    SbdsInternalApis.saveMatchAction(matchAction);
                }
                if ("进球".equals(eventName)) {
                    MatchAction matchAction = new MatchAction();
                    goalDetailType = Constants.goalType.get(eventElement.attributeValue("type")) == null ? 100159000L : Constants.goalType.get(eventElement.attributeValue("type"));
                    matchAction.setPassedTime(Double.parseDouble(eventElement.attributeValue("min")) * 60 + Double.parseDouble(eventElement.attributeValue("sec")));
                    matchAction.setMid(matchId);
                    String tSodaId = eventElement.attributeValue("teamId");
                    if (tSodaId.equals(homeTeam.getSodaId())) {
                        matchAction.setTid(homeTeam.getId());
                    }
                    if (tSodaId.equals(awayTeam.getSodaId())) {
                        matchAction.setTid(awayTeam.getId());
                    }
                    matchAction.setType(goalType);
                    HashMap<String, Long> map = new HashMap();
                    map.put("goalType", goalDetailType);
                    matchAction.setContent(JSON.toJSONString(map));
                    Player player = SbdsInternalApis.getPlayerBySodaId(eventElement.attributeValue("playerId"));
                    if (null != player) {
                        matchAction.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,soda playerId:{}", eventElement.attributeValue("playerId"));
                        continue;
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                    logger.info("matchAction: " + matchAction);

                    lastGoalTime = LeNumberUtils.toLong(eventElement.attributeValue("min"));
                    lastGoalPeriodId = eventElement.attributeValue("periodId");
                }
            }

            //最后进球时刻
            if (lastGoalTime > 0 && !"0".equals(lastGoalPeriodId)) {
                if ("2".equals(lastGoalPeriodId)) {
                    moment = "上半场-" + lastGoalTime;
                } else if ("4".equals(lastGoalPeriodId)) {
                    moment = "下半场-" + lastGoalTime;
                } else if ("5".equals(lastGoalPeriodId)) {
                    moment = "加时上半场-" + lastGoalTime;
                } else if ("6".equals(lastGoalPeriodId)) {
                    moment = "加时下半场-" + lastGoalTime;
                } else if ("7".equals(lastGoalPeriodId)) {
                    moment = "点球-" + lastGoalTime;
                }

                logger.info("当前比赛状态和时间:{}", moment);
            }

        } catch (Exception e) {
            logger.error("generateMatchAction  error", e);
        }
        return moment;
    }

    //获取阶段比分
    private List<Match.SectionResult> generateSectionResult(String periodId, String score, String halfScore, String type) {
        int periodIdInt = LeNumberUtils.toInt(periodId);
        int scoreInt = LeNumberUtils.toInt(score);
        int halfScoreInt = LeNumberUtils.toInt(halfScore);

        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            //上半场开打
            if (periodIdInt > 1) {
                Match.SectionResult sectionResult = new Match.SectionResult();
                sectionResult.setResult(halfScore);
                sectionResult.setOrder(1);
                sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
                sectionResults.add(sectionResult);
            }
            //下半场开打
            if (periodIdInt > 3) {
                Match.SectionResult sectionResult = new Match.SectionResult();
                sectionResult.setResult((scoreInt - halfScoreInt) + "");
                sectionResult.setOrder(2);
                sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
                sectionResults.add(sectionResult);
            }
        } catch (Exception e) {
            logger.error("generateSectionResult  error", e);
        }
        return sectionResults;
    }

    //获取控球率
    private Set<Match.CompetitorStat> generatePossession(Element minElement, Set<Match.CompetitorStat> competitorStats, Team homeTeam, Team awayTeam) {
        int homeMin = 0;
        int awayMin = 0;
        int homePossession = 0;
        int awayPossession = 0;
        Set<Match.CompetitorStat> newCompetitorStats = Sets.newHashSet();
        try {
            Iterator<Element> minIterator = minElement.elementIterator("Min");
            while (minIterator.hasNext()) {
                Element possessionElement = minIterator.next();
                //最后一个元素
                if (!minIterator.hasNext()) {
                    homeMin = LeNumberUtils.toInt(possessionElement.attributeValue("home"));
                    awayMin = LeNumberUtils.toInt(possessionElement.attributeValue("away"));
                    homePossession = homeMin * 100 / (homeMin + awayMin);
                    awayPossession = awayMin * 100 / (homeMin + awayMin);
                }
            }
            if (CollectionUtils.isNotEmpty(competitorStats)) {
                for (Match.CompetitorStat competitorStat : competitorStats) {
                    Map<String, Object> map = competitorStat.getStats();
                    if (competitorStat.getCompetitorId() == homeTeam.getId()) {
                        map.put("ballPossession", homePossession);
                    }
                    if (competitorStat.getCompetitorId() == awayTeam.getId()) {
                        map.put("ballPossession", awayPossession);
                    }
                    competitorStat.setStats(map);
                    newCompetitorStats.add(competitorStat);
                }
            }

        } catch (Exception e) {
            logger.error("generatePossession  error", e);
        }
        return newCompetitorStats;
    }
}
