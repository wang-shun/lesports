package com.lesports.sms.data.adapter.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.sbd.*;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.qmt.sbd.api.common.TimeSort;
import com.lesports.qmt.sbd.model.*;
import com.lesports.qmt.sbd.model.Competition;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.qmt.sbd.model.MatchStats;
import com.lesports.qmt.sbd.model.Player;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultLiveScore;
import com.lesports.sms.data.process.impl.DefaultLiveStatsProcessor;
import com.lesports.sms.data.process.impl.DefaultPlayerProcessor;
import com.lesports.sms.data.process.impl.DefultLiveScoreProcessor;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("liveScoreAdaptor")
public class DefaultLiveScoreAdaptor extends DefualtAdaptor {

    private static Logger LOG = LoggerFactory.getLogger(DefaultLiveScoreAdaptor.class);
    @Resource
    private DefultLiveScoreProcessor liveScoreProcessor;
    @Resource
    private DefaultLiveStatsProcessor liveStatsProcessor;
    @Resource
    private DefaultPlayerProcessor playerProcessor;

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultLiveScore.LiveModel liveModel = (DefaultLiveScore.LiveModel) object;
            Match match1 = SbdMatchInternalApis.getMatchByPartner(getPartner(liveModel.getPartnerId(), partnerType));
            if (match1 == null) {
                LOG.warn("Match：{},Missed", liveModel.getPartnerId());
                return false;
            }
            Match currentMatch = convertModelToMatch(match1.getGameFType(), partnerType, liveModel);
            if (currentMatch == null) {
                LOG.warn("Match,partnerId:{},partnerType:{},Adaptor fail", liveModel.getPartnerId(), partnerType.toString());
                return false;
            }
            currentMatch.setId(match1.getId());
            liveScoreProcessor.process(currentMatch);
            MatchStats currentMatchStats = convertModelToMatchStat(partnerType, match1.getId(), liveModel);
            if (currentMatchStats != null) liveStatsProcessor.process(currentMatchStats);
            return true;
        } catch (Exception e) {
            LOG.error("adapter liveScore error", e);
        }
        return false;
    }

    private Match convertModelToMatch(Long gameFType, PartnerType partnerType, DefaultLiveScore.LiveModel matchLiveScore) {
        Match match = new Match();
        Team team1 = SbdTeamInternalApis.getTeamByPartner(getPartner(matchLiveScore.getHomeCompetitorId(), partnerType));
        Team team2 = SbdTeamInternalApis.getTeamByPartner(getPartner(matchLiveScore.getAwayCompetitorId(), partnerType));
        if (null == team1 || null == team2) {
            LOG.warn("the team not found,partner1Id:{},partner2Id:{}", matchLiveScore.getHomeCompetitorId(), matchLiveScore.getAwayCompetitorId());
            return null;
        }
        Match.Competitor homeCompetitor = new Match.Competitor();
        homeCompetitor.setCompetitorId(team1.getId());
        homeCompetitor.setFinalResult(matchLiveScore.getHomeCompetitorScore());
        homeCompetitor.setSectionResults(getSectionResults(matchLiveScore.getHomeCompetitorSectionScore()));
        homeCompetitor.setGround(com.lesports.qmt.sbd.api.common.GroundType.HOME);
        homeCompetitor.setType(CompetitorType.TEAM);
        Match.Competitor awayCompetitor = new Match.Competitor();
        awayCompetitor.setCompetitorId(team2.getId());
        awayCompetitor.setFinalResult(matchLiveScore.getAwayCompetitorScore());
        awayCompetitor.setSectionResults(getSectionResults(matchLiveScore.getAwayCompetitorSectionScore()));
        awayCompetitor.setGround(com.lesports.qmt.sbd.api.common.GroundType.AWAY);
        awayCompetitor.setType(CompetitorType.TEAM);
        match.setStatus(matchLiveScore.getStatus());
        Set competitorSet = Sets.newHashSet();
        competitorSet.add(homeCompetitor);
        competitorSet.add(awayCompetitor);
        match.setCompetitors(competitorSet);
        if (matchLiveScore.getSectionId() != null && matchLiveScore.getSectionId() > 0) {
            Match.CurrentMoment currentMoment = new Match.CurrentMoment();
            com.lesports.qmt.config.model.DictEntry dictEntry = QmtConfigDictInternalApis.getDictById(gameFType);
            currentMoment.setSection(matchLiveScore.getSectionId());
            currentMoment.setTime(matchLiveScore.getSectionTime());
            if (dictEntry != null && dictEntry.getName().equals("篮球")) {
                currentMoment.setSort(TimeSort.DESC);
            } else {
                currentMoment.setSort(TimeSort.ASC);
            }
            match.setCurrentMoment(currentMoment);
        }
        return match;
    }

    private MatchStats convertModelToMatchStat(PartnerType partnerType, Long matchId, DefaultLiveScore.LiveModel matchLiveScore) {
        Match currentMatch = SbdMatchInternalApis.getMatchById(matchId);
        if (currentMatch == null) return null;
        Competition currentCompetition = SbdCompetitionInternalApis.getCompetitionById(currentMatch.getCid());
        MatchStats matchStat = new MatchStats();
        com.lesports.qmt.sbd.model.Team team1 = SbdTeamInternalApis.getTeamByPartner(getPartner(matchLiveScore.getHomeCompetitorId(), partnerType));
        com.lesports.qmt.sbd.model.Team team2 = SbdTeamInternalApis.getTeamByPartner(getPartner(matchLiveScore.getAwayCompetitorId(), partnerType));
        if (null == team1 || null == team2) {
            LOG.warn("the team not found,partner1Id:{},partner2Id:{}", matchLiveScore.getHomeCompetitorId(), matchLiveScore.getAwayCompetitorId());
            return null;
        }
        Set<MatchStats.CompetitorStat> competitorStats = Sets.newHashSet();
        List<MatchStats.Squad> squads = Lists.newArrayList();
        competitorStats.add(ConvertModelToCompetitorStat(team1, matchLiveScore.getHomeCompetitorStat()));
        competitorStats.add(ConvertModelToCompetitorStat(team2, matchLiveScore.getAwayCompetitorStat()));
        squads.add(ConvertModelToSquad(currentCompetition, partnerType, team1.getId(), matchLiveScore.getHomeLineUpForamtion(), matchLiveScore.getHomeLineUp()));
        squads.add(ConvertModelToSquad(currentCompetition, partnerType, team2.getId(), matchLiveScore.getAwayLineUpFormation(), matchLiveScore.getAwayLineUp()));
        matchStat.setCompetitorStats(competitorStats);
        matchStat.setSquads(squads);
        matchStat.setDeleted(false);
        matchStat.setAllowCountries(currentCompetition.getAllowCountries());
        matchStat.setId(matchId);
        return matchStat;
    }

    private MatchStats.CompetitorStat ConvertModelToCompetitorStat(com.lesports.qmt.sbd.model.Team team1, Map<String, Object> stats) {
        MatchStats.CompetitorStat CompetitorStat = new MatchStats.CompetitorStat();
        CompetitorStat.setCompetitorId(team1.getId());
        CompetitorStat.setCompetitorType(com.lesports.qmt.sbd.api.common.CompetitorType.TEAM);
        CompetitorStat.setStats(stats);
        return CompetitorStat;
    }

    private MatchStats.Squad ConvertModelToSquad(Competition competition, PartnerType partnerType, Long tid, String formation, List<DefaultLiveScore.LiveModel.SquadPlayerModel> players) {
        MatchStats.Squad squad = new MatchStats.Squad();
        squad.setTid(tid);
        squad.setFormation(formation);
        List<MatchStats.SimplePlayer> simplePlayers = Lists.newArrayList();
        for (DefaultLiveScore.LiveModel.SquadPlayerModel playerModel : players) {
            MatchStats.SimplePlayer simplePlayer = new MatchStats.SimplePlayer();
            Player currentPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(playerModel.getPlayerId(), partnerType));
            if (currentPlayer == null) {
                playerProcessor.process(convertModelToPlayer(partnerType, competition, playerModel));
                currentPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(playerModel.getPlayerId(), partnerType));
            }
            simplePlayer.setPid(currentPlayer.getId());
            simplePlayer.setNumber(playerModel.getNumber());
            simplePlayer.setSquadOrder(playerModel.getSquadOrder());
            simplePlayer.setPosition(currentPlayer.getPosition());
            simplePlayer.setDnp(playerModel.getDnp());
            simplePlayer.setIsOnCourt(playerModel.getIsOnCourt());
            simplePlayer.setStarting(playerModel.getStarting());
            simplePlayer.setStats(playerModel.getStats());
            simplePlayers.add(simplePlayer);
        }
        squad.setPlayers(simplePlayers);
        return squad;
    }

    private List<Match.SectionResult> getSectionResults(Map<Integer, Match.SectionResult> sectionScores) {
        List<Match.SectionResult> results = Lists.newArrayList();
        if (MapUtils.isEmpty(sectionScores)) return results;
        Set<Map.Entry<Integer, Match.SectionResult>> maps = sectionScores.entrySet();
        for (Map.Entry<Integer, Match.SectionResult> currentEntry : maps) {
            results.add(currentEntry.getValue());
        }
        return results;
    }

    private Player convertModelToPlayer(PartnerType partnerType, Competition competition, DefaultLiveScore.LiveModel.SquadPlayerModel playerModel) {
        Player player = new Player();
        player.setCids(Sets.newHashSet(competition.getId()));
        player.setPartners(Lists.newArrayList(getPartner(playerModel.getPlayerId(), partnerType)));
        player.setName(StringUtils.isBlankOrNull(playerModel.getPlayerName()) ? playerModel.getEngLishName() : playerModel.getPlayerName());
        player.setMultiLangNames(getMultiLang(player.getName()));
        player.setAllowCountries(competition.getAllowCountries());
        player.setOnlineLanguages(competition.getOnlineLanguages());
        return player;
    }


}


