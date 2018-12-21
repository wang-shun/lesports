package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import com.lesports.sms.data.process.impl.DefaultPlayerProcessor;
import com.lesports.sms.data.process.impl.DefaultTeamProcessor;
import com.lesports.sms.data.process.impl.DefultTeamSeasonProcessor;
import com.lesports.sms.model.Competition;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Iterator;
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

    public Boolean nextProcessor(TransModel transModel, Object object) {
        DefaultTeamSeason.TeamModel teamSeasonModel = (DefaultTeamSeason.TeamModel) object;
        //  writePlayerToExcel(teamSeasonModel);
        try {
            Competition currentCompetition = SbdsInternalApis.getCompetitionById(SbdsInternalApis.getCompetitionSeasonById(transModel.getCsid()).getCid());
            if (currentCompetition == null) {
                LOG.warn("Competition:{},and competitionSeason:{} missed", currentCompetition.getId(), transModel.getCsid());
                return false;
            }
            Long tid = 0L;
            Long coachId = 0L;
            if (teamSeasonModel != null) {
                teamProcessor.process(convertModelToTeam(currentCompetition, transModel.getCsid(), teamSeasonModel));
                tid = SbdsInternalApis.getTeamByQQId(teamSeasonModel.getPartnerId()).getId();
            }
            if (teamSeasonModel.getCoachId() != null && teamSeasonModel.getCoachName() != null) {
                playerProcessor.process(convertModelToCoach(teamSeasonModel.getCoachName(), teamSeasonModel.getCoachId(), currentCompetition, transModel.getCsid()));
                coachId = SbdsInternalApis.getPlayerByQQId(teamSeasonModel.getCoachId()).getId();
            }
            List<TeamSeason.TeamPlayer> teamPlayers = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(teamSeasonModel.getPlayerModels())) {
                for (DefaultTeamSeason.TeamModel.PlayerModel playerModel : teamSeasonModel.getPlayerModels()) {
                    if (playerProcessor.process(converPlayerModelToPlayer(currentCompetition, transModel.getCsid(), playerModel))) {
                        Player currentPlayer = SbdsInternalApis.getPlayerByQQId(playerModel.getPartnerId());
                        Long pid = currentPlayer.getId();
                        TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                        teamPlayer.setNumber(Long.valueOf(playerModel.getShirtName()));
                        teamPlayer.setPid(pid);
                        teamPlayers.add(teamPlayer);
                    }
                }
            }
            TeamSeason teamSeason1 = new TeamSeason();
            teamSeason1.setTid(tid);
            teamSeason1.setCsid(transModel.getCsid());
            teamSeason1.setCoachId(coachId);
            teamSeason1.setPlayers(teamPlayers);
            return teamSeasonProcessor.process(teamSeason1);
        } catch (Exception e) {
            LOG.error("adaptor teamseason,fail", e);
        }
        return false;
    }

    private Team convertModelToTeam(Competition competition, Long csid, DefaultTeamSeason.TeamModel teamModel) {
        Team currentTeam = new Team();
        Set<Long> cids = Sets.newHashSet();
        cids.add(competition.getId());
        currentTeam.setCids(cids);
        currentTeam.setName(teamModel.getName());
        currentTeam.setNickname(teamModel.getName());
        currentTeam.setQQId(teamModel.getPartnerId());
        currentTeam.setMultiLangNames(getMultiLang(teamModel.getName()));
        currentTeam.setOnlineLanguages(competition.getOnlineLanguages());
        currentTeam.setAllowCountries(competition.getAllowCountries());
        return currentTeam;
    }

    private Player convertModelToCoach(String coachName, String coachId, Competition competitionSeason, Long csid) {
        Player currentCoach = new Player();
        Set<Long> cids = Sets.newHashSet();
        cids.add(competitionSeason.getId());
        currentCoach.setCids(cids);
        currentCoach.setQQId(coachId);
        currentCoach.setName(coachName);
        currentCoach.setMultiLangNames(getMultiLang(coachName));
        currentCoach.setAllowCountries(competitionSeason.getAllowCountries());
        return currentCoach;
    }

    private Player converPlayerModelToPlayer(Competition competition, Long csid, DefaultTeamSeason.TeamModel.PlayerModel playerModel) {
        Player currentPlayer = new Player();
        Set cids = Sets.newConcurrentHashSet();
        cids.add(competition.getId());
        currentPlayer.setCids(cids);
        currentPlayer.setQQId(playerModel.getPartnerId());
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

    private void writePlayerToExcel(DefaultTeamSeason.TeamModel teamModel) {

        try {
            Workbook book;
            WritableWorkbook wwb;

            String fileName = "D://NBAPlayers.xls";
            File file = new File(fileName);

            if (file.exists()) {
                //如果文件存在;
                book = Workbook.getWorkbook(file);
                wwb = Workbook.createWorkbook(file, book);
            } else {
                wwb = Workbook.createWorkbook(file);
            }
            // 创建可写入的Excel工作簿
            //以fileName为文件名来创建一个Workbook
            // 创建工作表
            WritableSheet ws = wwb.getSheet("NBA球队");
            if (null == ws) {
                ws = wwb.createSheet("NBA球队", 0);
            }
            Constants.j++;
            //要插入到的Excel表格的行号，默认从0开始
            Label labelname = new Label(0, 0, "运动员QQID");//表示第
            Label labelId = new Label(1, 0, "运动员名称");//表示第
            Label image = new Label(2, 0, "运动员英文名");
            Label image_name = new Label(3, 0, "球队名称");
            ws.addCell(labelname);
            ws.addCell(labelId);
            ws.addCell(image);
            ws.addCell(image_name);
            try {
                Team currentTeam = SbdsInternalApis.getTeamByQQId(teamModel.getPartnerId());

                for (DefaultTeamSeason.TeamModel.PlayerModel playerModel : teamModel.getPlayerModels()) {
                    Constants.j++;
                    Label labelname_i = new Label(0, Constants.j, playerModel.getPartnerId());
                    Label labelId_i = new Label(1, Constants.j, playerModel.getPlayerName());
                    Label image_i = new Label(2, Constants.j, playerModel.getPlayerEnName());
                    Label image_name_i = new Label(3, Constants.j, currentTeam.getName());
                    ws.addCell(labelname_i);
                    ws.addCell(labelId_i);
                    ws.addCell(image_i);
                    ws.addCell(image_name_i);
                }
            } catch (Exception e) {

            }
            //写进文档
            wwb.write();
            // 关闭Excel工作簿对象
            wwb.close();
            System.out.print("NNNNNNNNNN");

        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }
    }


}
