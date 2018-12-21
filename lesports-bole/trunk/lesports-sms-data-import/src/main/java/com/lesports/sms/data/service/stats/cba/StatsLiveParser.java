package com.lesports.sms.data.service.stats.cba;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.lesports.id.api.IdType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageContent;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.TimeSort;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.StatConstats;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
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
 * Created by qiaohongxin on 2015/9/18.
 */

@Service("statsLiveParser")
public class StatsLiveParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(StatsLiveParser.class);
    private Map<String, MatchStatus> stateMap = ImmutableMap.of("Pre-Game", MatchStatus.MATCH_NOT_START, "In-Progress", MatchStatus.MATCHING, "Final", MatchStatus.MATCH_END);
    private Map<String, String> elementIdentifyMap = ImmutableMap.of("NBA", "nba", "CBACHN", "bk");

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
            String cName = file.contains("NBA") ? "NBA" : "CBACHN";
            String elementIdentify = elementIdentifyMap.get(cName);
            if (StringUtils.isBlank(elementIdentify)) {
                logger.warn("the file type is unknown ,not NBA or CBACHA", file);
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();
            Element boxScore = rootElement.element("sports-boxscores").element(elementIdentify + "-boxscores").element(elementIdentify + "-boxscore");
            String partnerId = boxScore.element("gamecode").attributeValue("global-id");
            Match currentMatch = SbdsInternalApis.getMatchByPartnerIdAndType(partnerId, Constants.PartnerSourceStaticId);
            if (null == currentMatch) {
                logger.warn(partnerId + "live match is not exist");
                return result;
            }
            //matchState
            MatchStats matchStats = SbdsInternalApis.getMatchStatsById(currentMatch.getId());
            if (matchStats == null) {
                matchStats = new MatchStats();
                matchStats.setAllowCountries(currentMatch.getAllowCountries());
                matchStats.setId(currentMatch.getId());
            }
            // 开始进行match upsert操作
            Match overTimeMatch = new Match();
            //match status 更新
            overTimeMatch.setStatus(currentMatch.getStatus());
            String statusId = boxScore.element("gamestate").attributeValue("status");
            if (stateMap.get(statusId) != null) {
                logger.warn("the current statusId" + statusId + "can find the  exist status");
                MatchStatus currentStatus = stateMap.get(statusId);
                currentMatch.setStatus(currentStatus);
            }
            //update 比赛当前时间状态
            Match.CurrentMoment overtimeMoment = currentMatch.getCurrentMoment();
            Match.CurrentMoment currentMoment = new Match.CurrentMoment();
            String lineScoreIdentify = "quarter";
            String quarter = boxScore.element("gamestate").attributeValue("quarter");
            if (quarter == null) {
                lineScoreIdentify = "period";
                quarter = boxScore.element("gamestate").attributeValue("period");
            }
            String minute = boxScore.element("gamestate").attributeValue("minutes");
            String second = boxScore.element("gamestate").attributeValue("seconds");
            double restTime = CommonUtil.parseDouble(minute, 15) * 60.0 + CommonUtil.parseDouble(second, 0);
            String sectionName = "第1节";
            if (CommonUtil.parseInt(quarter, 0) > 4) {
                int addtime = CommonUtil.parseInt(quarter, 0) - 4;
                sectionName = "加时" + addtime;
            } else {
                sectionName = "第" + quarter + "节";
            }
            currentMoment.setSection(getDictIdByName(sectionName));
            currentMoment.setSectionName(sectionName);
            currentMoment.setTime(restTime);
            currentMoment.setSort(TimeSort.DESC);
            currentMatch.setCurrentMoment(currentMoment);
            overTimeMatch.setCurrentMoment(overtimeMoment);
            //cet current competetiors 信息
            String homeTeamPartnerId = boxScore.element("home-team").element("team-code").attributeValue("global-id");
            String visitingTeamPartnerId = boxScore.element("visiting-team").element("team-code").attributeValue("global-id");
            Team homeTeam = SbdsInternalApis.getTeamByStatsId(homeTeamPartnerId);
            Team visitingTeam = SbdsInternalApis.getTeamByStatsId(visitingTeamPartnerId);
            //判定时候已构建两个球队的信息
            if (null == homeTeam || null == visitingTeam) {
                logger.warn("teams can not be found,partner1Id:{}" + homeTeamPartnerId + ",partner2Id:{}" + visitingTeamPartnerId);
                return result;
            }
            //更新Match.competions 信息
            Set<Match.Competitor> currentCompetitors = new HashSet<>();
            Set<Match.Competitor> outDateCompetitors = new HashSet<>();
            Set<Match.CompetitorStat> competitorStats = new HashSet<>();
            if (null != currentMatch.getCompetitors() && !currentMatch.getCompetitors().isEmpty()) {
                Iterator competitorIterator = currentMatch.getCompetitors().iterator();
                while (competitorIterator.hasNext()) {
                    Match.Competitor competitor = (Match.Competitor) competitorIterator.next();
                    if (competitor.getCompetitorId().equals(homeTeam.getId())) {
                        upsertMatchCompetitor(lineScoreIdentify, competitor, boxScore, currentCompetitors, outDateCompetitors, competitorStats, "0");
                        logger.info("home competitor information is updated end" + competitor.getCompetitorId());
                    }
                    if (competitor.getCompetitorId().equals(visitingTeam.getId())) {
                        upsertMatchCompetitor(lineScoreIdentify, competitor, boxScore, currentCompetitors, outDateCompetitors, competitorStats, "1");
                        logger.info("visiting competitor information is updated end" + competitor.getCompetitorId());
                    }
                }
            } else {
                Match.Competitor homeCompetitor = new Match.Competitor();
                homeCompetitor.setCompetitorId(homeTeam.getId());
                homeCompetitor.setGround(GroundType.HOME);
                homeCompetitor.setType(CompetitorType.TEAM);
                upsertMatchCompetitor(lineScoreIdentify, homeCompetitor, boxScore, currentCompetitors, outDateCompetitors, competitorStats, "0");
                logger.info("home competitor information is saved end");
                Match.Competitor visitingCompetitor = new Match.Competitor();
                visitingCompetitor.setCompetitorId(visitingTeam.getId());
                visitingCompetitor.setType(CompetitorType.TEAM);
                visitingCompetitor.setGround(GroundType.AWAY);
                upsertMatchCompetitor(lineScoreIdentify, visitingCompetitor, boxScore, currentCompetitors, outDateCompetitors, competitorStats, "1");
                logger.info("home competitor information is saved end");
            }
            currentMatch.setCompetitors(currentCompetitors);
            overTimeMatch.setCompetitors(outDateCompetitors);
            currentMatch.setCompetitorStats(competitorStats);
            List<Match.Squad> squads = createSquadList(boxScore.element("player-stats"), homeTeam, visitingTeam, currentMatch);
            currentMatch.setSquads(squads);
            matchStats.setCompetitorStats(CompetitorStatToStats(competitorStats, squads));    //add stats
            logger.info("match team and player squard and statics informatin are saved en information is saved");
            Set<Match.BestPlayerStat> bestPlayerStats = createBestPlyerStats(currentMatch.getCsid(), homeTeam.getId(), visitingTeam.getId(), squads);
            currentMatch.setBestPlayerStats(bestPlayerStats);
            if (saveMatch(currentMatch)) {
                logger.info("update match success,matchId:" + currentMatch.getId() + ",matchName:" + currentMatch.getName() + ",matchPartnerId:" + currentMatch.getPartnerId() +
                        ",matchStatus:" + currentMatch.getStatus());
            }
            if (saveMatchState(matchStats)) {
                logger.info("save matchState success,matchId:" + currentMatch.getId() + ",matchName:" + currentMatch.getName() + ",matchPartnerId:" + currentMatch.getPartnerId() +
                        ",matchStatus:" + currentMatch.getStatus());
                if (currentMatch.getStatus().equals(MatchStatus.MATCH_END) && currentMatch.getStage() != null && currentMatch.getCid().longValue() == 44001L && currentMatch.getStage().longValue() == Constants.regularSeasonId) {
                    MessageContent messageContent = new MessageContent();
                    messageContent.addToMsgBody("cid", currentMatch.getCid().toString()).addToMsgBody("csid", currentMatch.getId().toString());
                    LeMessage message = LeMessageBuilder.create().setEntityId(currentMatch.getId())
                            .setIdType(IdType.COMPETITOR_SEASON_STAT).setContent(messageContent).build();
                    sendMessage(message);
                }
            }
            return true;

        } catch (Exception e) {
            logger.error("parse NBA_livescore error", e);
        }
        return result;
    }


    /**
     * 获取实时比赛的最佳球员
     *
     * @param homeId
     * @param visitId
     * @param squadList
     * @return
     */
    private Set<Match.BestPlayerStat> createBestPlyerStats(Long csid, Long homeId, Long visitId, List<Match.Squad> squadList) {
        Set<Match.BestPlayerStat> bestPlayerStats = new HashSet<Match.BestPlayerStat>();
        Match.BestPlayerStat homeTeamBestPlayerStats = new Match.BestPlayerStat();
        Match.BestPlayerStat visitingTeamBestPlayerStats = new Match.BestPlayerStat();
        List<Match.SimplePlayer> homeTeamBestPlayers = new ArrayList<Match.SimplePlayer>();
        List<Match.SimplePlayer> visitingTeamBestPlayers = new ArrayList<Match.SimplePlayer>();
        ;
        //得分，三分，篮板，助攻，罚球， 抢断，盖帽
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
                    String playerNumber = player.getNumber().toString();
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

    /**
     * 获取当前比赛的主客队阵容
     *
     * @param playerStats
     * @param homeTeam
     * @param visitingTeam
     * @param currentMatch
     * @return
     */
    private List<Match.Squad> createSquadList(Element playerStats, Team homeTeam, Team visitingTeam, Match currentMatch) {
        List<Match.Squad> list = new ArrayList<Match.Squad>();
        //主队的阵容
        Match.Squad squad1 = new Match.Squad();
        squad1.setTid(homeTeam.getId());
        squad1.setPlayers(generateSimplePlayer(playerStats, "0", homeTeam.getId(), currentMatch.getCsid(), currentMatch.getCid()));
        //客队的阵容
        Match.Squad squad2 = new Match.Squad();
        squad2.setTid(visitingTeam.getId());
        squad2.setPlayers(generateSimplePlayer(playerStats, "1", visitingTeam.getId(), currentMatch.getCsid(), currentMatch.getCid()));
        list.add(squad1);
        list.add(squad2);
        return list;
    }

    /**
     * 获取指定球队的队员阵容详情和技术统计数据详情
     *
     * @param playerstats
     * @param type
     * @param teamId
     * @param csid
     * @param cid
     * @return
     */
    private List<Match.SimplePlayer> generateSimplePlayer(Element playerstats, String type, Long teamId, Long csid, Long cid) {
        List<Match.SimplePlayer> list = new ArrayList<Match.SimplePlayer>();
        if (null != playerstats) {
            Iterator playerIterator = null;
            if (type.equals("0")) {
                playerIterator = playerstats.elementIterator("home-player-stats");
            }
            if (type.equals("1")) {
                playerIterator = playerstats.elementIterator("visiting-player-stats");
            }
            if (null == playerIterator) {
                logger.warn("player infotmation is empty");
                return null;
            }
            while (playerIterator.hasNext()) {
                Match.SimplePlayer simplePlayer = new Match.SimplePlayer();
                Element teamPlayer = (Element) playerIterator.next();
                String pid = teamPlayer.element("player-code").attributeValue("global-id");
                String pName = teamPlayer.element("name").attributeValue("first-name") + " " + teamPlayer.element("name").attributeValue("last-name");
                Long playerId = getPlayerBypartnerId(teamId, cid, pid, pName);
                simplePlayer.setPid(playerId);
                String playerNumber = "-1";
                List<TeamSeason> season = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
                if (season != null && !season.isEmpty() && season.get(0).getPlayers() != null) {
                    for (TeamSeason.TeamPlayer player : season.get(0).getPlayers()) {
                        if (player.getPid().longValue() != playerId.longValue())
                            continue;
                        playerNumber = player.getNumber().toString();
                    }
                }
                simplePlayer.setNumber(Integer.parseInt(playerNumber));
                Long id = 0L;
                try {
                    String pos = teamPlayer.element("games").attributeValue("position");
                    if (pos != null) id = getDictIdByName(Constants.basketBallPositionMap.get(pos));
                    simplePlayer.setPosition(id);
                    String substitute = teamPlayer.element("games").attributeValue("games-started");
                    if (StringUtils.isNotEmpty(substitute))
                        simplePlayer.setStarting(substitute.equals("0") ? false : true);
                    String isplay = teamPlayer.element("games").attributeValue("games");
                    if (StringUtils.isNotEmpty(isplay)) {
                        int dnp = 1 - CommonUtil.parseInt(isplay, 0);
                        simplePlayer.setDnp(dnp);

                    }
                    String onCourt = teamPlayer.element("games").attributeValue("on-court");
                    if (StringUtils.isNotEmpty(onCourt)) {
                        simplePlayer.setIsOnCourt(onCourt.equalsIgnoreCase("true") ? "1" : "0");
                    }
                } catch (Exception e) {
                    logger.warn("the file not contain the element of game :player");
                }
                Map<String, Object> map = getNBAStats(teamPlayer, StatConstats.NBALiveStatPath);
                simplePlayer.setStats(map);
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
    private Long getPlayerBypartnerId(Long teamId, Long cid, String partnerId, String pName) {
        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, Constants.PartnerSourceStaticId);
        if (player != null) return player.getId();
        Competition competition = SbdsInternalApis.getCompetitionById(cid);
        player = new Player();
        player.setName(pName);
        player.setMultiLangNames(getMultiLang(pName));
        player.setPartnerId(partnerId);
        player.setPartnerType(Constants.PartnerSourceStaticId);
        player.setDeleted(false);
        player.setGameFType(competition.getGameFType());
        player.setAllowCountries(competition.getAllowCountries());
        player.setOnlineLanguages(getOnlineLanguage());
        Long id = SbdsInternalApis.savePlayer(player);
        logger.info("insert into player sucess,partnerId:");
        return id;
    }


    /**
     * g更新两个team的信息
     *
     * @param competitor
     * @param nbaBoxScore
     * @param newSet
     * @param oldSet
     * @param statsSet
     * @param teamType
     */
    private void upsertMatchCompetitor(String identify, Match.Competitor competitor, Element nbaBoxScore, Set<Match.Competitor> newSet, Set<Match.Competitor> oldSet, Set<Match.CompetitorStat> statsSet, String teamType) {
        Match.Competitor oldCompetitor = new Match.Competitor();
        Match.CompetitorStat competitorStat = new Match.CompetitorStat();
        oldCompetitor.setFinalResult(competitor.getFinalResult());
        oldCompetitor.setCompetitorId(competitor.getCompetitorId());
        Map<String, Object> map = Maps.newHashMap();
        if (teamType.equalsIgnoreCase("0")) {
            oldCompetitor.setGround(GroundType.HOME);
            competitor.setFinalResult(nbaBoxScore.element("home-team").element("linescore").attributeValue("score"));
            List<Match.SectionResult> results = generatePeriodResult(identify, nbaBoxScore.element("home-team").element("linescore"));
            if (CollectionUtils.isEmpty(results)) {
                competitor.setSectionResults(oldCompetitor.getSectionResults());
            }
            competitor.setSectionResults(results);
            map = getNBAStats(nbaBoxScore.element("team-stats").element("home-team-stats"), StatConstats.NBALiveStatPath);
        }
        if (teamType.equalsIgnoreCase("1")) {
            oldCompetitor.setGround(GroundType.AWAY);
            competitor.setFinalResult(nbaBoxScore.element("visiting-team").element("linescore").attributeValue("score"));
            List<Match.SectionResult> results = generatePeriodResult(identify, nbaBoxScore.element("visiting-team").element("linescore"));
            if (CollectionUtils.isEmpty(results)) {
                competitor.setSectionResults(oldCompetitor.getSectionResults());
            }
            competitor.setSectionResults(results);
            map = getNBAStats(nbaBoxScore.element("team-stats").element("visiting-team-stats"), StatConstats.NBALiveStatPath);
        }

        competitorStat.setStats(map);
        competitorStat.setCompetitorId(competitor.getCompetitorId());
        oldCompetitor.setType(CompetitorType.TEAM);
        newSet.add(competitor);
        oldSet.add(oldCompetitor);
        statsSet.add(competitorStat);
    }

    /**
     * 构建当前比赛的小节成绩
     *
     * @param linescore
     * @return
     */
    private List<Match.SectionResult> generatePeriodResult(String identify, Element linescore) {
        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            if (null == linescore) {
                return null;
            }
            Iterator scoreIterator = linescore.elementIterator(identify);
            while (scoreIterator.hasNext()) {
                Element quarter = (Element) scoreIterator.next();
                Match.SectionResult sectionResult = new Match.SectionResult();
                sectionResult.setResult(quarter.attributeValue("score"));
                sectionResult.setOrder(CommonUtil.parseInt(quarter.attributeValue(identify), 0));
                String quarter1 = quarter.attributeValue(identify);
                String sectionName = "第1节$";
                if (CommonUtil.parseInt(quarter1, 0) > 4) {
                    int addtime = CommonUtil.parseInt(quarter1, 0) - 4;
                    sectionName = "加时" + addtime + "$";
                } else {
                    sectionName = "第" + quarter1 + "节$";
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


}

