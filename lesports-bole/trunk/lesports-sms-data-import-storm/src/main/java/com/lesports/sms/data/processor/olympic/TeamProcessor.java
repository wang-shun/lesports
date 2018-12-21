package com.lesports.sms.data.processor.olympic;

import com.google.common.collect.Sets;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.model.olympic.ParticipantTeam;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
@Component
public class TeamProcessor extends AbstractProcessor implements BeanProcessor<ParticipantTeam> {
    private static Logger LOG = LoggerFactory.getLogger(ParticipantProcessor.class);

    @Override
    public Boolean process(String fileType, ParticipantTeam obj) {
        try {
            if (obj == null || obj.getCode() == null || obj.getSportType() == null)
                return false;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.OG_CID);
            if (competitionSeason == null) {
                LOG.warn(" the right season competition is not exits : {}.", obj.getCompetitionCode());
                return false;
            }
            DictEntry discipline = getCommonDictWithCache(Constants.DICT_NAME_DISCIPLINE, obj.getSportType().substring(0, 2) + "$");
            if (discipline == null) {
                LOG.warn("will not handle this, can not get game first type {},  code : {}.", obj.getSportType(), obj.getCode());
                return false;
            }
            DictEntry gameFTypeDict = getCommonDictWithCache(Constants.DICT_NAME_SPORT, discipline.getCode().substring(0, 2) + "$");
            if (gameFTypeDict == null) return false;
            Long gameFType = gameFTypeDict.getId();
            Team currentTeam = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(obj.getCode(), Constants.partner_typeS);
            if (null == currentTeam) {
                currentTeam = new Team();
                currentTeam.setName(obj.getName());
                currentTeam.setOnlineLanguages(getOnlineLang());
                currentTeam.setMultiLangNames(getMultiLang(currentTeam.getName()));
                currentTeam.setMultiLangDesc(getMultiLang(currentTeam.getDesc()));
                currentTeam.setMultiLangAbbrs(getMultiLang(currentTeam.getAbbreviation()));
                currentTeam.setMultiLangCities(getMultiLang(currentTeam.getCity()));
                currentTeam.setMultiLangHomeGrounds(getMultiLang(currentTeam.getHomeGround()));
                currentTeam.setMultiLangNicknames(getMultiLang(currentTeam.getNickname()));
                currentTeam.setMultiLangOctopusNames(getMultiLang(currentTeam.getOctopusName()));
                Set<Long> cids = Sets.newHashSet();
                cids.add(Constants.OG_CID);
                currentTeam.setCids(cids);
            }
            currentTeam.setGameFType(gameFType);
            currentTeam.setPartnerId(obj.getCode());
            currentTeam.setPartnerType(Constants.partner_typeS);
            currentTeam.setCountryId(getCommonDictWithCache(Constants.DICT_NAME_COUNTRY, obj.getOrganisation()).getId());
            SbdsInternalApis.saveTeam(currentTeam);
            LOG.info("team is saved successfully : {}.", obj.getCode());
            //处理赛季信息
           Team currentTeamNew = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(obj.getCode(), Constants.partner_type);
           updateTeamSeason(currentTeamNew.getId(), competitionSeason.getId(), obj.getPlayers());
            return true;
        } catch (Exception e) {
            LOG.warn("team is parse fail : {}, {}", obj.getCode(), e.getMessage(), e);
            return false;
        }

    }

    private void updateTeamSeason(Long teamId, Long csid, List<ParticipantTeam.TeamPlayer> players) {
        //更新赛季对象
        TeamSeason teamSeason = null;
        List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
        if ( CollectionUtils.isNotEmpty(teamSeasons)) {
            teamSeason = teamSeasons.get(0);
        } else {
            teamSeason = new TeamSeason();
            teamSeason.setCsid(csid);
            teamSeason.setTid(teamId);
        }
        //添加球员
        if (players == null || players.isEmpty()) return;
        List<TeamSeason.TeamPlayer> tTeamPlayers = new ArrayList<>(players.size());
        for (ParticipantTeam.TeamPlayer teamPlayer : players) {
            Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(teamPlayer.getPlayerCode(), Constants.partner_type);
            if (player == null) continue;
            TeamSeason.TeamPlayer tTeamPlayer = new TeamSeason.TeamPlayer();
            tTeamPlayer.setNumber(teamPlayer.getPlayerOrder());
            tTeamPlayer.setPid(player.getId());
            tTeamPlayers.add(tTeamPlayer);
        }
        teamSeason.setPlayers(tTeamPlayers);
        SbdsInternalApis.saveTeamSeason(teamSeason);

    }

}

