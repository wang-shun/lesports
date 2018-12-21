package com.lesports.sms.data.service.stats.euro;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.TimeSort;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.FbStatistics;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by zhonglin on 2016/4/21.
 */
@Service("EUROLiveParser")
public class EUROLiveParser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(EUROLiveParser.class);
    //init match status information
    private Map<String, MatchStatus> stateMap = ImmutableMap.of("1", MatchStatus.MATCH_NOT_START, "2", MatchStatus.MATCHING, "4", MatchStatus.MATCH_END);


    @Override
    public Boolean parseData(String file) {
        logger.info("live score  parser begin");
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
            Element eurocores = rootElement.element("sports-boxscores").element("soccer-ifb-boxscores");
            Element euroBoxScore = eurocores.element("ifb-boxscore");
            String partnerId = euroBoxScore.element("gamecode").attributeValue("global-code");
            Match match = SbdsInternalApis.getMatchByStatsId(partnerId);
            if (null == match) {
                logger.warn(partnerId + " live match is not exist");
                return result;
            }
            //match status 更新
            String statusId = euroBoxScore.element("gamestate").attributeValue("status-id");
            if (stateMap.get(statusId) != null) {
                MatchStatus currentStatus = stateMap.get(statusId);
                match.setStatus(currentStatus);
            }

            //update 比赛当前时间状态
            Match.CurrentMoment overtimeMoment = match.getCurrentMoment();
            Match.CurrentMoment currentMoment = new Match.CurrentMoment();
            String period = euroBoxScore.element("gamestate").attributeValue("period");
            String minute = euroBoxScore.element("gamestate").attributeValue("minutes");
            String second = euroBoxScore.element("gamestate").attributeValue("seconds");
            String addMinute = euroBoxScore.element("gamestate").attributeValue("additional-minutes");
            double restTime = CommonUtil.parseDouble(minute, 0) * 60.0 + CommonUtil.parseDouble(second, 0) + CommonUtil.parseDouble(addMinute, 0);
            String sectionName = "上半场";
            if (CommonUtil.parseInt(period, 0) == 1) {
                sectionName = "上半场";
            } else if (CommonUtil.parseInt(period, 0) == 2) {
                sectionName = "下半场";
            } else if (CommonUtil.parseInt(period, 0) == 3) {
                sectionName = "加时上半场";
            } else if (CommonUtil.parseInt(period, 0) == 4) {
                sectionName = "加时下半场";
            }
            currentMoment.setSection(SbdsInternalApis.getDictEntriesByName("^"+sectionName + "$") == null ? 0L : SbdsInternalApis.getDictEntriesByName(sectionName + "$").get(0).getId());
            currentMoment.setSectionName(sectionName);
            currentMoment.setTime(restTime);
            currentMoment.setSort(TimeSort.DESC);
            match.setCurrentMoment(currentMoment);

            //get current competetiors 信息
            String homeTeamPartnerId = euroBoxScore.element("home-team").element("team-info").attributeValue("global-id");
            String visitingTeamPartnerId = euroBoxScore.element("visiting-team").element("team-info").attributeValue("global-id");
            Team homeTeam = SbdsInternalApis.getTeamByStatsId(homeTeamPartnerId);
            Team visitingTeam = SbdsInternalApis.getTeamByStatsId(visitingTeamPartnerId);
            //判定时候已构建两个球队的信息
            if (null == homeTeam || null == visitingTeam) {
                logger.warn("teams can not be found,partner1Id:{} ,partner2Id:{} ", homeTeamPartnerId, visitingTeamPartnerId);
                return result;
            }

            //更新Match.competions 信息
            Set<Match.Competitor> competitors = new HashSet<>();
            Set<Match.CompetitorStat> competitorStats = new HashSet<>();
            if (CollectionUtils.isNotEmpty(match.getCompetitors())) {
                logger.info("old competitors : " + match.getCompetitors() + " homeId: " + homeTeam.getId() + " awayId: " + visitingTeam.getId());
                Iterator competitorIterator = match.getCompetitors().iterator();
                while (competitorIterator.hasNext()) {
                    Match.Competitor competitor = (Match.Competitor) competitorIterator.next();
                    if (competitor.getCompetitorId().equals(homeTeam.getId())) {
                        upsertMatchCompetitor(competitor, euroBoxScore, competitors, competitorStats, "0");
                        logger.info("home competitor information is updated end" + competitor.getCompetitorId());
                    }
                    if (competitor.getCompetitorId().equals(visitingTeam.getId())) {
                        upsertMatchCompetitor(competitor, euroBoxScore, competitors, competitorStats, "1");
                        logger.info("visiting competitor information is updated end" + competitor.getCompetitorId());
                    }
                }
            } else {
                Match.Competitor homeCompetitor = new Match.Competitor();
                homeCompetitor.setCompetitorId(homeTeam.getId());
                homeCompetitor.setGround(GroundType.HOME);
                homeCompetitor.setType(CompetitorType.TEAM);
                upsertMatchCompetitor(homeCompetitor, euroBoxScore, competitors, competitorStats, "0");
                logger.info("home competitor information is saved end");
                Match.Competitor visitingCompetitor = new Match.Competitor();
                visitingCompetitor.setCompetitorId(visitingTeam.getId());
                visitingCompetitor.setType(CompetitorType.TEAM);
                visitingCompetitor.setGround(GroundType.AWAY);
                upsertMatchCompetitor(visitingCompetitor, euroBoxScore, competitors, competitorStats, "1");
                logger.info("home competitor information is saved end");
            }

            match.setCompetitors(competitors);

            match.setCompetitorStats(competitorStats);

            List<Match.Squad> squads = createSquadList(euroBoxScore, homeTeam, visitingTeam, match);
            match.setSquads(squads);

            logger.info("match team and player squard and statics informatin are saved en information is saved");

            //删除原有matchAction
            List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(match.getId());
            if (CollectionUtils.isNotEmpty(matchActions)) {
                Iterator iterator1 = matchActions.iterator();
                while (iterator1.hasNext()) {
                    MatchAction matchAction = (MatchAction) iterator1.next();
                    SbdsInternalApis.deleteMatchAction(matchAction.getId());
                    logger.info("matchAction: " + matchAction);
                }
            }

            //获取match
            if (euroBoxScore.element("half-details") != null) {
                updateMatchAction(euroBoxScore.element("half-details"), match.getId(), homeTeam, visitingTeam);
            }

            boolean isSuccess = false;
            int tryCount = 0;
            while (!isSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
                try {
                    logger.info("match: " + match);
                    isSuccess = SbdsInternalApis.saveMatch(match) > 0L;
                } catch (Exception e) {
                    logger.error("fail to update match. id : {}. sleep and try again.", match.getId(), e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            logger.info("update EURO match success,matchId:" + match.getId() + ",matchName:" + match.getName() + ",matchPartnerId:" + match.getPartnerId() +
                    ",matchStatus:" + match.getStatus());
            return true;
        } catch (Exception e) {
            logger.error("parse EURO_livescore error", e);
        }
        return result;
    }


    /**
     * 获取当前比赛的主客队阵容
     *
     * @param euroBoxScore
     * @param homeTeam
     * @param visitingTeam
     * @param match
     * @return
     */
    private List<Match.Squad> createSquadList(Element euroBoxScore, Team homeTeam, Team visitingTeam, Match match) {
        List<Match.Squad> list = new ArrayList<Match.Squad>();

        Iterator<Element> playerIt = euroBoxScore.elementIterator("players");
        Iterator<Element> benchIt = euroBoxScore.elementIterator("bench");

        //主队的阵容
        Match.Squad squad1 = new Match.Squad();
        squad1.setTid(homeTeam.getId());

        //客队的阵容
        Match.Squad squad2 = new Match.Squad();
        squad2.setTid(visitingTeam.getId());

        List<Match.SimplePlayer> playerList1 = Lists.newArrayList();
        List<Match.SimplePlayer> playerList2 = Lists.newArrayList();

        while (playerIt.hasNext()) {
            Element playersStats = playerIt.next();
            if (playersStats.attributeValue("team").equals("home")) {
                generateSimplePlayer(playersStats, "0", homeTeam.getId(), match.getCsid(), playerList1);
            } else {
                generateSimplePlayer(playersStats, "0", visitingTeam.getId(), match.getCsid(), playerList2);
            }

        }

        while (benchIt.hasNext()) {
            Element playersStats = benchIt.next();
            if (playersStats.attributeValue("team").equals("home")) {
                generateSimplePlayer(playersStats, "1", homeTeam.getId(), match.getCsid(), playerList1);
            } else {
                generateSimplePlayer(playersStats, "1", visitingTeam.getId(), match.getCsid(), playerList2);
            }
        }

        squad1.setPlayers(playerList1);
        squad2.setPlayers(playerList2);
        squad1.setFormation(generateFormation(euroBoxScore, "0"));
        squad2.setFormation(generateFormation(euroBoxScore, "1"));
        list.add(squad1);
        list.add(squad2);

        return list;
    }

    /**
     * 获取指定球队阵型
     *
     * @param euroBoxScore
     * @return
     */
    private String generateFormation(Element euroBoxScore, String type) {
        String formation = "";
        Element teamElement;
        if ("0".equals(type)) {
            teamElement = euroBoxScore.element("home-team");
        } else {
            teamElement = euroBoxScore.element("visiting-team");
        }
        if (null == teamElement) {
            return formation;
        }
        formation = teamElement.element("formation").attributeValue("formation");
        return formation;
    }

    /**
     * 获取指定球队的队员阵容详情和技术统计数据详情
     *
     * @param playerstats
     * @param type
     * @param teamId
     * @return
     */
    private List<Match.SimplePlayer> generateSimplePlayer(Element playerstats, String type, Long teamId, Long csid, List<Match.SimplePlayer> list) {
        if (null != playerstats) {
            Iterator playerIterator = null;
            playerIterator = playerstats.elementIterator("player");

            if (null == playerIterator) {
                logger.warn("player infotmation is empty");
                return null;
            }
            while (playerIterator.hasNext()) {
                Match.SimplePlayer simplePlayer = new Match.SimplePlayer();
                Element teamPlayer = (Element) playerIterator.next();
                String pid = teamPlayer.element("player-code").attributeValue("global-id");
                String pName;
                if (StringUtils.isBlank(teamPlayer.element("name").attributeValue("first-name"))) {
                    pName = teamPlayer.element("name").attributeValue("last-name");
                } else {
                    pName = teamPlayer.element("name").attributeValue("first-name") + " " + teamPlayer.element("name").attributeValue("last-name");
                }
//                String substitute = teamPlayer.element("games").attributeValue("games-started");
                Long playerId = getPlayerBypartnerId(pid, pName);
                simplePlayer.setPid(playerId);

                String playerNumber = teamPlayer.element("player-number").attributeValue("number");
                if (StringUtils.isBlank(playerNumber)) playerNumber = "-1";


                if (playerNumber.equals("-1")) {
                    List<TeamSeason> season = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
                    if (season != null && !season.isEmpty() && season.get(0).getPlayers() != null) {
                        for (TeamSeason.TeamPlayer player : season.get(0).getPlayers()) {
                            if (player.getPid().longValue() != playerId.longValue())
                                continue;
                            playerNumber = player.getNumber().toString();
                        }
                    }
                }

                simplePlayer.setNumber(Integer.parseInt(playerNumber));
                long id = 0L;
                String pos = teamPlayer.element("player-position").attributeValue("english");
                if (StringUtils.isNotEmpty(pos)) {
                    logger.info("thecurre pos of the player" + playerId + "pos:" + pos);

                    if (pos.equalsIgnoreCase("Forward")) {
                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
                    }
                    if (pos.equalsIgnoreCase("Midfielder")) {
                        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
                    }
                    if (pos.equalsIgnoreCase("Defender")) {
                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
                    }
                    if (pos.equalsIgnoreCase("Goalkeeper")) {
                        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
                    }
                }
                logger.info("the position of the player" + playerId + " id: " + id);
                simplePlayer.setPosition(id);

                //是否首发
                if ("0".equals(type)) {
                    if ("Y".equals(teamPlayer.element("starter").attributeValue("starter"))) {
                        simplePlayer.setStarting(true);
                        simplePlayer.setSquadOrder(teamPlayer.element("starter").attributeValue("formation"));
                    } else {
                        simplePlayer.setStarting(false);
                        simplePlayer.setSquadOrder("0");
                    }
                } else if ("1".equals(type)) {
                    simplePlayer.setStarting(false);
                    simplePlayer.setSquadOrder("0");
                }


                //是否上场 0 未上场  1 上场
                simplePlayer.setDnp(CommonUtil.parseInt(type, 0));

                list.add(simplePlayer);
                logger.info("simplePlayer is builed and added to list sucessfully");
            }
        }
        return list;
    }

    /**
     * 构建球队阵容，如果球员存在，则直接构建，否则先创建球员
     *
     * @param partnerId
     * @param pName
     * @return
     */
    private Long getPlayerBypartnerId(String partnerId, String pName) {
        Player player = SbdsInternalApis.getPlayerByStatsId(partnerId);
        if (null == player) {
            logger.warn("the player not found,partnerId:{}", partnerId);
            player = new Player();
            player.setName(pName);
            player.setStatsId(partnerId);
            player.setDeleted(false);
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^足球$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                player.setGameFType(dictEntries.get(0).getId());
            }
            SbdsInternalApis.savePlayer(player);
            logger.info("insert into player sucess,partnerId:");
        }
        return SbdsInternalApis.getPlayerByStatsId(partnerId).getId();
    }

    /**
     * g更新两个team的信息
     *
     * @param competitor
     * @param euroBoxScore
     * @param newSet
     * @param statsSet
     * @param teamType
     */
    private void upsertMatchCompetitor(Match.Competitor competitor, Element euroBoxScore, Set<Match.Competitor> newSet, Set<Match.CompetitorStat> statsSet, String teamType) {
        Match.Competitor oldCompetitor = new Match.Competitor();
        Match.CompetitorStat competitorStat = new Match.CompetitorStat();
        oldCompetitor.setFinalResult(competitor.getFinalResult());
        oldCompetitor.setCompetitorId(competitor.getCompetitorId());
        FbStatistics fbStatistics = new FbStatistics();
        if (teamType.equalsIgnoreCase("0")) {
            oldCompetitor.setGround(GroundType.HOME);
            if (euroBoxScore.element("home-team").element("linescore") != null) {
                competitor.setFinalResult(euroBoxScore.element("home-team").element("linescore").attributeValue("score"));
            } else {
                competitor.setFinalResult("0");
            }

            List<Match.SectionResult> results = generatePeriodResult(euroBoxScore.element("home-team").element("linescore"));
            if (results == null || results.isEmpty()) {
                results = generatePeriodResult(euroBoxScore.element("home-team").element("linescore"));
            }
            competitor.setSectionResults(results);
        }
        if (teamType.equalsIgnoreCase("1")) {
            oldCompetitor.setGround(GroundType.AWAY);
            if (euroBoxScore.element("visiting-team").element("linescore") != null) {
                competitor.setFinalResult(euroBoxScore.element("visiting-team").element("linescore").attributeValue("score"));
            } else {
                competitor.setFinalResult("0");
            }
            List<Match.SectionResult> results = generatePeriodResult(euroBoxScore.element("visiting-team").element("linescore"));
            if (results == null || results.isEmpty()) {
                results = generatePeriodResult(euroBoxScore.element("visiting-team").element("linescore"));
            }
            competitor.setSectionResults(results);

        }
        newSet.add(competitor);

        //获取球队统计
        Iterator<Element> statsIt = euroBoxScore.elementIterator("team-stats");
        while (statsIt.hasNext()) {
            Element stats = statsIt.next();
            if (stats.attributeValue("team").equals("home") && competitor.getGround().equals(GroundType.HOME)) {
                fbStatistics = generateStatistics(stats, "TEAM", competitor.getCompetitorId());
                competitorStat.setShowOrder(1);
            }
            if (stats.attributeValue("team").equals("visitors") && competitor.getGround().equals(GroundType.AWAY)) {
                fbStatistics = generateStatistics(stats, "TEAM", competitor.getCompetitorId());
                competitorStat.setShowOrder(2);
            }
        }

        Map<String, Object> map = CommonUtil.convertBeanToMap(fbStatistics);
        competitorStat.setStats(map);
        competitorStat.setCompetitorId(competitor.getCompetitorId());
        competitorStat.setCompetitorType(CompetitorType.TEAM);
        newSet.add(competitor);

        statsSet.add(competitorStat);

    }


    /**
     * 构建当前比赛的小节成绩
     *
     * @param linescore
     * @return
     */
    private List<Match.SectionResult> generatePeriodResult(Element linescore) {

        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            if (null == linescore) {
                return null;
            }
            Iterator scoreIterator = linescore.elementIterator("half");
            while (scoreIterator.hasNext()) {
                Element half = (Element) scoreIterator.next();
                Match.SectionResult sectionResult = new Match.SectionResult();
                sectionResult.setResult(half.attributeValue("score"));
                sectionResult.setOrder(CommonUtil.parseInt(half.attributeValue("half"), 0));
                String sectionName = "上半场$";
                if (sectionResult.getOrder() == 2) {
                    sectionName = "下半场$";
                } else if (sectionResult.getOrder() == 3) {
                    sectionName = "加时上半场$";
                } else if (sectionResult.getOrder() == 4) {
                    sectionName = "加时下半场$";
                }

                sectionResult.setSection(SbdsInternalApis.getDictEntriesByName(sectionName).get(0).getId());
                sectionResults.add(sectionResult);
            }
            logger.info("generate section Results sucessfully");
        } catch (Exception e) {
            logger.error("generateSectionResult  error", e);
        }

        return sectionResults;
    }


    /**
     * 更新matchAction
     *
     * @param halfDetailsElement
     * @param matchId
     * @param team1
     * @param team2
     * @return
     */
    public void updateMatchAction(Element halfDetailsElement, Long matchId, Team team1, Team team2) {
        Long yellowType;
        Long redType;
        Long goalType;
        Long subtitutionType;

        Iterator<Element> halfDetails = halfDetailsElement.elementIterator("half-detail");
        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^黄牌$");
        if (CollectionUtils.isNotEmpty(dictEntries)) {
            yellowType = dictEntries.get(0).getId();
        } else {
            logger.warn("no dict 黄牌 found");
            return;
        }
        List<DictEntry> dictEntries1 = SbdsInternalApis.getDictEntriesByName("^红牌$");
        if (CollectionUtils.isNotEmpty(dictEntries1)) {
            redType = dictEntries1.get(0).getId();
        } else {
            logger.warn("no dict 红牌 found");
            return;
        }

        List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^进球$");
        if (CollectionUtils.isNotEmpty(dictEntries2)) {
            goalType = dictEntries2.get(0).getId();
        } else {
            logger.warn("no dict 进球 found");
            return;
        }

        List<DictEntry> dictEntries3 = SbdsInternalApis.getDictEntriesByName("^换人$");
        if (CollectionUtils.isNotEmpty(dictEntries3)) {
            subtitutionType = dictEntries3.get(0).getId();
        } else {
            logger.warn("no dict 换人 found");
            return;
        }
        while (halfDetails.hasNext()) {
            Element halfDetail = halfDetails.next();

            logger.info("halfDetail half: " + halfDetail.attributeValue("half"));

            //红牌
            if (halfDetail.element("red-cards") != null) {
                Iterator<Element> redCards = halfDetail.element("red-cards").elementIterator("red-card");
                while (redCards.hasNext()) {
                    Element redCard = redCards.next();
                    generateCardMatchAction(redCard, matchId, team1, team2, redType);
                }
            }

            //黄牌
            if (halfDetail.element("yellow-cards") != null) {
                Iterator<Element> yellowCards = halfDetail.element("yellow-cards").elementIterator("yellow-card");
                while (yellowCards.hasNext()) {
                    Element yellowCard = yellowCards.next();
                    generateCardMatchAction(yellowCard, matchId, team1, team2, yellowType);
                }
            }

            //进球
            if (halfDetail.element("goals") != null) {
                Iterator<Element> goals = halfDetail.element("goals").elementIterator("goal");
                while (goals.hasNext()) {
                    Element goal = goals.next();
                    generateGoalMatchAction(goal, matchId, team1, team2, goalType);
                }
            }

            //换人
            if (halfDetail.element("substitutions") != null) {
                Iterator<Element> substitutions = halfDetail.element("substitutions").elementIterator("substitution");
                while (substitutions.hasNext()) {
                    Element substitution = substitutions.next();
                    generateSubstitutionMatchAction(substitution, matchId, team1, team2, subtitutionType);
                }
            }
        }
    }

    /**
     * 构建红黄牌matchAction
     *
     * @param eventElement
     * @param matchId
     * @param team1
     * @param team2
     * @param type
     * @return
     */
    private void generateCardMatchAction(Element eventElement, Long matchId, Team team1, Team team2, Long type) {
        try {
            MatchAction matchAction = new MatchAction();

            String globalId = eventElement.element("team-info").attributeValue("global-id");
            String minute = eventElement.element("time").attributeValue("minutes");
            String second = eventElement.element("time").attributeValue("seconds");
            String addMinute = eventElement.element("time").attributeValue("additional-minutes");
            double restTime = CommonUtil.parseDouble(minute, 0) * 60.0 + CommonUtil.parseDouble(second, 0) + CommonUtil.parseDouble(addMinute, 0);
            matchAction.setPassedTime(restTime);
            matchAction.setMid(matchId);
            if (team1.getStatsId().equals(globalId))
                matchAction.setTid(team1.getId());
            else
                matchAction.setTid((team2.getId()));

            matchAction.setType(type);
            Element playerElement = eventElement.element("player-cautioned").element("player-code");
            if (null != playerElement) {
                Player player = SbdsInternalApis.getPlayerByStatsId(playerElement.attributeValue("global-id"));
                if (null != player) {
                    matchAction.setPid(player.getId());
                } else {
                    logger.warn("the player not found,statsId:{}", eventElement.element("Player").element("player-code").attributeValue("global-id"));
                    return;
                }
            }
            SbdsInternalApis.saveMatchAction(matchAction);
            logger.info("card matchAction: " + matchAction);
        } catch (Exception e) {
            logger.error("generateMatchAction error", e);
        }
    }

    /**
     * 构建进球matchAction
     *
     * @param eventElement
     * @param matchId
     * @param team1
     * @param team2
     * @param type
     * @return
     */
    private void generateGoalMatchAction(Element eventElement, Long matchId, Team team1, Team team2, Long type) {
        try {
            String globalId = eventElement.element("team-info").attributeValue("global-id");
            String minute = eventElement.element("time").attributeValue("minutes");
            String second = eventElement.element("time").attributeValue("seconds");
            String addMinute = eventElement.element("time").attributeValue("additional-minutes");
            double restTime = CommonUtil.parseDouble(minute, 0) * 60.0 + CommonUtil.parseDouble(second, 0) + CommonUtil.parseDouble(addMinute, 0);
            MatchAction matchAction = new MatchAction();
            Long goalDetailType = Constants.goalType.get(eventElement.element("goal-type").attributeValue("event-number")) == null ? 0L : Constants.goalType.get(eventElement.element("goal-type").attributeValue("event-number"));
            matchAction.setPassedTime(restTime);
            matchAction.setMid(matchId);
            if (team1.getStatsId().equals(globalId))
                matchAction.setTid(team1.getId());
            else
                matchAction.setTid((team2.getId()));
            matchAction.setType(type);
            HashMap<String, Long> map = new HashMap();
            map.put("goalType", goalDetailType);
            matchAction.setContent(JSON.toJSONString(map));
            Element playerElement = eventElement.element("player-code");
            Player player = SbdsInternalApis.getPlayerByStatsId(playerElement.attributeValue("global-id"));
            if (null != player) {
                matchAction.setPid(player.getId());
            } else {
                logger.warn("the player not found,soda playerId:{}", eventElement.attributeValue("playerId"));
                return;
            }
            SbdsInternalApis.saveMatchAction(matchAction);
            logger.info("goal matchAction: " + matchAction);

        } catch (Exception e) {
            logger.error("generateMatchAction error", e);
        }
    }

    /**
     * 构建换人matchAction
     *
     * @param eventElement
     * @param matchId
     * @param team1
     * @param team2
     * @param type
     * @return
     */
    private void generateSubstitutionMatchAction(Element eventElement, Long matchId, Team team1, Team team2, Long type) {
        try {
            String globalId = eventElement.element("team-info").attributeValue("global-id");
            String minute = eventElement.element("time").attributeValue("minutes");
            String second = eventElement.element("time").attributeValue("seconds");
            String addMinute = eventElement.element("time").attributeValue("additional-minutes");
            double restTime = CommonUtil.parseDouble(minute, 0) * 60.0 + CommonUtil.parseDouble(second, 0) + CommonUtil.parseDouble(addMinute, 0);

            MatchAction matchAction = new MatchAction();
            String playerOutId = eventElement.element("player-out").element("player-code").attributeValue("global-id");
            String playerInId = eventElement.element("player-in").element("player-code").attributeValue("global-id");
            matchAction.setPassedTime(restTime);
            matchAction.setMid(matchId);
            matchAction.setType(type);
            if (team1.getStatsId().equals(globalId))
                matchAction.setTid(team1.getId());
            else
                matchAction.setTid((team2.getId()));
            Player playerOut = SbdsInternalApis.getPlayerByStatsId(playerOutId);
            Player playerIn = SbdsInternalApis.getPlayerByStatsId(playerInId);
            if (null == playerOut || null == playerIn) {
                logger.warn("the player not found,soda playerOutId:{},soda playerInId:{}", eventElement.attributeValue("subOnId"), eventElement.attributeValue("playerId"));
                return;
            }
            matchAction.setPid(playerOut.getId());
            HashMap<String, Long> map = new HashMap();
            map.put("playerOut", playerOut.getId());
            map.put("playerIn", playerIn.getId());
            matchAction.setContent(JSON.toJSONString(map));
            SbdsInternalApis.saveMatchAction(matchAction);
            logger.info("substitution matchAction: " + matchAction);

        } catch (Exception e) {
            logger.error("generateMatchAction error", e);
        }
    }

    /**
     * 保存当前直播中的技术统计信息，通过类型区分，分别表示球队和球员的信息
     *
     * @param statistics
     * @param type
     * @param id
     * @return
     */
    private FbStatistics generateStatistics(Element statistics, String type, Long id) {
        FbStatistics fbStatistics = new FbStatistics();
        String possession_percentage = "0";
        String ontarget_scoring_att = "0";
        String shots_att = "0";
        String fk_foul_won = "0";
        String won_corners = "0";
        String fk_foul_lost = "0";
        String off_sides = "0";
        String throw_Ins = "0";
        String goal_keeper_saves = "0";
        String goal_kicks = "0";
        String red = "0";
        String yellow = "0";
        try {
            if (null != statistics) {

                //控球率
                Element possession = statistics.element("possession");
                if (possession != null) {
                    possession_percentage = possession.attributeValue("percent");
                    fbStatistics.setBallPossession(CommonUtil.parseInt(possession_percentage, 0));
                }

                //射正球门
                Element shotsOnGoal = statistics.element("shots-on-goal");
                if (shotsOnGoal != null) {
                    ontarget_scoring_att = shotsOnGoal.attributeValue("shots");
                    fbStatistics.setShotsOnGoal(CommonUtil.parseInt(ontarget_scoring_att, 0));
                }
                //射偏球门
                Element shots = statistics.element("shots");
                if (shots != null) {
                    shots_att = shots.attributeValue("shots");
                    fbStatistics.setShotsOffGoal(CommonUtil.parseInt(shots_att, 0) - fbStatistics.getShotsOnGoal());
                }

                //任意球
                Element freeKicks = statistics.element("FreeKicks");
                if (freeKicks != null) {
                    fk_foul_won = freeKicks.attributeValue("Team" + type);
                    fbStatistics.setFreeKicks(Integer.parseInt(fk_foul_won));
                }

                //角球
                Element cornerKicks = statistics.element("corner-kicks");
                if (cornerKicks != null) {
                    won_corners = cornerKicks.attributeValue("corner-kicks");
                    fbStatistics.setCornerKicks(CommonUtil.parseInt(won_corners, 0));
                }

                //犯规
                Element fouls = statistics.element("fouls-committed");
                if (fouls != null) {
                    fk_foul_lost = fouls.attributeValue("fouls");
                    fbStatistics.setFouls(CommonUtil.parseInt(fk_foul_lost, 0));
                }

                //越位
                Element offsides = statistics.element("offsides");
                if (offsides != null) {
                    off_sides = offsides.attributeValue("offsides");
                    fbStatistics.setOffsides(CommonUtil.parseInt(off_sides, 0));
                }

//                //界外球
//                Element throwIns = statistics.element("ThrowIns");
//                if (throwIns != null) {
//                    throw_Ins = throwIns.elementText("Team" + type);
//                    fbStatistics.setThrowIns(Integer.parseInt(throw_Ins));
//                }

                //守门员扑救
                Element goalkeeperSaves = statistics.element("saves");
                if (goalkeeperSaves != null) {
                    goal_keeper_saves = goalkeeperSaves.attributeValue("saves");
                    fbStatistics.setGoalkeeperSaves(Integer.parseInt(goal_keeper_saves));
                }

//                //球门球
//                Element goalKicks = statistics.element("GoalKicks");
//                if (goalKicks != null) {
//                    goal_kicks = goalKicks.elementText("Team" + type);
//                    fbStatistics.setGoalKicks(Integer.parseInt(goal_kicks));
//                }

                //黄牌
                Element yellowCards = statistics.element("yellow-cards");
                if (yellowCards != null) {
                    yellow = yellowCards.attributeValue("yellow-cards");
                    fbStatistics.setYellow(CommonUtil.parseInt(yellow, 0));
                }

                //红牌
                Element redCards = statistics.element("red-cards");
                if (redCards != null) {
                    red = redCards.attributeValue("red-cards");
                    fbStatistics.setRed(CommonUtil.parseInt(red, 0));
                }
            }
        } catch (Exception e) {
            logger.error("generateStatistics  error", e);
        }
        return fbStatistics;

    }
}
