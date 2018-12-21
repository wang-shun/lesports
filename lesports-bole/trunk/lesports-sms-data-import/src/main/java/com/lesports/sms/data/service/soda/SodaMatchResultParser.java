package com.lesports.sms.data.service.soda;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.FbStatistics;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by zhonglin on 2016/3/23.
 */
@Service("sodaMatchResultParser")
public class SodaMatchResultParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(SodaMatchResultParser.class);

    private final String SODA_TIME_LINE_FILE_PATH = LeProperties.getString("soda.csl.timeline.file.path");
//    private final String SODA_TIME_LINE_FILE_PATH = "E:\\soda\\";


    @Override
    public Boolean parseData(String file) {

        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            //match相关信息
            Element matchElement = rootElement.element("Match");
            String mSodaId = matchElement.attributeValue("id");
            String periodId = matchElement.attributeValue("periodId");
            Match match = SbdsInternalApis.getMatchBySodaId(mSodaId);

            //如果有锁则不更新
            if (match != null && match.getLock() != null && match.getLock() == true) {
                logger.warn("the match may be locked,sodaId:{}, mid:{}", mSodaId, match.getId());
                return result;
            }


            Iterator<Element> teamEventIterator = matchElement.elementIterator("Team");
            List<Match.Squad> squads = new ArrayList<Match.Squad>();
            Set<Match.Competitor> competitors = Sets.newHashSet();
            Set<Match.CompetitorStat> competitorStats = Sets.newHashSet();
            Team homeTeam = null;
            Team awayTeam = null;

            //比赛状态
            Byte matchStatusByte = new Byte("0");
            if ("8".equals(periodId)) {
                matchStatusByte = new Byte("2"); //已结束
            } else if ("1".equals(periodId)) {
                matchStatusByte = new Byte("0"); //未开始
            } else {
                matchStatusByte = new Byte("1"); //比赛中
            }
            match.setStatus(MatchStatus.findByValue(matchStatusByte));

            //按队处理实时比赛结果
            while (teamEventIterator.hasNext()) {
                Element teamEvent = teamEventIterator.next();
                String tSodaId = teamEvent.attributeValue("id");
                Team team = SbdsInternalApis.getTeamBySodaId(tSodaId);
                if (team == null) {
                    continue;
                }

                String side = teamEvent.attributeValue("side");
                String score = teamEvent.attributeValue("score");
                if (StringUtils.isBlank(score)) {
                    score = "0";
                }
                String halfScore = teamEvent.attributeValue("halfScore");
                if (StringUtils.isBlank(halfScore)) {
                    halfScore = score;
                }


                //队伍信息
                Match.Competitor competitor = new Match.Competitor();
                competitor.setCompetitorId(team.getId());
                competitor.setFinalResult(score);
                competitor.setSectionResults(generateSectionResult(periodId, score, halfScore));
                competitor.setType(CompetitorType.TEAM);
                if (side.equals("Away")) {
                    competitor.setGround(GroundType.AWAY);
                    awayTeam = team;
                } else {
                    competitor.setGround(GroundType.HOME);
                    homeTeam = team;
                }
                competitors.add(competitor);
                match.setCompetitors(competitors);


                //队伍比赛数据统计
                Match.CompetitorStat competitorStat = new Match.CompetitorStat();
                FbStatistics fbStatistics = generateStatistics(teamEvent);
                if (fbStatistics != null) {
                    competitorStat = new Match.CompetitorStat();
                    Map<String, Object> map = CommonUtil.convertBeanToMap(fbStatistics);
                    competitorStat.setStats(map);
                    competitorStat.setCompetitorId(team.getId());
                }
                competitorStats.add(competitorStat);

                //判断是否有了出场阵容
                Element PlayerLineUpElement = teamEvent.element("PlayerLineUp");
                Match.Squad squad = new Match.Squad();
                squad.setTid(team.getId());
                squad.setPlayers(generateSimplePlayer(PlayerLineUpElement, team.getId(), match.getCsid(), match.getCid()));
                squads.add(squad);

                //队伍阵型
                String formation = teamEvent.elementText("Formation_used");
                squad.setFormation(formation);

            }
            match.setSquads(squads);
            match.setCompetitorStats(competitorStats);


            //解析timeline的数据
            String name = "s8-282-2017-" + match.getSodaId() + "-timeline.xml";
            String filePath = SODA_TIME_LINE_FILE_PATH + name;
            parseTimeLineData(filePath, match, homeTeam, awayTeam);
            saveMatch(match);
            //matchState
            MatchStats matchStats = SbdsInternalApis.getMatchStatsById(match.getId());
            if (matchStats == null) {
                matchStats = new MatchStats();
                matchStats.setAllowCountries(getAllowCountries());
                matchStats.setId(match.getId());
            }
            matchStats.setCompetitorStats(CompetitorStatToStats(competitorStats, squads));
            saveMatchState(matchStats);

            result = true;
            logger.info("soda match result parser end");
        } catch (Exception e) {
            logger.error("parse soda match result file {}  error", file, e);
        }
        return result;
    }

    //球队阵容
    private List<Match.SimplePlayer> generateSimplePlayer(Element playerLineUp, Long tid, Long csid, Long cid) {
        List<Match.SimplePlayer> simplePlayers = new ArrayList<Match.SimplePlayer>();
        try {

            if (null != playerLineUp) {
                Iterator playerIterator = playerLineUp.elementIterator("Player");

                while (playerIterator.hasNext()) {
                    Match.SimplePlayer simplePlayer = new Match.SimplePlayer();

                    Element teamPlayer = (Element) playerIterator.next();
                    String pSodaId = teamPlayer.attributeValue("id");
                    String pName = teamPlayer.attributeValue("name");
                    String playerNumber = teamPlayer.attributeValue("shirtNumber");
                    String position = teamPlayer.attributeValue("position");
                    if (StringUtils.isNotEmpty(playerNumber)) {
                        simplePlayer.setNumber(Integer.parseInt(playerNumber));
                    }

                    Player player = SbdsInternalApis.getPlayerBySodaId(pSodaId);
                    if (null != player) {
                        simplePlayer.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,pSodaId:{}", pSodaId);
                        player = new Player();
                        player.setName(pName);
                        player.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                        player.setDeleted(false);

                        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^足球$");
                        if (CollectionUtils.isNotEmpty(dictEntries)) {
                            player.setGameFType(dictEntries.get(0).getId());
                        }
                        Set<Long> cids = player.getCids();
                        cids.add(cid);
                        player.setCids(cids);
                        player.setAllowCountries(getAllowCountries());
                        player.setOnlineLanguages(getOnlineLanguage());
                        SbdsInternalApis.savePlayer(player);
                        simplePlayer.setPid(player.getId());

                        //赛季里增加球员
                        TeamSeason teamSeason = new TeamSeason();
                        List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(tid, csid);
                        if (CollectionUtils.isNotEmpty(teamSeasons)) {
                            teamSeason = teamSeasons.get(0);
                            SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(playerNumber));
                        } else {
                            teamSeason.setCsid(csid);
                            teamSeason.setTid(tid);
                            SbdsInternalApis.saveTeamSeason(teamSeason);
                            SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(playerNumber));
                        }
                    }

                    Long id = 0L;
                    if ("门将".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
                    }
                    if ("后卫".equals(position) || "中后卫".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
                    }
                    if ("前卫".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
                    }
                    if ("前锋".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
                    }

                    long pos = 0;
                    Iterator<Element> statIterator = teamPlayer.elementIterator("Stat");
                    while (statIterator.hasNext()) {
                        Element statElement = statIterator.next();
                        if ("formation_place".equals(statElement.attributeValue("type"))) {
                            pos = LeNumberUtils.toLong(statElement.getText());
                        }
                    }
                    simplePlayer.setSquadOrder(pos + "");
                    simplePlayer.setPosition(id);
                    String substitute = teamPlayer.attributeValue("status");

                    //是否首发
                    if ("首发".equals(substitute)) {
                        simplePlayer.setStarting(true);
                    } else {
                        simplePlayer.setStarting(false);
                    }
                    simplePlayers.add(simplePlayer);
                }
            }
        } catch (Exception e) {
            logger.error("generateSimplePlayer  error", e);
        }
        return simplePlayers;
    }

    //球队统计数据
    private FbStatistics generateStatistics(Element teamElement) {
        FbStatistics fbStatistics = new FbStatistics();
        String possession_percentage = "0";
        //射正（被门将扑住）
        String ontarget_scoring_att = "0";
        //射偏
        String offtarget_scoring_att = "0";
        //击中门柱
        String shot_on_post = "0";
        //射门被封堵（被后卫挡出）
        String shot_blocked = "0";
        //进球
        String goals = "0";

        String fk_foul_won = "0";
        String won_corners = "0";
        String fk_foul_lost = "0";
        String off_sides = "0";
        String throw_Ins = "0";
        String goal_keeper_saves = "0";
        String goal_kicks = "0";

        int shot_off_target = 0;
        int shot_on_target = 0;

        try {
            Iterator<Element> statIterator = teamElement.elementIterator("Stat");
            while (statIterator.hasNext()) {
                Element statElement = statIterator.next();

                //控球率
                if (statElement.attributeValue("type").equals("BallPossession")) {
                    possession_percentage = statElement.getText();
                    fbStatistics.setBallPossession(Integer.parseInt(possession_percentage));
                }

                //守门员扑出
                if (statElement.attributeValue("type").equals("ontarget_scoring_att")) {
                    ontarget_scoring_att = statElement.getText();
                }

                //击中门柱
                if (statElement.attributeValue("type").equals("shot_on_post")) {
                    shot_on_post = statElement.getText();
                }

                //射偏
                if (statElement.attributeValue("type").equals("offtarget_scoring_att")) {
                    offtarget_scoring_att = statElement.getText();
                }

                //被后卫挡出
                if (statElement.attributeValue("type").equals("shot_blocked")) {
                    shot_blocked = statElement.getText();
                }

                //进球
                if (statElement.attributeValue("type").equals("goal")) {
                    goals = statElement.getText();
                }

                //射正次数
                shot_on_target = LeNumberUtils.toInt(ontarget_scoring_att) + LeNumberUtils.toInt(goals);
                fbStatistics.setShotsOnGoal(shot_on_target);

                //射偏次数
                shot_off_target = LeNumberUtils.toInt(shot_on_post) + LeNumberUtils.toInt(shot_blocked) + LeNumberUtils.toInt(offtarget_scoring_att);
                fbStatistics.setShotsOffGoal(shot_off_target);

                //任意球
                if (statElement.attributeValue("type").equals("free_kick")) {
                    fk_foul_won = statElement.getText();
                    fbStatistics.setFreeKicks(Integer.parseInt(fk_foul_won));
                }

                //角球
                if (statElement.attributeValue("type").equals("corner")) {
                    won_corners = statElement.getText();
                    fbStatistics.setCornerKicks(Integer.parseInt(won_corners));
                }

                //犯规
                if (statElement.attributeValue("type").equals("foul")) {
                    fk_foul_lost = statElement.getText();
                    fbStatistics.setFouls(Integer.parseInt(fk_foul_lost));
                }

                //越位
                if (statElement.attributeValue("type").equals("offside")) {
                    off_sides = statElement.getText();
                    fbStatistics.setOffsides(Integer.parseInt(off_sides));
                }

                //界外球
                if (statElement.attributeValue("type").equals("throw")) {
                    throw_Ins = statElement.getText();
                    fbStatistics.setThrowIns(Integer.parseInt(throw_Ins));
                }

                //守门员扑救成功
                if (statElement.attributeValue("type").equals("shot_blocked")) {
                    goal_keeper_saves = statElement.getText();
                    fbStatistics.setGoalkeeperSaves(Integer.parseInt(goal_keeper_saves));
                }

                //球门球
                if (statElement.attributeValue("type").equals("goal_kick")) {
                    goal_kicks = statElement.getText();
                    fbStatistics.setGoalKicks(Integer.parseInt(goal_kicks));
                }

                //黄牌
                if (statElement.attributeValue("type").equals("yellow_card")) {
                    goal_kicks = statElement.getText();
                    fbStatistics.setYellow(Integer.parseInt(goal_kicks));
                }

                //红牌
                if (statElement.attributeValue("type").equals("red_card")) {
                    goal_kicks = statElement.getText();
                    fbStatistics.setRed(Integer.parseInt(goal_kicks));
                }
            }
        } catch (Exception e) {
            logger.error("generateStatistics  error", e);
        }
        return fbStatistics;

    }

    //获取阶段比分
    private List<Match.SectionResult> generateSectionResult(String periodId, String score, String halfScore) {
        int periodIdInt = LeNumberUtils.toInt(periodId);
        int scoreInt = LeNumberUtils.toInt(score);
        int halfScoreInt = LeNumberUtils.toInt(halfScore);

        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            //上半场开打
            if (periodIdInt > 1) {
                Match.SectionResult sectionResult = new Match.SectionResult();
                sectionResult.setResult(halfScoreInt + "");
                sectionResult.setOrder(1);
                sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
                sectionResults.add(sectionResult);
            }
            //下半场开打
            if (periodIdInt > 3) {
                Match.SectionResult sectionResult = new Match.SectionResult();
                sectionResult.setResult((scoreInt - halfScoreInt) + "");
                sectionResult.setOrder(2);
                sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
                sectionResults.add(sectionResult);
            }
        } catch (Exception e) {
            logger.error("generateSectionResult  error", e);
        }
        return sectionResults;
    }

    //解析timeline文件，matchAction和控球率
    private boolean parseTimeLineData(String file, Match match, Team homeTeam, Team awayTeam) {
        logger.info("soda time line parser begin");
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            //比赛信息
            Element matchElement = rootElement.element("Match");
            String periodId = matchElement.attributeValue("periodId");

            //删除原有matchAction
            List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(match.getId());
            if (CollectionUtils.isNotEmpty(matchActions)) {
                Iterator iterator1 = matchActions.iterator();
                while (iterator1.hasNext()) {
                    MatchAction matchAction = (MatchAction) iterator1.next();
                    SbdsInternalApis.deleteMatchAction(matchAction.getId());
                }
            }

            //获取matchAction和moment
            Element eventsElement = matchElement.element("Events");
            String moment = generateMatchAction(eventsElement, match.getId(), homeTeam, awayTeam);
            if (StringUtils.isNotBlank(moment)) {
                match.setMoment(moment);
            }

            //计算控球率
            Element possessionElement = matchElement.element("Possession");
            Set<Match.CompetitorStat> competitorStats = generatePossession(possessionElement, match.getCompetitorStats(), homeTeam, awayTeam);
            match.setCompetitorStats(competitorStats);

            result = true;
            logger.info("soda time line parser end");
        } catch (Exception e) {
            logger.error("parse time line  file {}  error", file, e);
        }
        return result;
    }

    //获取matchAction
    private String generateMatchAction(Element eventsElement, Long matchId, Team homeTeam, Team awayTeam) {
        Long yellowType;
        Long redType;
        Long goalType;
        Long subtitutionType;
        Long goalDetailType;
        String moment = "";
        try {
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^黄牌$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                yellowType = dictEntries.get(0).getId();
            } else {
                logger.warn("no dict 黄牌 found");
                return moment;
            }
            List<DictEntry> dictEntries1 = SbdsInternalApis.getDictEntriesByName("^红牌$");
            if (CollectionUtils.isNotEmpty(dictEntries1)) {
                redType = dictEntries1.get(0).getId();
            } else {
                logger.warn("no dict 红牌 found");
                return moment;
            }

            List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^进球$");
            if (CollectionUtils.isNotEmpty(dictEntries2)) {
                goalType = dictEntries2.get(0).getId();
            } else {
                logger.warn("no dict 进球 found");
                return moment;
            }
            List<DictEntry> dictEntries3 = SbdsInternalApis.getDictEntriesByName("^换人$");
            if (CollectionUtils.isNotEmpty(dictEntries3)) {
                subtitutionType = dictEntries3.get(0).getId();
            } else {
                logger.warn("no dict 换人 found");
                return moment;
            }

            long lastGoalTime = 0;
            String lastGoalPeriodId = "0";
            Iterator<Element> eventIterator = eventsElement.elementIterator("Event");
            while (eventIterator.hasNext()) {
                Element eventElement = eventIterator.next();
                String eventName = eventElement.attributeValue("eventName");
                if ("黄牌".equals(eventName) || "红牌".equals(eventName)) {
                    MatchAction matchAction = new MatchAction();
                    matchAction.setPassedTime(Double.parseDouble(eventElement.attributeValue("min")) * 60 + Double.parseDouble(eventElement.attributeValue("sec")));
                    matchAction.setMid(matchId);
                    if ("黄牌".equals(eventName)) {
                        matchAction.setType(yellowType);
                    }
                    if ("红牌".equals(eventName)) {
                        matchAction.setType(redType);
                    }
                    String tSodaId = eventElement.attributeValue("teamId");
                    if (tSodaId.equals(homeTeam.getSodaId())) {
                        matchAction.setTid(homeTeam.getId());
                    }
                    if (tSodaId.equals(awayTeam.getSodaId())) {
                        matchAction.setTid(awayTeam.getId());
                    }

                    Player player = SbdsInternalApis.getPlayerBySodaId(eventElement.attributeValue("playerId"));
                    if (null != player) {
                        matchAction.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,soda playerId:{}", eventElement.attributeValue("playerId"));
                        continue;
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                }

                if ("换人".equals(eventName)) {
                    MatchAction matchAction = new MatchAction();
                    String playerInId = eventElement.attributeValue("subOnId");
                    String playerOutId = eventElement.attributeValue("playerId");
                    matchAction.setPassedTime(Double.parseDouble(eventElement.attributeValue("min")) * 60 + Double.parseDouble(eventElement.attributeValue("sec")));
                    matchAction.setMid(matchId);
                    matchAction.setType(subtitutionType);
                    String tSodaId = eventElement.attributeValue("teamId");
                    if (tSodaId.equals(homeTeam.getSodaId())) {
                        matchAction.setTid(homeTeam.getId());
                    }
                    if (tSodaId.equals(awayTeam.getSodaId())) {
                        matchAction.setTid(awayTeam.getId());
                    }
                    Player playerOut = SbdsInternalApis.getPlayerBySodaId(playerOutId);
                    Player playerIn = SbdsInternalApis.getPlayerBySodaId(playerInId);
                    if (null == playerOut || null == playerIn) {
                        logger.warn("the player not found,soda playerOutId:{},soda playerInId:{}", eventElement.attributeValue("subOnId"), eventElement.attributeValue("playerId"));
                        continue;
                    }
                    matchAction.setPid(playerOut.getId());
                    HashMap<String, Long> map = new HashMap();
                    map.put("playerOut", playerOut.getId());
                    map.put("playerIn", playerIn.getId());
                    matchAction.setContent(JSON.toJSONString(map));
                    SbdsInternalApis.saveMatchAction(matchAction);
                }
                if ("进球".equals(eventName)) {
                    MatchAction matchAction = new MatchAction();
                    goalDetailType = Constants.goalType.get(eventElement.attributeValue("type")) == null ? 100159000L : Constants.goalType.get(eventElement.attributeValue("type"));
                    matchAction.setPassedTime(Double.parseDouble(eventElement.attributeValue("min")) * 60 + Double.parseDouble(eventElement.attributeValue("sec")));
                    matchAction.setMid(matchId);
                    String tSodaId = eventElement.attributeValue("teamId");
                    if (tSodaId.equals(homeTeam.getSodaId())) {
                        matchAction.setTid(homeTeam.getId());
                    }
                    if (tSodaId.equals(awayTeam.getSodaId())) {
                        matchAction.setTid(awayTeam.getId());
                    }
                    matchAction.setType(goalType);
                    HashMap<String, Long> map = new HashMap();
                    map.put("goalType", goalDetailType);
                    matchAction.setContent(JSON.toJSONString(map));
                    Player player = SbdsInternalApis.getPlayerBySodaId(eventElement.attributeValue("playerId"));
                    if (null != player) {
                        matchAction.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,soda playerId:{}", eventElement.attributeValue("playerId"));
                        continue;
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                    lastGoalTime = LeNumberUtils.toLong(eventElement.attributeValue("min"));
                    lastGoalPeriodId = eventElement.attributeValue("periodId");
                }
            }

            //最后进球时刻
            if (lastGoalTime > 0 && !"0".equals(lastGoalPeriodId)) {
                if ("2".equals(lastGoalPeriodId)) {
                    moment = "上半场-" + lastGoalTime;
                } else if ("4".equals(lastGoalPeriodId)) {
                    moment = "下半场-" + lastGoalTime;
                } else if ("5".equals(lastGoalPeriodId)) {
                    moment = "加时上半场-" + lastGoalTime;
                } else if ("6".equals(lastGoalPeriodId)) {
                    moment = "加时下半场-" + lastGoalTime;
                } else if ("7".equals(lastGoalPeriodId)) {
                    moment = "点球-" + lastGoalTime;
                }

                logger.info("当前比赛状态和时间:{}", moment);
            }

        } catch (Exception e) {
            logger.error("generateMatchAction  error", e);
        }
        return moment;
    }

    //获取控球率
    private Set<Match.CompetitorStat> generatePossession(Element minElement, Set<Match.CompetitorStat> competitorStats, Team homeTeam, Team awayTeam) {
        long homeMin = 0;
        long awayMin = 0;
        long homePossession = 0;
        long awayPossession = 0;
        Set<Match.CompetitorStat> newCompetitorStats = Sets.newHashSet();

        try {
            Iterator<Element> minIterator = minElement.elementIterator("Min");
            while (minIterator.hasNext()) {
                Element possessionElement = minIterator.next();
                //最后一个元素
                if (!minIterator.hasNext()) {
                    homeMin = LeNumberUtils.toLong(possessionElement.attributeValue("home"));
                    awayMin = LeNumberUtils.toLong(possessionElement.attributeValue("away"));
                    homePossession = Math.round(homeMin * 100d / (homeMin + awayMin));
                    awayPossession = Math.round(awayMin * 100d / (homeMin + awayMin));
                }
            }
            logger.info("homeTid: {} , awayTid: {} , homePossession: {} , awayPossession: {} ", homeTeam.getId(), awayTeam.getId(), homePossession, awayPossession);
            if (CollectionUtils.isNotEmpty(competitorStats)) {
                for (Match.CompetitorStat competitorStat : competitorStats) {
                    Map<String, Object> map = competitorStat.getStats();
                    if (competitorStat.getCompetitorId().equals(homeTeam.getId())) {
                        map.put("ballPossession", homePossession);
                    }
                    if (competitorStat.getCompetitorId().equals(awayTeam.getId())) {
                        map.put("ballPossession", awayPossession);
                    }
                    competitorStat.setStats(map);
                    newCompetitorStats.add(competitorStat);
                }
            }

        } catch (Exception e) {
            logger.error("generatePossession  error", e);
        }
        return newCompetitorStats;
    }


}
