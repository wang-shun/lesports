package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.sbd.SbdCompetitionInternalApis;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.api.common.GroundOrder;
import com.lesports.qmt.sbd.model.*;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultSchedule;
import com.lesports.sms.data.process.impl.DefaultScheduleProcessor;
import com.lesports.sms.data.process.impl.DefaultTeamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/15/11.
 */
@Service("scheduleAdaptor")
public class DefaultScheduleAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultScheduleAdaptor.class);
    @Resource
    DefaultScheduleProcessor scheduleProcessor;
    @Resource
    DefaultTeamProcessor teamProcessor;

    public Boolean nextProcessor(com.lesports.qmt.sbd.model.PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultSchedule.ScheduleModel scheduleModel = (DefaultSchedule.ScheduleModel) object;
            //check the tranfer info
            CompetitionSeason curentCompetitionSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid);
            if (curentCompetitionSeason == null) {
                LOG.warn("CompetitionSeason:{} can not be found,the csid:{}", csid);
                return false;
            }
            Competition competition = SbdCompetitionInternalApis.getCompetitionById(curentCompetitionSeason.getCid());
            if (competition == null) {
                LOG.warn("Competition:{} can not be found,the sid:{}", curentCompetitionSeason.getCid());
                return false;
            }
            //get the database model of schedule
            Match currentMatch = convertModelToMatch(partnerType, competition, csid, scheduleModel);
            if (currentMatch == null) {
                LOG.error("adaptor schedule fail");
                return false;
            }
            return scheduleProcessor.process(currentMatch);
        } catch (
                Exception e
                )

        {
            LOG.error("adaptor schedule fail", e);
        }

        return false;
    }


    // convert the default model to match
    private Match convertModelToMatch(PartnerType type, Competition competition, Long csid, DefaultSchedule.ScheduleModel scheduleModel) {
        //init the basic info of match
        Team homeTeam = getTeam(type, scheduleModel.getHomeTeamId(), scheduleModel.getHomeTeamName());
        Team awayTeam = getTeam(type, scheduleModel.getAwayTeamId(), scheduleModel.getAwayTeamName());
        if (homeTeam == null || awayTeam == null) return null;
        Match match = new Match();
        match.setCid(competition.getId());
        match.setCsid(csid);
        match.setStatus(scheduleModel.getStatus());
        match.setPartners(Lists.newArrayList(getPartnerwithCode(scheduleModel.getPartnerId(), "", type)));
        match.setName(getMatchName(competition, scheduleModel.getStageId(), scheduleModel.getRoundId(), homeTeam, awayTeam));
        match.setGameFType(competition.getGameFType());
        match.setStartTime(scheduleModel.getStartTime());
        match.setStartDate(match.getStartTime().substring(0, 8));
        match.setGroup(scheduleModel.getRoundId());
        match.setStage(scheduleModel.getStageId());
        match.setDeleted(false);
        match.setRound(scheduleModel.getRoundId());
        // build the competitor info of the match
        Set<Match.Competitor> competitorSet = Sets.newHashSet();
        Match.Competitor homeCompetitor = new Match.Competitor();
        homeCompetitor.setCompetitorId(homeTeam.getId());
        homeCompetitor.setType(com.lesports.qmt.sbd.api.common.CompetitorType.TEAM);
        homeCompetitor.setGround(com.lesports.qmt.sbd.api.common.GroundType.HOME);
        homeCompetitor.setFinalResult(scheduleModel.getHomeScore());
        competitorSet.add(homeCompetitor);
        Match.Competitor awayCompetitor = new Match.Competitor();
        awayCompetitor.setCompetitorId(awayTeam.getId());
        awayCompetitor.setType(com.lesports.qmt.sbd.api.common.CompetitorType.TEAM);
        awayCompetitor.setGround(com.lesports.qmt.sbd.api.common.GroundType.AWAY);
        awayCompetitor.setFinalResult(scheduleModel.getHomeScore());
        competitorSet.add(awayCompetitor);
        match.setCompetitors(competitorSet);
        return match;
    }

    private Team getTeam(PartnerType partnerType, String partnerId, String name) {
        Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(partnerId, partnerType));
        if (currentTeam != null) return currentTeam;
        else {
            currentTeam = new Team();
            currentTeam.setPartners(Lists.newArrayList(getPartner(partnerId, partnerType)));
            currentTeam.setName(name);
            teamProcessor.process(currentTeam);
            currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(partnerId, partnerType));
            if (currentTeam != null) return currentTeam;
        }
        return currentTeam;
    }

    private String getMatchName(Competition competition, Long stageId, Long roundId, Team homeTeam, Team awayTeam) {
        String stage = "";
        String round = "";
        if (stageId != null) stage = QmtConfigDictInternalApis.getDictById(stageId).getName();
        if (roundId != null) round = QmtConfigDictInternalApis.getDictById(roundId).getName();
        if (competition.getGroundOrder().equals(GroundOrder.AWAYVSHOME)) {
            return competition.getName() + " " + stage + round + " " + awayTeam.getName() + " " + "VS" + " " + homeTeam.getName();
        }
        return competition.getName() + " " + stage + round + " " + homeTeam.getName() + " " + "VS" + " " + awayTeam.getName();
    }

}
