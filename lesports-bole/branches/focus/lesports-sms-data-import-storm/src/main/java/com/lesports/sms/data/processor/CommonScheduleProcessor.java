//package com.lesports.sms.data.processor;
//
//import com.lesports.sms.api.common.MatchStatus;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.CommonSchedule;
//import com.lesports.sms.data.model.sportrard.MatchSchedule;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.stats.StatsSchedule;
//import com.lesports.sms.data.utils.CommonUtil;
//import com.lesports.sms.model.*;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by qiaohongxin on 2016/5/11.
// */
//public abstract class CommonScheduleProcessor extends AbstractProcessor {
//    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(CommonScheduleProcessor.class);
//
//    public Boolean processMatch(Integer partnerType, CommonSchedule matchSchedule) {
//        Boolean result = false;
//        try {
//
//            Long cid = 47001L;
//            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
//            if (competitionSeason == null) {
//                LOG.warn("can not find relative competionSeason,tournamentId is:{}", matchSchedule.getTourmamentId());
//                return result;
//            }
//            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName(getGameFTypeName(matchSchedule.getSportId().toString(), partnerType));
//            Team homeTeam = getTeamByType("Home", 0L, cid, competitionSeason.getId(), matchSchedule.getHomeTeam().get(0));
//            Team awayTeam = getTeamByType("Away", 0L, cid, competitionSeason.getId(), matchSchedule.getAwayTeam().get(0));
//            if (homeTeam == null || awayTeam == null) {
//                LOG.warn("can not find relative team,matchId:{}", matchSchedule.getPartnerId());
//                return false;
//            }
//            Match match = SbdsInternalApis.getMatchByPartnerIdAndType(matchSchedule.getPartnerId(), partnerType);
//            if (match != null) {
//                if (match.getLock() != null && match.getLock() == true) return false;
//                if (deletePostPonedMatch(match, matchSchedule) || match.getStartTime().equals(matchSchedule.getStartTime()))
//                    return false;
//                updatePartnerMatch(match, matchSchedule);
//            } else {
//                Long matchId = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(matchSchedule.getStartTime(), homeTeam.getId(), awayTeam.getId());
//                if (matchId > 0) {
//                    if (match.getLock() != null && match.getLock() == true) return false;
//                    if (deletePostPonedMatch(match, matchSchedule)) return false;
//                    updateExistMatch(match, matchSchedule);
//                } else {
//                    match = new Match();
//                    createMatch(match, matchSchedule, competitionSeason.getId(), homeTeam, awayTeam);
//                }
//            }
//            if (saveEntity(match, Match.class)) {
//                LOG.info("update matchtime, partner_id: " + matchSchedule.getPartnerId());
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public void updatePartnerMatch(Match currentMatch, CommonSchedule matchSchedule) {
//        currentMatch.setStartTime(matchSchedule.getStartTime());
//    }
//
//    private void updateExistMatch(Match currentMatch, CommonSchedule matchSchedule) {
//        currentMatch.setStartTime(matchSchedule.getStartTime());
//        currentMatch.setPartnerId(matchSchedule.getPartnerId());
//        currentMatch.setPartnerType(469);
//    }
//
//    private void createMatch(Match currentMatch, CommonSchedule matchSchedule, Long csid, Team homeTeam, Team awayTeam) {
//        currentMatch.setAllowCountries(getAllowCountries());
//        currentMatch.setCid(SportrardConstants.nameMap.get(matchSchedule.getTourmamentId()));
//        currentMatch.setCsid(csid);
//        currentMatch.setPartnerId(matchSchedule.getPartnerId());
//        currentMatch.setPartnerType(469);
//        currentMatch.setGameFType(matchSchedule.getSportId());
//        currentMatch.setStartTime(matchSchedule.getStartTime());
//        Processor processor = ProcessorFactory.getProcessByGameFtype(469, getGameFTypeName(matchSchedule.getSportId().toString(), 469));
//        currentMatch.setCompetitors(processor.getSimpleCompetitorData(matchSchedule));
//        currentMatch.setRound(processor.getRoundData(matchSchedule));
//        currentMatch.setStage(processor.getStageData(matchSchedule));
//        currentMatch.setGroup(processor.getGroupData(currentMatch.getCid(), matchSchedule));
//        String name = "";
//        currentMatch.setName(name);
//        currentMatch.setMultiLangNames(getMultiLang(name));
//    }
//
//    public Team getTeamByType(String type, Long gameFType, Long cid, Long csid, CommonSchedule.MatchTeam matchTeam) {
//        try {
//            if (matchTeam == null) return null;
//            Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(matchTeam.getTeamId(), 469);
//            if (team != null) return team;
//            LOG.warn("cannot find related team,PartnerId:{},fileName:{}", matchTeam.getTeamId());
//            team = new Team();
//            team.setName(matchTeam.getTeamName());
//            team.setMultiLangNames(getMultiLang(matchTeam.getTeamName()));
//            team.setAllowCountries(getAllowCountries());
//            Set<Long> cids = team.getCids();
//            cids.add(cid);
//            team.setCids(cids);
//            team.setPartnerId(matchTeam.getTeamId());
//            team.setPartnerType(Integer.parseInt(SportrardConstants.partnerSourceId));
//            team.setGameFType(gameFType);
//            SbdsInternalApis.saveTeam(team);
//            LOG.info("insert into team success,partnerId:{},fileName:{}", matchTeam.getTeamId());
//            List<TeamSeason> seasonList = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), csid);
//            if (seasonList == null || seasonList.isEmpty()) {
//                TeamSeason teamSeason = new TeamSeason();
//                teamSeason.setTid(team.getId());
//                teamSeason.setCsid(csid);
//                SbdsInternalApis.saveTeamSeason(teamSeason);
//                LOG.info("insert into teamSeason success,partnerId:{}", matchTeam.getTeamId());
//            }
//            return team;
//        } catch (Exception e) {
//            LOG.error("get Team info fail", e);
//        }
//        return null;
//    }
//
//    public String getGameFTypeName(String sportId, Integer partnerType) {
//        return "足球";
//    }
//
//    public boolean deletePostPonedMatch(Match currentMatch, CommonSchedule schedule) {
//        MatchSchedule matchSchedule = (MatchSchedule) schedule;
//        if (CollectionUtils.isNotEmpty(matchSchedule.getMatchResult()) && (matchSchedule.getMatchResult().get(0).isCanceled() || matchSchedule.getMatchResult().get(0).isCanceled())) {
//            if (currentMatch.getStatus().equals(MatchStatus.MATCH_NOT_START) || StringUtils.isBlank(matchSchedule.getMatchResult().get(0).getNewPartnerId())) {
//                SbdsInternalApis.deleteMatch(currentMatch.getId());
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public String getStartTimeByPartner(Integer partnerType, CommonSchedule schedule) {
//        if (partnerType == 469) {
//            //       String startTime = CommonUtil.getDataYYYYMMDDHHMMSS(currentSchedule.getYear(), currentSchedule.getMonth(), currentSchedule.getDate(), currentSchedule.getHour(), currentSchedule.getUtcMinute(), currentSchedule.getUtcHour());
//        }
//        return null;
//
//    }
//}
