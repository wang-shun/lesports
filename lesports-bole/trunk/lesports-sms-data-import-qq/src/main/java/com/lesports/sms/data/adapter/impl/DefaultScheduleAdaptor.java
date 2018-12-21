package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultSchedule;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import com.lesports.sms.data.process.impl.DefaultScheduleProcessor;
import com.lesports.sms.data.process.impl.DefaultTeamProcessor;
import com.lesports.sms.model.Competition;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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

    public Boolean nextProcessor(TransModel transModel, Object object) {
        try {
            DefaultSchedule.ScheduleModel scheduleModel = (DefaultSchedule.ScheduleModel) object;
            //writeTeamsToExcel(scheduleModel);
            CompetitionSeason curentCompetitionSeason = SbdsInternalApis.getCompetitionSeasonById(transModel.getCsid());
            if (curentCompetitionSeason == null) {
                LOG.warn("CompetitionSeason:{} can not be found,the csid:{}", transModel.getCsid());
                return false;
            }
            Competition competition = SbdsInternalApis.getCompetitionById(curentCompetitionSeason.getCid());
            if (competition == null) {
                LOG.warn("Competition:{} can not be found,the sid:{}", curentCompetitionSeason.getCid());
                return false;
            }
            //get the database model of schedule
            Match currentMatch = convertModelToMatch(competition, transModel.getCsid(), scheduleModel);
            if (currentMatch == null) {
                LOG.warn("currentMatch:{} adpter fail,the sid:{}", scheduleModel.toString());
                return false;
            }
            return scheduleProcessor.process(currentMatch);
        } catch (Exception e) {
            LOG.warn("schedule Adapt fail", e);
            return false;
        }
    }


    // convert the default model to match
    private Match convertModelToMatch(Competition competition, Long csid, DefaultSchedule.ScheduleModel scheduleModel) {
        //init the basic info of match
        Match match = new Match();
        Team homeTeam = getTeam(scheduleModel.getHomeTeamId(), scheduleModel.getHomeTeamName());
        Team awayTeam = getTeam(scheduleModel.getAwayTeamId(), scheduleModel.getAwayTeamName());
        if (homeTeam == null || awayTeam == null) {
            LOG.warn("Team is null,homeTeam:{},awayTeam:{}", scheduleModel.getHomeTeamId(), scheduleModel.getAwayTeamId());
            return null;
        }
        match.setCid(competition.getId());
        match.setCsid(csid);
        match.setStatus(scheduleModel.getStatus());
        match.setQQId(scheduleModel.getPartnerId());
        match.setName(competition.getName() + " " + homeTeam.getName() + " vs " + awayTeam.getName());
        match.setMultiLangNames(getMultiLang(match.getName()));
        match.setOnlineLanguages(competition.getOnlineLanguages());
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
        homeCompetitor.setType(CompetitorType.TEAM);
        homeCompetitor.setGround(GroundType.HOME.HOME);
        homeCompetitor.setFinalResult(scheduleModel.getHomeScore());
        competitorSet.add(homeCompetitor);
        Match.Competitor awayCompetitor = new Match.Competitor();
        awayCompetitor.setCompetitorId(awayTeam.getId());
        awayCompetitor.setType(CompetitorType.TEAM);
        awayCompetitor.setGround(GroundType.AWAY);
        awayCompetitor.setFinalResult(scheduleModel.getHomeScore());
        competitorSet.add(awayCompetitor);
        match.setCompetitors(competitorSet);
        return match;
    }

    private Team getTeam(String partnerId, String name) {
        Team currentTeam = SbdsInternalApis.getTeamByQQId(partnerId);
        if (currentTeam != null) return currentTeam;
        else {
            currentTeam = new Team();
            currentTeam.setQQId(partnerId);
            currentTeam.setName(name);
            teamProcessor.process(currentTeam);
            currentTeam = SbdsInternalApis.getTeamByQQId(partnerId);
            if (currentTeam != null) return currentTeam;
        }
        return null;
    }

    private void writeTeamsToExcel(DefaultSchedule.ScheduleModel teamModel) {

        try {
            Workbook book;
            WritableWorkbook wwb;

            String fileName = "D://CBATeams.xls";
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
            WritableSheet ws = wwb.getSheet("CBA球队");
            if (null == ws) {
                ws = wwb.createSheet("CBA球队", 0);
            }
            //要插入到的Excel表格的行号，默认从0开始
            Label labelname = new Label(0, 0, "球队QQID");//表示第
            Label labelId = new Label(1, 0, "球队名称");//表示第

            ws.addCell(labelname);
            ws.addCell(labelId);

            try {
                Constants.j++;
                Label labelname_i = new Label(0, Constants.j, teamModel.getHomeTeamId());
                Label labelId_i = new Label(1, Constants.j, teamModel.getHomeTeamName());

                ws.addCell(labelname_i);
                ws.addCell(labelId_i);
                Constants.j++;
                Label labelname_m = new Label(0, Constants.j, teamModel.getAwayTeamId());
                Label labelId_m = new Label(1, Constants.j, teamModel.getAwayTeamName());

                ws.addCell(labelname_m);
                ws.addCell(labelId_m);

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
