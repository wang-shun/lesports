package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.qmt.sbd.SbdCompetitionInternalApis;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.model.*;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import com.lesports.sms.data.process.impl.DefaultPlayerProcessor;
import com.lesports.sms.data.process.impl.DefaultTeamProcessor;
import com.lesports.sms.data.process.impl.DefultTeamSeasonProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("teamSeasonAdaptor")
public class DefaultTeamSeasonAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultTeamSeasonAdaptor.class);
    @Resource
    DefultTeamSeasonProcessor teamSeasonProcessor;
    @Resource
    DefaultTeamProcessor teamProcessor;
    @Resource
    DefaultPlayerProcessor playerProcessor;

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultTeamSeason.TeamModel teamSeasonModel = (DefaultTeamSeason.TeamModel) object;
            Competition currentCompetition = SbdCompetitionInternalApis.getCompetitionById(SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid).getCid());
            if (currentCompetition == null) {
                LOG.warn("Competition:{},and competitionSeason:{} missed", currentCompetition.getId(), csid);
                return false;
            }
            Long tid = 0L;
            Long coachId = 0L;
            if (teamSeasonModel != null) {
                teamProcessor.process(convertModelToTeam(currentCompetition, csid, partnerType, teamSeasonModel));
                tid = SbdTeamInternalApis.getTeamByPartner(getPartner(teamSeasonModel.getPartnerId(), partnerType)).getId();
            }
            if (teamSeasonModel.getCoachId() != null && teamSeasonModel.getCoachName() != null) {
                playerProcessor.process(convertModelToCoach(teamSeasonModel.getCoachName(), teamSeasonModel.getCoachId(), currentCompetition, csid, partnerType));
                coachId = SbdPlayerInternalApis.getPlayerByPartner(getPartner(teamSeasonModel.getCoachId(), partnerType)).getId();
            }
            Long currentCaptiainId = 0L;
            List<TeamSeason.TeamPlayer> teamPlayers = Lists.newArrayList();
            List<Long> corePlayers = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(teamSeasonModel.getPlayerModels())) {
                for (DefaultTeamSeason.TeamModel.PlayerModel playerModel : teamSeasonModel.getPlayerModels()) {
                    if (playerProcessor.process(converPlayerModelToPlayer(currentCompetition, csid, partnerType, playerModel))) {
                        Player currentPlayer = SbdPlayerInternalApis.getPlayerByPartner(getPartner(playerModel.getPartnerId(), partnerType));
                        if (currentPlayer == null) {
                            LOG.warn("player:{} is not exists", playerModel.getPartnerId());
                            continue;
                        }
                        Long pid = currentPlayer.getId();
                        if (playerModel.isCorePlayer()) corePlayers.add(pid);
                        if (playerModel.isCaptain()) currentCaptiainId = pid;
                        TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                        teamPlayer.setNumber(playerModel.getShirtName());
                        teamPlayer.setPid(pid);
                        teamPlayers.add(teamPlayer);
                    }
                }
            }
            TeamSeason teamSeason1 = new TeamSeason();
            teamSeason1.setTid(tid);
            teamSeason1.setCsid(csid);
            teamSeason1.setCoachId(coachId);
            teamSeason1.setCorePlayers(corePlayers);
            teamSeason1.setCurrentCaptain(currentCaptiainId);
            teamSeason1.setPlayers(teamPlayers);
            return teamSeasonProcessor.process(teamSeason1);
        } catch (Exception e) {
            LOG.error("adaptor teamSeason fail", e);
        }
        return false;
    }

    private Team convertModelToTeam(Competition competition, Long csid, PartnerType partnerType, DefaultTeamSeason.TeamModel teamModel) {
        Team currentTeam = new Team();
        Set<Long> cids = Sets.newHashSet();
        cids.add(competition.getId());
        currentTeam.setCids(cids);
        currentTeam.setName(teamModel.getName());
        currentTeam.setNickname(teamModel.getName());
        currentTeam.setPartners(Lists.newArrayList(getPartner(teamModel.getPartnerId(), partnerType)));
        currentTeam.setMultiLangNames(getMultiLang(teamModel.getName()));
        currentTeam.setOnlineLanguages(competition.getOnlineLanguages());
        currentTeam.setAllowCountries(competition.getAllowCountries());
        return currentTeam;
    }

    private Player convertModelToCoach(String coachName, String coachId, Competition competitionSeason, Long csid, PartnerType partnerType) {
        Player currentCoach = new Player();
        Set<Long> cids = Sets.newHashSet();
        cids.add(competitionSeason.getId());
        currentCoach.setCids(cids);
        currentCoach.setPartners(Lists.newArrayList(getPartner(coachId, partnerType)));
        currentCoach.setName(coachName);
        currentCoach.setMultiLangNames(getMultiLang(coachName));
        currentCoach.setAllowCountries(competitionSeason.getAllowCountries());
        return currentCoach;
    }

    private Player converPlayerModelToPlayer(Competition competition, Long csid, PartnerType partnerType, DefaultTeamSeason.TeamModel.PlayerModel playerModel) {
        Player currentPlayer = new Player();
        Set cids = Sets.newConcurrentHashSet();
        cids.add(competition.getId());
        currentPlayer.setCids(cids);
        currentPlayer.setPartners(Lists.newArrayList(getPartner(playerModel.getPartnerId(), partnerType)));
        currentPlayer.setAllowCountries(competition.getAllowCountries());
        currentPlayer.setName(playerModel.getPlayerName());
        currentPlayer.setMultiLangNames(getMultiLang(playerModel.getPlayerName()));
        currentPlayer.setCountryId(playerModel.getCounntry());
        currentPlayer.setGameFType(competition.getGameFType());
        currentPlayer.setGender(playerModel.getGender());
        currentPlayer.setHeight(playerModel.getHeight());
        currentPlayer.setWeight(playerModel.getWeight());
        currentPlayer.setPosition(playerModel.getPosition());
        currentPlayer.setHeavyFoot(playerModel.getHeavyFoot());
        currentPlayer.setSalary(playerModel.getSalary());
        currentPlayer.setDraft(playerModel.getDraftContent());
        return currentPlayer;

    }


}
