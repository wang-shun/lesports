//package com.lesports.sms.data.processor.sportrard.InnerProcessor;
//
//import com.google.common.collect.Maps;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.CommonLiveScore;
//import com.lesports.sms.data.model.sportrard.MatchLiveScore;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.sportrard.SportrardTeamStanding;
//import com.lesports.sms.data.processor.Processor;
//import com.lesports.sms.model.Match;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by qiaohongxin on 2016/4/1.
// */
//public class BasketBallProcessor extends Processor {
//    private static Logger logger = LoggerFactory.getLogger(BasketBallProcessor.class);
//
//    @Override
//    public Map<String, Object> getTeamStandingStats(List<SportrardTeamStanding.LeagueTableColumn> columns) {
//        Map<String, Object> stats = Maps.newHashMap();
//        for (SportrardTeamStanding.LeagueTableColumn rowElement : columns) {
//            String statsName = SportrardConstants.BKTeamStandingStatPath.get(rowElement.getKey());
//            if (statsName != null) stats.put(statsName, rowElement.getValue());
//        }
//        return stats;
//    }
//
//    @Override
//    public List<Match.SectionResult> getSectionResults(CommonLiveScore liveScore1, String type) {
//        MatchLiveScore liveScore = (MatchLiveScore) liveScore1;
//        Element scores = liveScore.getScores();
//        String homeScore1 = "0";
//        String awayScore1 = "0";
//        String homeScore2 = "0";
//        String awayScore2 = "0";
//        String homeScore3 = "0";
//        String awayScore3 = "0";
//        String homeScore4 = "0";
//        String awayScore4 = "0";
//        String homeScore5 = "0";
//        String awayScore5 = "0";
//        List<Match.SectionResult> sectionResults = new ArrayList<>();
//        try {
//            if (scores != null) {
//                Iterator scoreIterator = scores.elementIterator("Score");
//                while (scoreIterator.hasNext()) {
//                    Element score = (Element) scoreIterator.next();
//                    if (score.attributeValue("type").equals("Period1")) {
//                        homeScore1 = score.elementText("Team1");
//                        awayScore1 = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        if ("home".equals(type))
//                            sectionResult.setResult(homeScore1);
//                        else
//                            sectionResult.setResult(awayScore1);
//                        sectionResult.setOrder(1);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第1节$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//
//                    if (score.attributeValue("type").equals("Period2")) {
//                        homeScore2 = score.elementText("Team1");
//                        awayScore2 = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        if ("home".equals(type))
//                            sectionResult.setResult(homeScore2);
//                        else
//                            sectionResult.setResult(awayScore2);
//                        sectionResult.setOrder(2);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第2节$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                    if (score.attributeValue("type").equals("Period3")) {
//                        homeScore3 = score.elementText("Team1");
//                        awayScore3 = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        if ("home".equals(type))
//                            sectionResult.setResult(homeScore3);
//                        else
//                            sectionResult.setResult(awayScore3);
//                        sectionResult.setOrder(3);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第3节$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                    if (score.attributeValue("type").equals("Period4")) {
//                        homeScore4 = score.elementText("Team1");
//                        awayScore4 = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        if ("home".equals(type))
//                            sectionResult.setResult(homeScore4);
//                        else
//                            sectionResult.setResult(awayScore4);
//                        sectionResult.setOrder(4);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第4节$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                    if (score.attributeValue("type").equals("Overtime")) {
//                        homeScore5 = score.elementText("Team1");
//                        awayScore5 = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        if ("home".equals(type))
//                            sectionResult.setResult(homeScore5);
//                        else
//                            sectionResult.setResult(awayScore5);
//                        sectionResult.setOrder(5);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^加时$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                }
//            }
//            for (Match.SectionResult sectionResult : sectionResults) {
//                if (sectionResult.getOrder() == 5) {
//                    Integer othomeScore = Integer.parseInt(homeScore5) - (Integer.parseInt(homeScore1) + Integer.parseInt(homeScore2) + Integer.parseInt(homeScore3) + Integer.parseInt(homeScore4));
//                    Integer otawayScore = Integer.parseInt(awayScore5) - (Integer.parseInt(awayScore1) + Integer.parseInt(awayScore2) + Integer.parseInt(awayScore3) + Integer.parseInt(awayScore4));
//                    if ("home".equals(type))
//                        sectionResult.setResult(othomeScore + "");
//                    else
//                        sectionResult.setResult(otawayScore + "");
//                }
//            }
//        } catch (Exception e) {
//            logger.error("generateSectionResult  error", e);
//        }
//
//        return sectionResults;
//    }
//}
//
//
