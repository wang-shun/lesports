package com.lesports.sms.data.processor;

import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.sportrard.SportrardConstants;
import com.lesports.sms.data.processor.olympic.AbstractProcessor;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
public abstract class CommonLiveScoreProcessor extends AbstractProcessor {

    private static Logger logger = LoggerFactory.getLogger(CommonLiveScoreProcessor.class);

//    public Boolean processLiveScore(CommonLiveScore matchLiveScore) {
//
//        Match match1 = SbdsInternalApis.getMatchByPartnerIdAndType(matchLiveScore.getPartnerId(), getPartnerType());
//        //如果有锁则不更新
//        if (match1 == null || match1.getLock() != null && match1.getLock()) {
//            logger.warn("the match may be locked,partnerId:{}", matchLiveScore.getPartnerId());
//            return false;
//        }
//        Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(matchLiveScore.getHomeTeamId(), getPartnerType());
//        Team team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(matchLiveScore.getAwayTeamId(), getPartnerType());
//        if (null == team1 || null == team2) {
//            logger.warn("the team not found,partner1Id:{},partner2Id:{}", matchLiveScore.getHomeTeamId(), matchLiveScore.getAwayTeamId());
//            return false;
//        }
//        //更新比赛状态
//        match1.setStatus(getMatchStatus(matchLiveScore.getStatus()));
//        Processor currentMatchProcess = ProcessorFactory.getProcessByGameFtype(getPartnerType(),getGameFTypeName(matchLiveScore.getSportId()));
//        match1.setCompetitors(currentMatchProcess.getCompetitorData(match1.getCompetitors(), matchLiveScore));
//        match1.setSquads(currentMatchProcess.getSquadData(matchLiveScore));
//        match1.setMoment(currentMatchProcess.getMatchMomentData(matchLiveScore));
//        match1.setCurrentMoment(currentMatchProcess.getCurrentMomentData(matchLiveScore));
//        match1.setCompetitorStats(currentMatchProcess.getCompetitorStatsData(matchLiveScore));
//        match1.setBestPlayerStats(currentMatchProcess.getBestPlayerStats(match1.getSquads()));
//        currentMatchProcess.updateMatchAction(matchLiveScore);
//        boolean isSuccess = false;
//        int tryCount = 0;
//        while (!isSuccess && tryCount++ < SportrardConstants.MAX_TRY_COUNT) {
//            try {
//                isSuccess = SbdsInternalApis.saveMatch(match1) > 0;
//            } catch (Exception e) {
//                logger.error("fail to update match. id : {}. sleep and try again.", match1.getId(), e);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e1) {
//                    logger.error("{}", e.getMessage(), e);
//                }
//            }
//        }
//        logger.info("update match success,matchId:" + match1.getId() + ",matchName:" + match1.getName() + ",matchPartnerId:" + match1.getPartnerId() +
//                ",matchStatus:" + match1.getStatus());
//        return true;
//    }
    public abstract  MatchStatus getMatchStatus(String status);
    public abstract Integer getPartnerType();
    public abstract String getGameFTypeName(String sportId);
}


