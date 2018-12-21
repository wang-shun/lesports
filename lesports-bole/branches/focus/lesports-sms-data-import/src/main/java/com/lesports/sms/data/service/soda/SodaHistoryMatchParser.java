package com.lesports.sms.data.service.soda;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.LeConstants;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchResult;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.MatchResultAdapter;
import com.lesports.sms.data.model.SodaHomeAwayStat;
import com.lesports.sms.data.model.SodaMatchStat;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.MatchReview;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import java.io.File;
import java.util.*;

/**
 * Created by ruiyuansheng on 2016/2/25.
 */
@Service("sodaHistoryMatchParser")
public class SodaHistoryMatchParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(SodaHistoryMatchParser.class);

    private static final int MAX_SIZE = 10;
    private static final int AFTER_MAX_SIZE = 6;

    @Resource
    private MatchResultAdapter matchResultAdapter;

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
//            Element matchInfo = rootElmement.element("Preview").element("Match");
//            String matchId = matchInfo.elementText("id");
//            String stadium = matchInfo.elementText("stadium");
//            String judge = matchInfo.elementText("judge");
//            String weather = matchInfo.elementText("weather");
//
//
//            Match match = SbdsInternalApis.getMatchBySodaId(matchId);
//            long homeTeamId = 0L;
//            long awayTeamId = 0L;
//            if (null != match) {
//                match.setVenue(stadium);
//                match.setMultiLangVenues(getMultiLang(stadium));
//                match.setJudge(judge);
//                match.setMultiLangJudges(getMultiLang(judge));
//                match.setWeather(weather);
//                match.setMultiLangWeathers(getMultiLang(weather));
//                SbdsInternalApis.saveMatch(match);
//                Set<Match.Competitor> competitors = match.getCompetitors();
//                if (CollectionUtils.isNotEmpty(competitors)) {
//                    Iterator<Match.Competitor> competitorIterator = competitors.iterator();
//                    while (competitorIterator.hasNext()) {
//                        Match.Competitor competitor = competitorIterator.next();
//                        if (competitor.getGround() == GroundType.HOME) {
//                            homeTeamId = competitor.getCompetitorId();
//                        } else {
//                            awayTeamId = competitor.getCompetitorId();
//                        }
//                    }
//                }
//
//                MatchReview matchReview = new MatchReview();
//                matchReview.setId(match.getId());
//                matchReview.setName(match.getName());
//                matchReview.setMultiLangNames(getMultiLang(match.getName()));
//                matchReview.setDeleted(false);
//                List<MatchReview.HistoryMatch> historyMatches = Lists.newArrayList();
//                Element con = rootElmement.element("confrontation");
//                Iterator<Element> matchIterator = con.elementIterator("match");
//                SodaHomeAwayStat sodaMatchStat = new SodaHomeAwayStat();
//                int homeWin = 0;
//                int homeLose = 0;
//                int homeFlat = 0;
//                int awayWin = 0;
//                int awayLose = 0;
//                int awayFlat = 0;
//                int homeGoal = 0;
//                int homeConcede = 0;
//                int awayGoal = 0;
//                int awayConcede = 0;
//                while (matchIterator.hasNext()) {
//
//                    Element matchElement = matchIterator.next();
//                    String historyMatchId = matchElement.attributeValue("id");
//
//
//                    Match historyMatch = SbdsInternalApis.getMatchBySodaId(historyMatchId);
//                    if (null != historyMatch) {
//                        int homeScore = 0;
//                        int awayScore = 0;
//                        Set<Match.Competitor> historyCompetitors = historyMatch.getCompetitors();
//                        for (Match.Competitor competitor : historyCompetitors) {
//                            if (competitor.getCompetitorId().equals(homeTeamId)) {
//                                homeScore = LeNumberUtils.toInt(competitor.getFinalResult());
//                                homeGoal += homeScore;
//                                awayConcede += homeScore;
//                            } else {
//                                awayScore = LeNumberUtils.toInt(competitor.getFinalResult());
//                                homeConcede += awayScore;
//                                awayGoal += awayScore;
//                            }
//                        }
//                        if (homeScore > awayScore) {
//                            homeWin++;
//                            awayLose++;
//                        } else if (homeScore == awayScore) {
//                            homeFlat++;
//                            awayFlat++;
//                        } else if (homeScore < awayScore) {
//                            awayWin++;
//                            homeLose++;
//                        }
//                        MatchReview.HistoryMatch historyMatch1 = matchResultAdapter.adapt(historyMatch);
//                        historyMatches.add(historyMatch1);
//                        if (historyMatches.size() >= MAX_SIZE)
//                            break;
//                    }
//
//                }
//
//                matchReview.setConfrontations(historyMatches);
//                sodaMatchStat.setHomeWin(homeWin);
//                sodaMatchStat.setHomeLose(homeLose);
//                sodaMatchStat.setHomeFlat(homeFlat);
//                sodaMatchStat.setAwayWin(awayWin);
//                sodaMatchStat.setAwayLose(awayLose);
//                sodaMatchStat.setAwayFlat(awayFlat);
//                sodaMatchStat.setHomeGoal(homeGoal);
//                sodaMatchStat.setHomeconcede(homeConcede);
//                sodaMatchStat.setAwayConcede(awayConcede);
//                sodaMatchStat.setAwayGoal(awayGoal);
//
//                Map<String, Object> map = CommonUtil.convertBeanToMap(sodaMatchStat);
//                matchReview.setStats(map);
//
//                Set<MatchReview.MatchInfo> matchInfos = Sets.newHashSet();
//                matchInfos.add(getMatchInfo(rootElmement, homeTeamId, GroundType.HOME));
//                matchInfos.add(getMatchInfo(rootElmement, awayTeamId, GroundType.AWAY));
//
//                matchReview.setMatchInfos(matchInfos);
//                matchReview.setAllowCountries(getAllowCountries());
//                SbdsInternalApis.saveMatchReview(matchReview);
//                logger.info("insert into history match success,matchId:{}" + match.getId());
//            }
//
//        } catch (Exception e) {
//            logger.error("insert into history match  error: ", e);
//        }
//        return result;
//    }
//
//    private MatchReview.MatchInfo getMatchInfo(Element element, long homeTeamId, GroundType groundType) {
//
//        MatchReview.MatchInfo matchInfo = new MatchReview.MatchInfo();
//        matchInfo.setCompetitorId(homeTeamId);
//        matchInfo.setGround(groundType);
//        matchInfo.setType(CompetitorType.TEAM);
//        String teamType = "";
//        if (GroundType.HOME == groundType) {
//            teamType = "home";
//        } else {
//            teamType = "away";
//        }
//        List<MatchReview.HistoryMatch> nearmatchers = getHistoryMatch(element, "nearMatch", teamType);
//        matchInfo.setNearMatches(nearmatchers);
//        int win = 0;
//        int lose = 0;
//        int flat = 0;
//        int goal = 0;
//        int concede = 0;
//        SodaMatchStat sodaMatchStat = new SodaMatchStat();
//        for (MatchReview.HistoryMatch historyMatch : nearmatchers) {
//            int homeScore = 0;
//            int awayScore = 0;
//            Set<Match.Competitor> historyCompetitors = historyMatch.getCompetitors();
//            for (Match.Competitor competitor : historyCompetitors) {
//                if (competitor.getCompetitorId().equals(homeTeamId)) {
//                    homeScore = LeNumberUtils.toInt(competitor.getFinalResult());
//                    goal += homeScore;
//                } else {
//                    awayScore = LeNumberUtils.toInt(competitor.getFinalResult());
//                    concede += awayScore;
//                }
//            }
//            if (homeScore > awayScore) {
//                win++;
//            } else if (homeScore == awayScore) {
//                flat++;
//            } else if (homeScore < awayScore) {
//                lose++;
//            }
//        }
//        sodaMatchStat.setWin(win);
//        sodaMatchStat.setLose(lose);
//        sodaMatchStat.setFlat(flat);
//        sodaMatchStat.setGoal(goal);
//        sodaMatchStat.setConcede(concede);
//        Map<String, Object> map = CommonUtil.convertBeanToMap(sodaMatchStat);
//        matchInfo.setStats(map);
//        matchInfo.setAfterMatches(getHistoryMatch(element, "afterMatch", teamType));
//        return matchInfo;
//    }
//
//    private List<MatchReview.HistoryMatch> getHistoryMatch(Element element, String matchType, String teamType) {
//        List<MatchReview.HistoryMatch> matchList = Lists.newArrayList();
//        Element nearMatchElement = element.element(matchType);
//        Element home = nearMatchElement.element(teamType);
//
//        Iterator<Element> homeNearMatches = home.elementIterator("match");
//        while (homeNearMatches.hasNext()) {
//            Element hMatch = homeNearMatches.next();
//            String nearMatchId = hMatch.attributeValue("id");
//
//
//            Match nearMatch = SbdsInternalApis.getMatchBySodaId(nearMatchId);
//            if (null != nearMatch) {
//                MatchReview.HistoryMatch historyMatch = matchResultAdapter.adapt(nearMatch);
//                String result = hMatch.attributeValue("result");
//                if ("胜".equals(result)) {
//                    historyMatch.setResult(MatchResult.WIN);
//                } else if ("平".equals(result)) {
//                    historyMatch.setResult(MatchResult.FLAT);
//                } else if ("负".equals(result)) {
//                    historyMatch.setResult(MatchResult.LOSE);
//                }
//                matchList.add(historyMatch);
//                if (matchList.size() >= MAX_SIZE)
//                    break;
//            }
//        }
//        return matchList;
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

            Element preview = rootElmement.element("Preview");

            Element matchInfo = preview.element("Match");
            String matchId = matchInfo.attributeValue("id");
            String stadium = matchInfo.attributeValue("stadium");
            String judge = matchInfo.attributeValue("judge");
            String weather = matchInfo.attributeValue("weather");


            Match match = SbdsInternalApis.getMatchBySodaId(matchId);
            long homeTeamId = 0L;
            long awayTeamId = 0L;
            if (null != match) {
                match.setVenue(stadium);
                match.setMultiLangVenues(getMultiLang(stadium));
                match.setJudge(judge);
                match.setMultiLangJudges(getMultiLang(judge));
                match.setWeather(weather);
                match.setMultiLangWeathers(getMultiLang(weather));
                SbdsInternalApis.saveMatch(match);
                Set<Match.Competitor> competitors = match.getCompetitors();
                if (CollectionUtils.isNotEmpty(competitors)) {
                    Iterator<Match.Competitor> competitorIterator = competitors.iterator();
                    while (competitorIterator.hasNext()) {
                        Match.Competitor competitor = competitorIterator.next();
                        if (competitor.getGround() == GroundType.HOME) {
                            homeTeamId = competitor.getCompetitorId();
                        } else {
                            awayTeamId = competitor.getCompetitorId();
                        }
                    }
                }

                MatchReview matchReview = new MatchReview();
                matchReview.setId(match.getId());
                matchReview.setName(match.getName());
                matchReview.setMultiLangNames(getMultiLang(match.getName()));
                matchReview.setDeleted(false);
                List<MatchReview.HistoryMatch> historyMatches = Lists.newArrayList();
                Element con = preview.element("History");
                Iterator<Element> matchIterator = con.elementIterator("Match");
                SodaHomeAwayStat sodaMatchStat = new SodaHomeAwayStat();
                int homeWin = 0;
                int homeLose = 0;
                int homeFlat = 0;
                int awayWin = 0;
                int awayLose = 0;
                int awayFlat = 0;
                int homeGoal = 0;
                int homeConcede = 0;
                int awayGoal = 0;
                int awayConcede = 0;
                while (matchIterator.hasNext()) {

                    Element matchElement = matchIterator.next();
                    String historyMatchId = matchElement.attributeValue("id");


                    Match historyMatch = SbdsInternalApis.getMatchBySodaId(historyMatchId);
                    if (null != historyMatch) {
                        int homeScore = 0;
                        int awayScore = 0;
                        Set<Match.Competitor> historyCompetitors = historyMatch.getCompetitors();
                        for (Match.Competitor competitor : historyCompetitors) {
                            if (competitor.getCompetitorId().equals(homeTeamId)) {
                                homeScore = LeNumberUtils.toInt(competitor.getFinalResult());
                                homeGoal += homeScore;
                                awayConcede += homeScore;
                            } else {
                                awayScore = LeNumberUtils.toInt(competitor.getFinalResult());
                                homeConcede += awayScore;
                                awayGoal += awayScore;
                            }
                        }
                        if (homeScore > awayScore) {
                            homeWin++;
                            awayLose++;
                        } else if (homeScore == awayScore) {
                            homeFlat++;
                            awayFlat++;
                        } else if (homeScore < awayScore) {
                            awayWin++;
                            homeLose++;
                        }
                        MatchReview.HistoryMatch historyMatch1 = matchResultAdapter.adapt(historyMatch);
                        historyMatches.add(historyMatch1);
                        if (historyMatches.size() >= MAX_SIZE)
                            break;
                    }

                }
                if (CollectionUtils.isNotEmpty(historyMatches)) {
                    Collections.sort(historyMatches, new Comparator<MatchReview.HistoryMatch>() {
                        @Override
                        public int compare(MatchReview.HistoryMatch o1, MatchReview.HistoryMatch o2) {
                            return o2.getStartTime().compareTo(o1.getStartTime());
                        }
                    });
                }
                matchReview.setConfrontations(historyMatches);
                sodaMatchStat.setHomeWin(homeWin);
                sodaMatchStat.setHomeLose(homeLose);
                sodaMatchStat.setHomeFlat(homeFlat);
                sodaMatchStat.setAwayWin(awayWin);
                sodaMatchStat.setAwayLose(awayLose);
                sodaMatchStat.setAwayFlat(awayFlat);
                sodaMatchStat.setHomeGoal(homeGoal);
                sodaMatchStat.setHomeconcede(homeConcede);
                sodaMatchStat.setAwayConcede(awayConcede);
                sodaMatchStat.setAwayGoal(awayGoal);

                Map<String, Object> map = CommonUtil.convertBeanToMap(sodaMatchStat);
                matchReview.setStats(map);

                Set<MatchReview.MatchInfo> matchInfos = Sets.newHashSet();

                Element before = preview.element("Before");
                Element after = preview.element("After");
                Element homeBefore = null,awayBefore = null;
                Element homeAfter = null,awayAfter = null;
                if(before != null) {
                    homeBefore = before.element("Home");
                    awayBefore = before.element("Away");
                }
                if(after != null) {
                    homeAfter = after.element("Home");
                    awayAfter = after.element("Away");
                }

                matchInfos.add(getMatchInfo(homeBefore,homeAfter, homeTeamId, GroundType.HOME));
                matchInfos.add(getMatchInfo(awayBefore,awayAfter, awayTeamId, GroundType.AWAY));

                matchReview.setMatchInfos(matchInfos);
                matchReview.setAllowCountries(getAllowCountries());
                SbdsInternalApis.saveMatchReview(matchReview);

                logger.info("insert into history match success,matchId:{}", match.getId());
            }

        } catch (Exception e) {
            logger.error("insert into history match  error: ", e);
        }
        return result;
    }

    private MatchReview.MatchInfo getMatchInfo(Element beforeElement,Element afterElement, long teamId, GroundType groundType) {

        MatchReview.MatchInfo matchInfo = new MatchReview.MatchInfo();
        matchInfo.setCompetitorId(teamId);
        matchInfo.setGround(groundType);
        matchInfo.setType(CompetitorType.TEAM);

        List<MatchReview.HistoryMatch> nearmatchers = getHistoryMatch(beforeElement,teamId, 100);
        if (CollectionUtils.isNotEmpty(nearmatchers)) {
            Collections.sort(nearmatchers, new Comparator<MatchReview.HistoryMatch>() {
                @Override
                public int compare(MatchReview.HistoryMatch o1, MatchReview.HistoryMatch o2) {
                    return o2.getStartTime().compareTo(o1.getStartTime());
                }
            });
        }
        List<MatchReview.HistoryMatch> newNearmatchers = Lists.newArrayList();
        int win = 0;
        int lose = 0;
        int flat = 0;
        int goal = 0;
        int concede = 0;
        SodaMatchStat sodaMatchStat = new SodaMatchStat();
        int i = 0;
        for (MatchReview.HistoryMatch historyMatch : nearmatchers) {
            i++;
            int homeScore = 0;
            int awayScore = 0;
            Set<Match.Competitor> historyCompetitors = historyMatch.getCompetitors();
            for (Match.Competitor competitor : historyCompetitors) {
                if (competitor.getCompetitorId().equals(teamId)) {
                    homeScore = LeNumberUtils.toInt(competitor.getFinalResult());
                    goal += homeScore;
                } else {
                    awayScore = LeNumberUtils.toInt(competitor.getFinalResult());
                    concede += awayScore;
                }
            }
            if (homeScore > awayScore) {
                win++;
            } else if (homeScore == awayScore) {
                flat++;
            } else if (homeScore < awayScore) {
                lose++;
            }
            newNearmatchers.add(historyMatch);
            if(i == MAX_SIZE)break;
        }
        matchInfo.setNearMatches(newNearmatchers);


        sodaMatchStat.setWin(win);
        sodaMatchStat.setLose(lose);
        sodaMatchStat.setFlat(flat);
        sodaMatchStat.setGoal(goal);
        sodaMatchStat.setConcede(concede);
        Map<String, Object> map = CommonUtil.convertBeanToMap(sodaMatchStat);
        matchInfo.setStats(map);
        matchInfo.setAfterMatches(getHistoryMatch(afterElement,teamId,AFTER_MAX_SIZE));
        return matchInfo;
    }

    private List<MatchReview.HistoryMatch> getHistoryMatch(Element element,long teamId,int size) {
        List<MatchReview.HistoryMatch> matchList = Lists.newArrayList();


        Iterator<Element> homeNearMatches = element.elementIterator("Match");
        while (homeNearMatches.hasNext()) {
            Element hMatch = homeNearMatches.next();
            String nearMatchId = hMatch.attributeValue("id");
            Match nearMatch = SbdsInternalApis.getMatchBySodaId(nearMatchId);
            if (null != nearMatch) {
                MatchReview.HistoryMatch historyMatch = matchResultAdapter.adapt(nearMatch);
                int homeScore = 0;
                int awayScore = 0;
                Set<Match.Competitor> historyCompetitors = historyMatch.getCompetitors();
                for (Match.Competitor competitor : historyCompetitors) {
                    if (competitor.getCompetitorId().equals(teamId)) {
                        homeScore = LeNumberUtils.toInt(competitor.getFinalResult());
                    } else {
                        awayScore = LeNumberUtils.toInt(competitor.getFinalResult());
                    }
                }
                if (homeScore > awayScore) {
                    historyMatch.setResult(MatchResult.WIN);
                } else if (homeScore == awayScore) {
                    historyMatch.setResult(MatchResult.FLAT);
                } else if (homeScore < awayScore) {
                    historyMatch.setResult(MatchResult.LOSE);
                }

                matchList.add(historyMatch);
                if (matchList.size() >= size)
                    break;
            }
        }
//        logger.info("matchList: " +matchList);
        return matchList;
    }
}
