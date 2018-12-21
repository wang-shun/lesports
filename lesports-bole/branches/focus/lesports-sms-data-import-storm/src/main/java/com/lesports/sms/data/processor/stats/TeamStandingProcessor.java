//package com.lesports.sms.data.processor.stats;
//
//import com.lesports.sms.api.common.CompetitorType;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.stats.StatsTeamStanding;
//import com.lesports.sms.data.processor.BeanProcessor;
//import com.lesports.sms.data.processor.Processor;
//import com.lesports.sms.data.processor.olympic.AbstractProcessor;
//import com.lesports.sms.model.CompetitionSeason;
//import com.lesports.sms.model.Team;
//import com.lesports.sms.model.TopList;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by qiaohongxin on 2016/5/12.
// */
//public class TeamStandingProcessor extends AbstractProcessor implements BeanProcessor<StatsTeamStanding> {
//    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(TeamStandingProcessor.class);
//
//    public Boolean process(String fileType, StatsTeamStanding standing) {
//        LOG.info("team standing file  parser begin");
//        Boolean result = false;
//        Long cid = SportrardConstants.nameMap.get(standing.getTourmamentId());
//        CompetitionSeason competitionseason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);//Constans.getCid(standing.getTourmamentId()));
//        if (competitionseason == null) {
//            LOG.warn("can not find the right time competions by tournamentId:{}", standing.getTourmamentId());
//            return result;
//        }
//        Long csid = competitionseason.getId();
//
//        if (CollectionUtils.isEmpty(standing.getDivisionStandings())) {
//            LOG.warn("the standing is empty by tournamentId:{}", standing.getTourmamentId());
//            return false;
//        }
//     //   Processor currentMatchProcess = ProcessorFactory.getProcessByGameFtype(499, standing.getSportId().toString());
//        TopList conferenceTopList = getTopList(cid, csid, standing.getConferenceName(), 0L);
//        List<TopList.TopListItem> conferenceTopListItems = null;
//        if (conferenceTopList != null) {
//            conferenceTopListItems = new ArrayList<TopList.TopListItem>();
//        }
//        for (StatsTeamStanding.DivisionStanding currentDivision : standing.getDivisionStandings()) {
//            TopList divisionTopList = getTopList(cid, csid, currentDivision.getDivisionName(), 0L);
//            if (divisionTopList == null || CollectionUtils.isEmpty(currentDivision.getTeamStandings())) continue;
//            List<TopList.TopListItem> divisionTopListItems = new ArrayList<TopList.TopListItem>();
//            for (StatsTeamStanding.TeamStanding currentTeam : currentDivision.getTeamStandings()) {
//                Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(currentTeam.getTeamId(), 499);
//                if (team == null) {
//                    LOG.warn("cannot find team,teamPartnerId:{}", currentTeam.getTeamId());
//                    continue;
//                }
//                TopList.TopListItem divisionTopListItem = new TopList.TopListItem();
//                divisionTopListItem.setCompetitorId(team.getId());
//                divisionTopListItem.setCompetitorType(CompetitorType.TEAM);
//                divisionTopListItem.setRank(currentTeam.getDivisionRank());
//                divisionTopListItem.setShowOrder(currentTeam.getDivisionRank());
//                divisionTopListItem.setStats(currentMatchProcess.getDivisionStandingStats(currentTeam.getStats()));
//                divisionTopListItems.add(divisionTopListItem);
//                if (conferenceTopListItems != null) {
//                    TopList.TopListItem conferenceTopListItem = new TopList.TopListItem();
//                    conferenceTopListItem.setCompetitorId(team.getId());
//                    conferenceTopListItem.setCompetitorType(CompetitorType.TEAM);
//                    conferenceTopListItem.setRank(currentTeam.getConferenceRank());
//                    conferenceTopListItem.setShowOrder(currentTeam.getConferenceRank());
//                    conferenceTopListItem.setStats(currentMatchProcess.getConferenceStandingStats(currentTeam.getStats()));
//                    conferenceTopListItems.add(conferenceTopListItem);
//                }
//            }
//            SbdsInternalApis.saveTopList(divisionTopList);
//
//        }
//        SbdsInternalApis.saveTopList(conferenceTopList);
//        return true;
//    }
//
//
//    private TopList getTopList(Long cid, Long csid, String standigName, Long stage) {
//        Long standinTtype = 0L;
//        if (SportrardConstants.TeamrankingType.get(standigName) != null) {
//            standinTtype = SportrardConstants.TeamrankingType.get(standigName);
//        }
//        TopList topList = new TopList();
//        List<TopList> topLists = new ArrayList<>();
//        topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, standinTtype);
//        if (CollectionUtils.isNotEmpty(topLists)) {
//            topList = topLists.get(0);
//        } else {
//            topList.setAllowCountries(getAllowCountries());
//        }
//        topList.setDeleted(false);
//        topList.setCid(cid);
//        topList.setCsid(csid);
//        topList.setLatest(true);
//        topList.setType(standinTtype);
//        topList.setStage(stage);
//        return topList;
//    }
//}
//
//
