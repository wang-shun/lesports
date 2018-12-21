package com.lesports.sms.data.processor;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchSystem;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.CommonSchedule;
import com.lesports.sms.data.model.sportrard.*;
import com.lesports.sms.model.Competition;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Element;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/4/1.
 */
public abstract class Processor {
    public Integer getRankOrderData(String type, List<SportrardPlayerStanding.PlayerStatsEntry> entrys) {
        return 0;
    }

    public Map<String, Object> getPlayerStandingStats(String type, List<SportrardPlayerStanding.PlayerStatsEntry> entrys) {
        return null;
    }

    public Map<String, Object> getTeamStandingStats(List<SportrardTeamStanding.LeagueTableColumn> columns) {
        return null;
    }

    public Map<String, Object> getDivisionStandingStats(Element element) {
        return null;
    }

    public Map<String, Object> getConferenceStandingStats(Element element) {
        return null;
    }

    //TODO
    public Set<Match.BestPlayerStat> getBestPlayerStats(List<Match.Squad> squads) {
        return null;
    }

    public Set<Match.Competitor> getSimpleCompetitorData(CommonSchedule commonSchedule) {
        MatchSchedule schedue = (MatchSchedule) commonSchedule;
        Set<Match.Competitor> competitors = new HashSet<Match.Competitor>();
        Match.Competitor homeCompetitor = new Match.Competitor();
        homeCompetitor.setCompetitorId(SbdsInternalApis.getTeamByPartnerIdAndPartnerType(schedue.getHomeTeam().get(0).getTeamId(), 469).getId());
        homeCompetitor.setGround(GroundType.HOME);
        homeCompetitor.setType(CompetitorType.TEAM);
        Match.Competitor awayCompetitor = new Match.Competitor();
        awayCompetitor.setCompetitorId((SbdsInternalApis.getTeamByPartnerIdAndPartnerType(schedue.getAwayTeam().get(0).getTeamId(), 469).getId()));
        awayCompetitor.setGround(GroundType.AWAY);
        awayCompetitor.setType(CompetitorType.TEAM);
        //赛果
        if (schedue.getMatchResult() != null) {
            if (schedue.getMatchResult().get(0).getFirstScore() != null) {
                String[] scoreArr = schedue.getMatchResult().get(0).getFirstScore().split(":");
                homeCompetitor.setFinalResult(scoreArr[0]);
                awayCompetitor.setFinalResult(scoreArr[1]);
            }
        }
        competitors.add(homeCompetitor);
        competitors.add(awayCompetitor);
        return competitors;
    }

    public Long getRoundData(CommonSchedule commonSchedule) {
        MatchSchedule schedule = (MatchSchedule) commonSchedule;
        if (schedule.getRoundName() != null || schedule.getRoundNum() == null) return null;
        List<DictEntry> dicConfig = SbdsInternalApis.getDictEntriesByName("^轮次$");
        if (CollectionUtils.isEmpty(dicConfig)) return null;
        DictEntry dicts = SbdsInternalApis.getDictEntryByNameAndParentId("第" + schedule.getRoundNum() + "轮", dicConfig.get(0).getId());
        if (dicts == null) return null;
        return dicts.getId();
    }

    public Long getStageData(CommonSchedule commonSchedule) {
        MatchSchedule schedule = (MatchSchedule) commonSchedule;
        if (schedule.getRoundName() == null) return null;
        String roundName = SportrardConstants.groupsMap.get(schedule.getRoundName());
        List<DictEntry> dicConfig = SbdsInternalApis.getDictEntriesByName("^阶段");
        if (CollectionUtils.isEmpty(dicConfig)) return null;
        DictEntry dicts = SbdsInternalApis.getDictEntryByNameAndParentId(roundName, dicConfig.get(0).getId());
        if (dicts == null) return null;
        return dicts.getId();
    }

    public Long getGroupData(Long cid, CommonSchedule commonSchedule) {
        MatchSchedule schedule = (MatchSchedule) commonSchedule;
        Competition competition = SbdsInternalApis.getCompetitionById(cid);
        if (competition.getSystem() != MatchSystem.CUP) return null;
        List<DictEntry> dictEntry = SbdsInternalApis.getDictEntriesByName("分组");
        if (CollectionUtils.isEmpty(dictEntry)) return null;
        //TODO int position = file.lastIndexOf(".xml");
        String g = "A";//file.substring(position - 1, position);
        String gname = g + "组";
        DictEntry dc = SbdsInternalApis.getDictEntryByNameAndParentId(gname, dictEntry.get(0).getId());
        if (dc == null) return null;
        return dc.getId();
    }


//    public Set<Match.Competitor> getCompetitorData(Set<Match.Competitor> oldCompetitors, CommonLiveScore liveScore) {
//        Set<Match.Competitor> competitors = new HashSet<Match.Competitor>();
//        for (Match.Competitor currentCompetitor : oldCompetitors) {
//
//            if (currentCompetitor.getGround() == GroundType.HOME) {
//                currentCompetitor.setFinalResult(liveScore.getHomeScore());
//                List<Match.SectionResult> sectionResults = getSectionResults(liveScore, "Home");
//                currentCompetitor.setSectionResults(sectionResults);
//
//            } else {
//                currentCompetitor.setFinalResult(liveScore.getHomeScore());
//                List<Match.SectionResult> sectionResults = getSectionResults(liveScore, "Away");
//                currentCompetitor.setSectionResults(sectionResults);
//            }
//            competitors.add(currentCompetitor);
//        }
//        return competitors;
//    }
//
//    public List<Match.Squad> getSquadData(CommonLiveScore liveScore) {
//        List<Match.Squad> squads = Lists.newArrayList();
//        return squads;
//    }
//
//    public List<Match.SimplePlayer> getSimplePlayersData(Long csid, Long teamId, String formation, List<MatchLiveScore.TeamPlayer> players) {
//        return null;
//    }
//
//    public String getMatchMomentData(CommonLiveScore liveScore) {
//        return null;
//    }
//
//    public Match.CurrentMoment getCurrentMomentData(CommonLiveScore liveScore1) {
//        return null;
//    }
//
//    public Set<Match.CompetitorStat> getCompetitorStatsData(CommonLiveScore liveScore) {
//        return null;
//    }
//
//    public Boolean updateMatchAction(CommonLiveScore liveScore) {
//        return true;
//    }
//
//    public List<Match.SectionResult> getSectionResults(CommonLiveScore liveScore, String type) {
//        return null;
//    }
//
//    public List<LangString> getMultiLang(String value) {
//        List<LangString> langStrings = Lists.newArrayList();
//        if (null != value) {
//            LangString cn = new LangString();
//            cn.setLanguage(LanguageCode.ZH_CN);
//            cn.setValue(value);
//            langStrings.add(cn);
//            LangString hk = new LangString();
//            hk.setLanguage(LanguageCode.ZH_HK);
//            hk.setValue(HanLP.convertToTraditionalChinese(value));
//            langStrings.add(hk);
//        }
//        return langStrings;
//    }

    public List<CountryCode> getAllowCountries() {
        List<CountryCode> countryCodes = Lists.newArrayList();
        countryCodes.add(CountryCode.CN);
        return countryCodes;
    }
}


