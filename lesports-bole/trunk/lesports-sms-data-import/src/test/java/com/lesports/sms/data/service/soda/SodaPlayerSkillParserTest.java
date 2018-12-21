package com.lesports.sms.data.service.soda;

import com.google.common.collect.Sets;
import com.lesports.AbstractIntegrationTest;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.api.vo.TCompetitionSeason;
import com.lesports.sms.api.vo.TTeamPlayer;
import com.lesports.sms.api.vo.TTeamSeason;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import com.lesports.sms.service.CompetitionSeasonService;
import com.lesports.sms.service.PlayerService;
import com.lesports.sms.service.TeamSeasonService;
import com.lesports.sms.service.TeamService;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.data.domain.Page;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
* Created by zhonglin on 2016/3/9.
*/
public class SodaPlayerSkillParserTest extends AbstractIntegrationTest {

    @Resource
    private SodaPlayerSkillParser sodaPlayerSkillParser;


    @Test
    public void testSodaPlayerSkillParser() {

        System.out.println("ttestSodaPlayerSkillParser begin");
        String name = "5829|5505|5504|8994|5506|5508|3321|5837|11108|8733|7875|456|5773|11093|5791|5827";
        String[] teamIds = name.split("\\|");

        //根据赛事id获取最新的赛季id
        CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(47001);
        if(competitionSeason==null){
            System.out.println("tCompetitionSeason is null");
            return;
        }

        for(String teamId:teamIds){
            Team team = SbdsInternalApis.getTeamBySodaId(teamId);
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), competitionSeason.getId());
            TeamSeason teamSeason = null;
            if(CollectionUtils.isNotEmpty(teamSeasons)){
                teamSeason = teamSeasons.get(0);
            }
            else{
                System.out.println("teamSeasons is null tid: " + team.getId());
            }
            if(teamSeason == null){
                System.out.println("tTeamSeason is null tid: " + team.getId() + " csid: " + competitionSeason.getId());
                continue;
            }

            List<TeamSeason.TeamPlayer> teamPlayers = teamSeason.getPlayers();
            if(CollectionUtils.isNotEmpty(teamPlayers)){
                for(TeamSeason.TeamPlayer teamPlayer:teamPlayers){
                    Player player = SbdsInternalApis.getPlayerById(teamPlayer.getPid());
                    if(player==null || player.getSodaId()==null || !player.getType().equals(PlayerType.PLAYER)){
                        System.out.println("soda player is error tid: " + teamId);
                        continue;
                    }
                    String filePath = "E:\\soda\\2016\\" + "t316-player-skill-" + player.getSodaId() + ".xml";
                    System.out.println("filePath: " + filePath);
                    sodaPlayerSkillParser.parseData(filePath);
                }
            }
            else{
                System.out.println("tTeamPlayers is null tid: " + teamId);
            }
        }
    }

//    @Test
//    public void testOldSodaPlayerSkillParser() {
//
//        System.out.println("ttestSodaPlayerSkillParser begin");
//        String name = "5829|5505|5504|8994|5506|5508|3321|5837|11108|8733|7875|456|5773|11093|5791|5827";
//        String[] teamIds = name.split("\\|");
//
//        //根据赛事id获取最新的赛季id
//        TCompetitionSeason tCompetitionSeason = competitionSeasonService.getLatestTCompetitionSeasonsByCId(47001);
//        if(tCompetitionSeason==null){
//            System.out.println("tCompetitionSeason is null");
//            return;
//        }
//
//        for(String teamId:teamIds){
//            Team team = teamService.getTeamBySodaId(teamId);
//            List<TTeamSeason> teamSeasons = teamSeasonService.getTTeamSeasonByTidAndCsid(team.getId(), tCompetitionSeason.getId());
//            TTeamSeason tTeamSeason = null;
//            if(CollectionUtils.isNotEmpty(teamSeasons)){
//                tTeamSeason = teamSeasons.get(0);
//            }
//
//            if(tTeamSeason == null){
//                System.out.println("tTeamSeason is null tid: " + team.getId() + " csid: " + tCompetitionSeason.getId());
//                continue;
//            }
//
//            List<TTeamPlayer> tTeamPlayers = tTeamSeason.getPlayers();
//            if(CollectionUtils.isNotEmpty(tTeamPlayers)){
//                for(TTeamPlayer tTeamPlayer:tTeamPlayers){
//                    for(int i=2013;i<2016;i++){
//                        Player player = playerService.findOne(tTeamPlayer.getPid());
//                        if(player==null || player.getSodaId()==null || !player.getType().equals(PlayerType.PLAYER)){
//                            System.out.println("soda player is error tid: " + teamId);
//                            continue;
//                        }
//                        String path = "E://soda//2016//";
//                        path = path.replace("2016",i+"");
//                        String filePath = path + "t316-player-skill-" + player.getSodaId() + ".xml";
//                        System.out.println("filePath: " + filePath);
//                        sodaPlayerSkillParser.parseData(filePath);
//                    }
//                }
//            }
//            else{
//                System.out.println("tTeamPlayers is null tid: " + teamId);
//            }
//        }
//    }


}
