package com.lesports.sms.data.adapter.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.TimeSort;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultLiveScore;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import com.lesports.sms.data.process.impl.DefaultLiveStatsProcessor;
import com.lesports.sms.data.process.impl.DefultLiveScoreProcessor;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

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

    public Boolean nextProcessor(TransModel transModel, Object object) {
        try {
            DefaultLiveScore.LiveModel liveModel = (DefaultLiveScore.LiveModel) object;
            distributeSquad(liveModel);
//        writePlayerToExcel(liveModel.getHomeCompetitorId(), liveModel.getHomeLineUp());
//        writePlayerToExcel(liveModel.getAwayCompetitorId(), liveModel.getAwayLineUp());
            Match currentMatch = convertModelToMatch(transModel.getCsid(), transModel.getPartnerId(), liveModel);
            if (currentMatch != null) {
                LOG.info("the match:{} adator is created and send to the nextProcessor", currentMatch.toString());
                return liveScoreProcessor.process(currentMatch);

            }
        } catch (Exception e) {
            LOG.warn("parse match:{},fail", e);
        }
        return false;
    }

    private boolean distributeSquad(DefaultLiveScore.LiveModel currentLiveModel) {
        if (CollectionUtils.isNotEmpty(currentLiveModel.getHomeLineUp()) || CollectionUtils.isNotEmpty(currentLiveModel.getAwayLineUp()) || CollectionUtils.isEmpty(currentLiveModel.getAllLineUp()))
            return true;
        boolean isHome = true;
        boolean isExistSub = false;
        List<DefaultLiveScore.LiveModel.SquadPlayerModel> homeLineUps = Lists.newArrayList();
        List<DefaultLiveScore.LiveModel.SquadPlayerModel> awayLineUps = Lists.newArrayList();
        for (DefaultLiveScore.LiveModel.SquadPlayerModel currentSquadMode : currentLiveModel.getAllLineUp()) {
            if (StringUtils.isEmpty(currentSquadMode.getPlayerId())) continue;
            if (!currentSquadMode.getStarting()) isExistSub = true;
            if (currentSquadMode.getStarting() && isExistSub) isHome = false;
            if (isHome) {
                homeLineUps.add(currentSquadMode);
            } else {
                awayLineUps.add(currentSquadMode);
            }

        }
        currentLiveModel.setHomeLineUp(homeLineUps);
        currentLiveModel.setAwayLineUp(awayLineUps);
        return true;
    }

    private Match convertModelToMatch(Long csid, String partnerId, DefaultLiveScore.LiveModel matchLiveScore) {
        Match match = new Match();
        Team team1 = SbdsInternalApis.getTeamByQQId(matchLiveScore.getHomeCompetitorId());
        Team team2 = SbdsInternalApis.getTeamByQQId(matchLiveScore.getAwayCompetitorId());
        if (null == team1 || null == team2) {
            LOG.warn("the team not found,partner1Id:{},partner2Id:{}", matchLiveScore.getHomeCompetitorId(), matchLiveScore.getAwayCompetitorId());
            return null;
        }
        Match oldMatch = SbdsInternalApis.getMatchByQQId(matchLiveScore.getPartnerId() == null ? partnerId : matchLiveScore.getPartnerId());
        if (oldMatch == null) {
            LOG.warn("MATCH NOT RELAX QQID RETURN;");
            return null;
        }
        match.setId(oldMatch.getId());
        Match.Competitor homeCompetitor = new Match.Competitor();
        homeCompetitor.setCompetitorId(team1.getId());
        homeCompetitor.setFinalResult(matchLiveScore.getHomeCompetitorScore());
        // homeCompetitor.setSectionResults(Lists.transform(matchLiveScore.getHomeCompetitorSectionScore(), new AttachmentTransformer()));
        homeCompetitor.setSectionResults(ConvertMapResultToList(matchLiveScore.getHomeCompetitorSectionScoreMap()));
        homeCompetitor.setGround(GroundType.HOME);
        homeCompetitor.setType(CompetitorType.TEAM);
        Match.Competitor awayCompetitor = new Match.Competitor();
        awayCompetitor.setCompetitorId(team2.getId());
        awayCompetitor.setFinalResult(matchLiveScore.getAwayCompetitorScore());
        //awayCompetitor.setSectionResults(Lists.transform(matchLiveScore.getAwayCompetitorSectionScore(), new AttachmentTransformer()));
        awayCompetitor.setSectionResults(ConvertMapResultToList(matchLiveScore.getAwayCompetitorSectionScoreMap()));
        awayCompetitor.setGround(GroundType.AWAY);
        awayCompetitor.setType(CompetitorType.TEAM);
        match.setStatus(matchLiveScore.getStatus());
        Set competitorSet = Sets.newHashSet();
        competitorSet.add(homeCompetitor);
        competitorSet.add(awayCompetitor);
        match.setCompetitors(competitorSet);
        Match.CurrentMoment currentMoment = new Match.CurrentMoment();
        currentMoment.setSection(matchLiveScore.getSectionId());
        currentMoment.setTime(matchLiveScore.getSectionTime());
        currentMoment.setSort(TimeSort.DESC);
        match.setCurrentMoment(currentMoment);
        Set<Match.CompetitorStat> competitorStats = Sets.newHashSet();
        competitorStats.add(ConvertModelToCompetitorStat(team1, matchLiveScore.getHomeCompetitorStat()));
        competitorStats.add(ConvertModelToCompetitorStat(team2, matchLiveScore.getAwayCompetitorStat()));
        match.setCompetitorStats(competitorStats);
        List<Match.Squad> squads = Lists.newArrayList();
        squads.add(ConvertModelToSquad(csid, team1.getId(), matchLiveScore.getHomeLineUpForamtion(), matchLiveScore.getHomeLineUp()));
        squads.add(ConvertModelToSquad(csid, team2.getId(), matchLiveScore.getAwayLineUpFormation(), matchLiveScore.getAwayLineUp()));
        match.setSquads(squads);
        Set<Match.BestPlayerStat> bestPlayerStats = createBestPlyerStats(team1.getId(), team2.getId(), squads);
        match.setBestPlayerStats(bestPlayerStats);
        return match;
    }


    private Match.CompetitorStat ConvertModelToCompetitorStat(Team team1, Map<String, Object> stats) {
        Match.CompetitorStat CompetitorStat = new Match.CompetitorStat();
        CompetitorStat.setCompetitorId(team1.getId());
        CompetitorStat.setCompetitorType(CompetitorType.TEAM);
        CompetitorStat.setStats(stats);
        return CompetitorStat;
    }

    private Match.Squad ConvertModelToSquad(Long csid, Long tid, String formation, List<DefaultLiveScore.LiveModel.SquadPlayerModel> players) {
        Match.Squad squad = new Match.Squad();
        squad.setTid(tid);
        squad.setFormation(formation);
        List<Match.SimplePlayer> simplePlayers = Lists.newArrayList();
        for (DefaultLiveScore.LiveModel.SquadPlayerModel playerModel : players) {
            Match.SimplePlayer simplePlayer = new Match.SimplePlayer();
            Player currentPlayer = SbdsInternalApis.getPlayerByQQId(playerModel.getPlayerId());
            if (currentPlayer == null) {
                LOG.warn("the player:{} is not relax ", playerModel.getPlayerId());
                currentPlayer = new Player();
                currentPlayer.setQQId(playerModel.getPlayerId());
                currentPlayer.setName(playerModel.getPlayerName());
                currentPlayer.setMultiLangNames(getMultiLang(playerModel.getPlayerName()));
                Long pid = SbdsInternalApis.savePlayer(currentPlayer);
                if (pid > 0) {
                    currentPlayer.setId(pid);
                }
            }
            String playerNumber = "-1";
            List<TeamSeason> season = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(tid, csid);
            if (season != null && !season.isEmpty() && season.get(0).getPlayers() != null) {
                for (TeamSeason.TeamPlayer player : season.get(0).getPlayers()) {
                    if (player.getPid().longValue() != currentPlayer.getId().longValue())
                        continue;
                    playerNumber = player.getNumber().toString();

                }
            }
            simplePlayer.setNumber(Integer.parseInt(playerNumber));
            simplePlayer.setPid(currentPlayer.getId());
            simplePlayer.setPosition(playerModel.getPosition());
            simplePlayer.setDnp(playerModel.getDnp());
            simplePlayer.setIsOnCourt(playerModel.getIsOnCourt());
            simplePlayer.setStarting(playerModel.getStarting());
            simplePlayer.setStats(playerModel.getStats());
            simplePlayers.add(simplePlayer);
        }
        squad.setPlayers(simplePlayers);
        return squad;
    }

    private List<Match.SectionResult> ConvertMapResultToList(Map<Integer, Match.SectionResult> sectionResults) {
        List<Match.SectionResult> results = Lists.newArrayList();
        if (sectionResults == null) return results;
        Set<Map.Entry<Integer, Match.SectionResult>> lists = sectionResults.entrySet();
        for (Map.Entry<Integer, Match.SectionResult> result : lists) {
            results.add(result.getValue());
        }
        return results;
    }

    public class AttachmentTransformer implements Function<DefaultLiveScore.LiveModel.SectionScore, Match.SectionResult> {
        //子类来实现
        @Override
        public Match.SectionResult apply(DefaultLiveScore.LiveModel.SectionScore attachmentModel) {
            Match.SectionResult metaData = new Match.SectionResult();
            metaData.setOrder(attachmentModel.getPeriodNum());
            metaData.setResult(attachmentModel.getScore());
            metaData.setSection(attachmentModel.getPeriodId());
            return metaData;
        }
    }

    private Set<Match.BestPlayerStat> createBestPlyerStats(Long homeId, Long visitId, List<Match.Squad> squadList) {
        Set<Match.BestPlayerStat> bestPlayerStats = new HashSet<Match.BestPlayerStat>();
        Match.BestPlayerStat homeTeamBestPlayerStats = new Match.BestPlayerStat();
        Match.BestPlayerStat visitingTeamBestPlayerStats = new Match.BestPlayerStat();
        List<Match.SimplePlayer> homeTeamBestPlayers = new ArrayList<Match.SimplePlayer>();
        List<Match.SimplePlayer> visitingTeamBestPlayers = new ArrayList<Match.SimplePlayer>();
        ;
        //得分，三分，篮板，助攻，罚球，抢断，盖帽
        Match.SimplePlayer homeBestFieldGoal = new Match.SimplePlayer();
        Map homepointstats = new HashMap();
        homepointstats.put("name", "得分");
        homepointstats.put("type", "points");
        homepointstats.put("value", 0);
        homeBestFieldGoal.setStats(homepointstats);
        Match.SimplePlayer visitingBestFieldGoal = new Match.SimplePlayer();
        Map visitpointstats = new HashMap();
        visitpointstats.put("name", "得分");
        visitpointstats.put("type", "points");
        visitpointstats.put("value", 0);
        visitingBestFieldGoal.setStats(visitpointstats);
        //三分，
        Match.SimplePlayer homeBestThreePoint = new Match.SimplePlayer();
        Map homestats = new HashMap();
        homestats.put("name", "三分");
        homestats.put("type", "threepoint_made");
        homestats.put("value", 0);
        homeBestThreePoint.setStats(homestats);
        Match.SimplePlayer visitingBestThreePoint = new Match.SimplePlayer();
        Map visitstats = new HashMap();
        visitstats.put("name", "三分");
        visitstats.put("type", "threepoint_made");
        visitstats.put("value", 0);
        visitingBestThreePoint.setStats(visitstats);
        //篮板，
        Match.SimplePlayer homeBestrebound = new Match.SimplePlayer();
        Map homerebound = new HashMap();
        homerebound.put("name", "篮板");
        homerebound.put("type", "total_rebounds");
        homerebound.put("value", 0);
        homeBestrebound.setStats(homerebound);
        Match.SimplePlayer visitingBestrebound = new Match.SimplePlayer();
        Map visitrebound = new HashMap();
        visitrebound.put("name", "篮板");
        visitrebound.put("type", "total_rebounds");
        visitrebound.put("value", 0);
        visitingBestrebound.setStats(visitrebound);
        //罚球，
        Match.SimplePlayer homeBestfoul = new Match.SimplePlayer();
        Map homerfoul = new HashMap();
        homerfoul.put("name", "罚球");
        homerfoul.put("type", "freethrows_made");
        homerfoul.put("value", 0);
        homeBestfoul.setStats(homerfoul);
        Match.SimplePlayer visitingBestfoul = new Match.SimplePlayer();
        Map visitfoul = new HashMap();
        visitfoul.put("name", "罚球");
        visitfoul.put("type", "freethrows_made");
        visitfoul.put("value", 0);
        visitingBestfoul.setStats(visitfoul);
        //助攻，
        Match.SimplePlayer homeBestAssist = new Match.SimplePlayer();
        Map homeAssist = new HashMap();
        homeAssist.put("name", "助攻");
        homeAssist.put("type", "assists");
        homeAssist.put("value", 0);
        homeBestAssist.setStats(homeAssist);
        Match.SimplePlayer visitingBestAssist = new Match.SimplePlayer();
        Map visitAssist = new HashMap();
        visitAssist.put("name", "助攻");
        visitAssist.put("type", "assists");
        visitAssist.put("value", 0);
        visitingBestAssist.setStats(visitAssist);
        //盖帽
        Match.SimplePlayer homeBestBlockShot = new Match.SimplePlayer();
        Map homeBolockShot = new HashMap();
        homeBolockShot.put("name", "盖帽");
        homeBolockShot.put("type", "blockedshots");
        homeBolockShot.put("value", 0);
        homeBestBlockShot.setStats(homeBolockShot);
        Match.SimplePlayer visitingBestBlockShot = new Match.SimplePlayer();
        Map visitBlockShot = new HashMap();
        visitBlockShot.put("name", "盖帽");
        visitBlockShot.put("type", "blockedshots");
        visitBlockShot.put("value", 0);
        visitingBestBlockShot.setStats(visitBlockShot);
        //抢断
        Match.SimplePlayer homeBestSteal = new Match.SimplePlayer();
        Map homeSteal = new HashMap();
        homeSteal.put("name", "抢断");
        homeSteal.put("type", "steals");
        homeSteal.put("value", 0);
        homeBestSteal.setStats(homeSteal);
        Match.SimplePlayer visitingBestSteal = new Match.SimplePlayer();
        Map visitSteal = new HashMap();
        visitSteal.put("name", "抢断");
        visitSteal.put("type", "steals");
        visitSteal.put("value", 0);
        visitingBestSteal.setStats(visitSteal);
        for (Match.Squad squad : squadList) {
            if (squad.getTid().longValue() == homeId.longValue() && squad.getPlayers() != null && !squad.getPlayers().isEmpty()) {
                for (Match.SimplePlayer player : squad.getPlayers()) {
                    Long playerId = player.getPid();
                    String playerNumber = player.getNumber() == null ? player.getNumber().toString() : "0";
                    if (CommonUtil.compare(player.getStats().get("points"), homeBestFieldGoal.getStats().get("value"))) {
                        homeBestFieldGoal.setPid(playerId);
                        homeBestFieldGoal.setNumber(Integer.parseInt(playerNumber));
                        homeBestFieldGoal.getStats().remove("value");
                        homeBestFieldGoal.getStats().put("value", player.getStats().get("points"));
                    }
                    if (CommonUtil.compare(player.getStats().get("threepoint_made"), homeBestThreePoint.getStats().get("value"))) {
                        homeBestThreePoint.setPid(playerId);
                        homeBestThreePoint.setNumber(Integer.parseInt(playerNumber));
                        homeBestThreePoint.getStats().remove("value");
                        homeBestThreePoint.getStats().put("value", player.getStats().get("threepoint_made"));
                    }

                    if (CommonUtil.compare(player.getStats().get("total_rebounds"), homeBestrebound.getStats().get("value"))) {
                        homeBestrebound.setPid(playerId);
                        homeBestrebound.setNumber(Integer.parseInt(playerNumber));
                        homeBestrebound.getStats().remove("value");
                        homeBestrebound.getStats().put("value", player.getStats().get("total_rebounds"));
                    }
                    if (CommonUtil.compare(player.getStats().get("freethrows_made"), homeBestfoul.getStats().get("value"))) {
                        homeBestfoul.setPid(playerId);

                        homeBestfoul.setNumber(Integer.parseInt(playerNumber));
                        homeBestfoul.getStats().remove("value");
                        homeBestfoul.getStats().put("value", player.getStats().get("freethrows_made"));
                    }
                    if (CommonUtil.compare(player.getStats().get("assists"), homeBestAssist.getStats().get("value"))) {
                        homeBestAssist.setPid(playerId);
                        homeBestAssist.setNumber(Integer.parseInt(playerNumber));
                        homeBestAssist.getStats().remove("value");
                        homeBestAssist.getStats().put("value", player.getStats().get("assists"));
                    }
                    if (CommonUtil.compare(player.getStats().get("blockedshots"), homeBestBlockShot.getStats().get("value"))) {
                        homeBestBlockShot.setPid(playerId);
                        homeBestBlockShot.setNumber(Integer.parseInt(playerNumber));
                        homeBestBlockShot.setNumber(Integer.parseInt(playerNumber));
                        homeBestBlockShot.getStats().remove("value");
                        homeBestBlockShot.getStats().put("value", player.getStats().get("blockedshots"));
                    }
                    if (CommonUtil.compare(player.getStats().get("steals"), homeBestSteal.getStats().get("value"))) {
                        homeBestSteal.setPid(playerId);
                        homeBestSteal.setNumber(Integer.parseInt(playerNumber));
                        homeBestSteal.getStats().remove("value");
                        homeBestSteal.getStats().put("value", player.getStats().get("steals"));
                    }
                }

            }
            if (squad.getTid().longValue() == visitId.longValue() && squad.getPlayers() != null && !squad.getPlayers().isEmpty()) {
                for (Match.SimplePlayer player : squad.getPlayers()) {
                    Long playerId = player.getPid();
                    String playerNumber = player.getNumber().toString();
                    if (CommonUtil.compare(player.getStats().get("points"), visitingBestFieldGoal.getStats().get("value"))) {
                        visitingBestFieldGoal.setPid(playerId);
                        visitingBestFieldGoal.setNumber(Integer.parseInt(playerNumber));
                        visitingBestFieldGoal.getStats().remove("value");
                        visitingBestFieldGoal.getStats().put("value", player.getStats().get("points"));
                    }
                    if (CommonUtil.compare(player.getStats().get("threepoint_made"), visitingBestThreePoint.getStats().get("value"))) {
                        visitingBestThreePoint.setPid(playerId);
                        visitingBestThreePoint.setNumber(Integer.parseInt(playerNumber));
                        visitingBestThreePoint.getStats().remove("value");
                        visitingBestThreePoint.getStats().put("value", player.getStats().get("threepoint_made"));
                    }
                    if (CommonUtil.compare(player.getStats().get("total_rebounds"), visitingBestrebound.getStats().get("value"))) {
                        visitingBestrebound.setPid(playerId);
                        visitingBestrebound.setNumber(Integer.parseInt(playerNumber));
                        visitingBestrebound.getStats().remove("value");
                        visitingBestrebound.getStats().put("value", player.getStats().get("total_rebounds"));
                    }
                    if (CommonUtil.compare(player.getStats().get("freethrows_made"), visitingBestfoul.getStats().get("value"))) {
                        visitingBestfoul.setPid(playerId);
                        visitingBestfoul.setNumber(Integer.parseInt(playerNumber));
                        visitingBestfoul.getStats().remove("value");
                        visitingBestfoul.getStats().put("value", player.getStats().get("freethrows_made"));
                    }
                    if (CommonUtil.compare(player.getStats().get("assists"), visitingBestAssist.getStats().get("value"))) {
                        visitingBestAssist.setPid(playerId);
                        visitingBestAssist.setNumber(Integer.parseInt(playerNumber));
                        visitingBestAssist.getStats().remove("value");
                        visitingBestAssist.getStats().put("value", player.getStats().get("assists"));
                    }
                    if (CommonUtil.compare(player.getStats().get("blockedshots"), visitingBestBlockShot.getStats().get("value"))) {
                        visitingBestBlockShot.setPid(playerId);
                        visitingBestBlockShot.setNumber(Integer.parseInt(playerNumber));
                        visitingBestBlockShot.getStats().remove("value");
                        visitingBestBlockShot.getStats().put("value", player.getStats().get("blockedshots"));
                    }
                    if (CommonUtil.compare(player.getStats().get("steals"), visitingBestSteal.getStats().get("value"))) {
                        visitingBestSteal.setPid(playerId);
                        visitingBestSteal.setNumber(Integer.parseInt(playerNumber));
                        visitingBestSteal.getStats().remove("value");
                        visitingBestSteal.getStats().put("value", player.getStats().get("steals"));
                    }
                }
            }
        }
        //添加最佳到主队的最佳球员列表中
        homeTeamBestPlayers.add(homeBestFieldGoal);
        homeTeamBestPlayers.add(homeBestThreePoint);
        homeTeamBestPlayers.add(homeBestrebound);
        homeTeamBestPlayers.add(homeBestAssist);
        homeTeamBestPlayers.add(homeBestfoul);
        homeTeamBestPlayers.add(homeBestSteal);
        homeTeamBestPlayers.add(homeBestBlockShot);
        //添加最佳到刻度的最佳球员列表中
        visitingTeamBestPlayers.add(visitingBestFieldGoal);
        visitingTeamBestPlayers.add(visitingBestThreePoint);
        visitingTeamBestPlayers.add(visitingBestrebound);
        visitingTeamBestPlayers.add(visitingBestAssist);
        visitingTeamBestPlayers.add(visitingBestfoul);
        visitingTeamBestPlayers.add(visitingBestSteal);
        visitingTeamBestPlayers.add(visitingBestBlockShot);
        //保存主队最佳的数据对象
        homeTeamBestPlayerStats.setTid(homeId);
        homeTeamBestPlayerStats.setPlayers(homeTeamBestPlayers);
        //保存客队最佳的数据对象
        visitingTeamBestPlayerStats.setTid(visitId);
        visitingTeamBestPlayerStats.setPlayers(visitingTeamBestPlayers);
        bestPlayerStats.add(homeTeamBestPlayerStats);
        bestPlayerStats.add(visitingTeamBestPlayerStats);
        return bestPlayerStats;
    }

    private void writePlayerToExcel(String homeId, List<DefaultLiveScore.LiveModel.SquadPlayerModel> lists) {

        try {
            Workbook book;
            WritableWorkbook wwb;

            String fileName = "D://CBAPlayers.xls";
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
            WritableSheet ws = wwb.getSheet("CBA球队球员表");
            if (null == ws) {
                ws = wwb.createSheet("CBA球队球员表", 0);
            }
            Constants.j++;
            //要插入到的Excel表格的行号，默认从0开始
            Label labelname = new Label(0, 0, "运动员QQID");//表示第
            Label labelId = new Label(1, 0, "运动员名称");//表示第
            Label image_name = new Label(2, 0, "球队名称");
            ws.addCell(labelname);
            ws.addCell(labelId);
            ws.addCell(image_name);
            try {
                Team currentTeam = SbdsInternalApis.getTeamByQQId(homeId);

                for (DefaultLiveScore.LiveModel.SquadPlayerModel playerModel : lists) {
                    Constants.j++;
                    Label labelname_i = new Label(0, Constants.j, playerModel.getPlayerId());
                    Label labelId_i = new Label(1, Constants.j, playerModel.getPlayerName());
                    Label image_name_i = new Label(2, Constants.j, currentTeam.getName());
                    ws.addCell(labelname_i);
                    ws.addCell(labelId_i);
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


