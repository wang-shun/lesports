package com.lesports.sms.data.adapter.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.qmt.sbd.SbdCompetitionInternalApis;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.model.*;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import com.lesports.sms.data.process.impl.DefaultPlayerProcessor;
import com.lesports.sms.data.process.impl.DefaultTeamProcessor;
import com.lesports.sms.data.process.impl.DefultTeamSeasonProcessor;
import com.lesports.sms.data.util.CommonUtil;
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
            //  writePlayerToExcel(teamSeasonModel);
            CompetitionSeason currentCompetitionSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid);
            if (currentCompetitionSeason == null) {
                LOG.warn("competitionSeason:{} missed", csid);
                return false;
            }
            Competition currentCompetition = SbdCompetitionInternalApis.getCompetitionById(currentCompetitionSeason.getCid());
            Long tid = 0L;
            Long coachId = 0L;
            if (teamSeasonModel != null) {
                teamProcessor.process(convertModelToTeam(currentCompetition, csid, partnerType, teamSeasonModel));
                Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(teamSeasonModel.getPartnerId(), partnerType));
                if (currentTeam == null) {
                    LOG.error("team deal fial", teamSeasonModel.getPartnerId());
                    return false;
                }
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
        currentTeam.setGameFType(competition.getGameFType());
        currentTeam.setPartners(Lists.newArrayList(getPartner(teamModel.getPartnerId(), partnerType)));
        currentTeam.setMultiLangNames(getMultiLang(teamModel.getName()));
        currentTeam.setRanks(convertModelToRank(teamModel.getRankList()));
        currentTeam.setHonors(convertModelToHonour(teamModel.getHonoursList()));
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
        Set cids = Sets.newHashSet();
        cids.add(competition.getId());
        currentPlayer.setCids(cids);
        currentPlayer.setPartners(Lists.newArrayList(getPartner(playerModel.getPartnerId(), partnerType)));
        currentPlayer.setAllowCountries(competition.getAllowCountries());
        currentPlayer.setName(playerModel.getPlayerName() == null ? playerModel.getPlayerEnName() : playerModel.getPlayerName());
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
        currentPlayer.setAllowCountries(competition.getAllowCountries());
        currentPlayer.setOnlineLanguages(competition.getOnlineLanguages());
        return currentPlayer;
    }

    private List<Team.Rank> convertModelToRank(List<DefaultTeamSeason.TeamModel.RankModel> teamRanks) {
        List<Team.Rank> ranks = Lists.newArrayList();
        for (DefaultTeamSeason.TeamModel.RankModel rankModel : teamRanks) {
            Team.Rank teamRank = new Team.Rank();
            teamRank.setRank(CommonUtil.parseInt(rankModel.getRankNum(), 0));
            teamRank.setTime(rankModel.getSeason());
            ranks.add(teamRank);
        }
        return ranks;
    }

    private List<String> convertModelToHonour(List<DefaultTeamSeason.TeamModel.HonourModel> honourModels) {
        List<String> ranks = Lists.newArrayList();
        for (DefaultTeamSeason.TeamModel.HonourModel honourModel : honourModels) {
            String hounr = honourModel.getSeason() + " " + honourModel.getType();
            ranks.add(hounr);
        }
        return ranks;
    }
//    private void writePlayerToExcel(DefaultTeamSeason.TeamModel teamModel) {
//
//        try {
//            Workbook book;
//            WritableWorkbook wwb;
//
//            String fileName = "D://ChineseSuperLeaguePlayers.xls";
//            File file = new File(fileName);
//
//            if (file.exists()) {
//                //如果文件存在;
//                book = Workbook.getWorkbook(file);
//                wwb = Workbook.createWorkbook(file, book);
//            } else {
//                wwb = Workbook.createWorkbook(file);
//            }
//            // 创建可写入的Excel工作簿
//            //以fileName为文件名来创建一个Workbook
//            // 创建工作表
//            WritableSheet ws = wwb.getSheet("中超球队");
//            if (null == ws) {
//                ws = wwb.createSheet("NBA球队", 0);
//            }
//            Constants.j++;
//            //要插入到的Excel表格的行号，默认从0开始
//            Label labelname = new Label(0, 0, "运动员QQID");//表示第
//            Label labelId = new Label(1, 0, "运动员名称");//表示第
//            Label image = new Label(2, 0, "运动员英文名");
//            Label image_name = new Label(3, 0, "球队名称");
//            ws.addCell(labelname);
//            ws.addCell(labelId);
//            ws.addCell(image);
//            ws.addCell(image_name);
//            try {
//                Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(teamModel.getPartnerId(),PartnerType.SPORTRADAR));
//
//                for (DefaultTeamSeason.TeamModel.PlayerModel playerModel : teamModel.getPlayerModels()) {
//                    Constants.j++;
//                    Label labelname_i = new Label(0, Constants.j, playerModel.getPartnerId());
//                    Label labelId_i = new Label(1, Constants.j, playerModel.getPlayerName());
//                    Label image_i = new Label(2, Constants.j, playerModel.getPlayerEnName());
//                    Label image_name_i = new Label(3, Constants.j, currentTeam.getName());
//                    ws.addCell(labelname_i);
//                    ws.addCell(labelId_i);
//                    ws.addCell(image_i);
//                    ws.addCell(image_name_i);
//                }
//            } catch (Exception e) {
//
//            }
//            //写进文档
//            wwb.write();
//            // 关闭Excel工作簿对象
//            wwb.close();
//            System.out.print("NNNNNNNNNN");
//
//        } catch (
//                Exception e
//                )
//
//        {
//            e.printStackTrace();
//        }
//    }



}
