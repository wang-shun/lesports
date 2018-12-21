//package com.lesports.sms.data.processor.sportrard.InnerProcessor;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.hankcs.hanlp.corpus.util.StringUtils;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.CommonLiveScore;
//import com.lesports.sms.data.model.sportrard.MatchLiveScore;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.sportrard.SportrardPlayerStanding;
//import com.lesports.sms.data.model.sportrard.SportrardTeamStanding;
//import com.lesports.sms.data.processor.Processor;
//import com.lesports.sms.data.utils.CommonUtil;
//import com.lesports.sms.model.*;
//import com.lesports.utils.LeDateUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
///**
// * Created by qiaohongxin on 2016/4/1.
// */
//public class ScoccerProcessor extends Processor {
//    private static Logger logger = LoggerFactory.getLogger(ScoccerProcessor.class);
//
//    @Override
//    public Integer getRankOrderData(String type, List<SportrardPlayerStanding.PlayerStatsEntry> entrys) {
//        if (CollectionUtils.isEmpty(entrys)) return 0;
//        for (SportrardPlayerStanding.PlayerStatsEntry entry : entrys) {
//            if (type.contains("assist") && entry.getKey().equals("27")) {
//                return Integer.valueOf(entry.getValue());
//            } else if (type.contains("score") && entry.getKey().equals("4")) {
//                return Integer.valueOf(entry.getValue());
//            }
//            continue;
//        }
//        return 0;
//    }
//
//    @Override
//    public List<Match.Squad> getSquadData(CommonLiveScore liveScore1) {
//        MatchLiveScore liveScore = (MatchLiveScore) liveScore1;
//        List<Match.Squad> squads = Lists.newArrayList();
//        Match.Squad squad1 = new Match.Squad();
//        squad1.setTid(SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getHometeamId(), 469).getId());
//        squad1.setFormation(liveScore.getHomeFormation());
//        squad1.setPlayers(getSimplePlayersData(0L, 0L, liveScore.getHomeFormation(), liveScore.getHomePlayerList()));
//        Match.Squad squad2 = new Match.Squad();
//        squad2.setTid(SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getHometeamId(), 469).getId());
//        squad2.setFormation(liveScore.getAwayFormation());
//        squad2.setPlayers(getSimplePlayersData(0L, 0L, liveScore.getAwayFormation(), liveScore.getAwayPlayerList()));
//        squads.add(squad1);
//        squads.add(squad2);
//        return squads;
//    }
//
//    @Override
//    public Map<String, Object> getPlayerStandingStats(String type, List<SportrardPlayerStanding.PlayerStatsEntry> entrys) {
//        Map<String, Object> stats = Maps.newHashMap();
//        Map<String, String> currentMap = null;
//        if (type.contains("assist")) currentMap = SportrardConstants.AssistPlayerStandingStatPath;
//        else if (type.contains("goal")) currentMap = SportrardConstants.ScorePlayerStandingStatPath;
//        for (SportrardPlayerStanding.PlayerStatsEntry rowElement : entrys) {
//            String statsName = currentMap.get(rowElement.getKey());
//            if (statsName != null) stats.put(statsName, rowElement.getValue());
//        }
//        return stats;
//    }
//
//    @Override
//    public Map<String, Object> getTeamStandingStats(List<SportrardTeamStanding.LeagueTableColumn> columns) {
//        Map<String, Object> stats = Maps.newHashMap();
//        for (SportrardTeamStanding.LeagueTableColumn rowElement : columns) {
//            String statsName = SportrardConstants.FBTeamStandingStatPath.get(rowElement.getKey());
//            if (statsName != null) stats.put(statsName, rowElement.getValue());
//        }
//        return stats;
//    }
//
//    @Override
//    public String getMatchMomentData(CommonLiveScore liveScore1) {
//        MatchLiveScore liveScore = (MatchLiveScore) liveScore1;
//        if (liveScore.getMoment() == null) return null;
//        long countTime = LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(liveScore.getBetradarTime().substring(0, liveScore.getBetradarTime().length() - 5)).getTime() - LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(liveScore.getCurrentPeriodStartTime().substring(0, liveScore.getCurrentPeriodStartTime().length() - 5)).getTime();
//        long currentMatchTime = countTime / 60000L;
//        return liveScore.getMoment() + currentMatchTime;
//    }
//
//    @Override
//    public Boolean updateMatchAction(CommonLiveScore liveScore1) {
//        MatchLiveScore liveScore = (MatchLiveScore) liveScore1;
//        Match currentMatch = SbdsInternalApis.getMatchByPartnerIdAndType(liveScore.getPartenrId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//        List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(currentMatch.getId());
//        if (CollectionUtils.isNotEmpty(matchActions)) {
//            Iterator iterator1 = matchActions.iterator();
//            while (iterator1.hasNext()) {
//                MatchAction matchAction = (MatchAction) iterator1.next();
//                SbdsInternalApis.deleteMatchAction(matchAction.getId());
//            }
//        }
//        Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getHometeamId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//        Team team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getAwayteamId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//        saveCardAction(team1.getId(), team2.getId(), currentMatch.getId(), liveScore.getCardActions());
//        saveSubsAction(team1.getId(), team2.getId(), currentMatch.getId(), liveScore.getSubstitutionActions());
//        saveGoalAction(team1.getId(), team2.getId(), currentMatch.getId(), liveScore.getGaolActions());
//        return true;
//    }
//
//    private void saveCardAction(Long homeId, Long awayId, Long mid, List<MatchLiveScore.Card> actions) {
//        if (CollectionUtils.isEmpty(actions)) return;
//        for (MatchLiveScore.Card action : actions) {
//            MatchAction matchAction = new MatchAction();
//            matchAction.setPassedTime(Double.parseDouble(action.getTime()) * 60);
//            matchAction.setAllowCountries(getAllowCountries());
//            matchAction.setMid(mid);
//            if ("1".equals(action.getTeam()))
//                matchAction.setTid(homeId);
//            matchAction.setTid((awayId));
//            if ("Yellow".equals(action.getCardType())) {
//                matchAction.setType(SbdsInternalApis.getDictEntriesByName("^黄牌$").get(0).getId());
//            } else if ("Red".equals(action.getCardType()) || "YellowRed".equals(action.getCardType())) {
//                matchAction.setType(SbdsInternalApis.getDictEntriesByName("^红牌$").get(0).getId());
//            }
//            matchAction.setPid(SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(action.getPlayerId(), Integer.parseInt(SportrardConstants.partnerSourceId)).getId());
//            SbdsInternalApis.saveMatchAction(matchAction);
//        }
//    }
//
//    private void saveGoalAction(Long homeId, Long awayId, Long mid, List<MatchLiveScore.Goal> actions) {
//        if (CollectionUtils.isEmpty(actions)) return;
//        for (MatchLiveScore.Goal action : actions) {
//            MatchAction matchAction = new MatchAction();
//            Long goalDetailType = SportrardConstants.goalType.get(action.getGoalType()) == null ? 0L : SportrardConstants.goalType.get(action.getGoalType());
//            matchAction.setPassedTime(Double.parseDouble(action.getTime()) * 60);
//            matchAction.setAllowCountries(getAllowCountries());
//            matchAction.setMid(mid);
//            if ("1".equals(action.getTeam()))
//                matchAction.setTid(homeId);
//            matchAction.setTid((awayId));
//            matchAction.setType(SbdsInternalApis.getDictEntriesByName("^进球$").get(0).getId());
//            HashMap<String, Long> map = new HashMap();
//            map.put("goalType", goalDetailType);
//            matchAction.setContent(JSON.toJSONString(map));
//            // matchAction.setMultiLangConts(getMultiLang(JSON.toJSONString(map)));
//            matchAction.setPid(SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(action.getPlayerId(), Integer.parseInt(SportrardConstants.partnerSourceId)).getId());
//            SbdsInternalApis.saveMatchAction(matchAction);
//        }
//    }
//
//    private void saveSubsAction(Long homeId, Long awayId, Long mid, List<MatchLiveScore.Substitution> actions) {
//        if (CollectionUtils.isEmpty(actions)) return;
//        for (MatchLiveScore.Substitution action : actions) {
//            MatchAction matchAction = new MatchAction();
//            matchAction.setPassedTime(Double.parseDouble(action.getTime()) * 60);
//            matchAction.setType(SbdsInternalApis.getDictEntriesByName("^换人$").get(0).getId());
//            matchAction.setAllowCountries(getAllowCountries());
//            matchAction.setMid(mid);
//            if ("1".equals(action.getTeam()))
//                matchAction.setTid(homeId);
//            matchAction.setTid((awayId));
//            Player playerOutId = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(action.getOutPlayerId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//            Player playerInId = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(action.getInPlayerId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//            matchAction.setPid(playerOutId.getId());
//            HashMap<String, Long> map = new HashMap();
//            map.put("playerOut", playerOutId.getId());
//            map.put("playerIn", playerInId.getId());
//
//            matchAction.setContent(JSON.toJSONString(map));
//            //  matchAction.setMultiLangConts(getMultiLang(JSON.toJSONString(map)));
//            SbdsInternalApis.saveMatchAction(matchAction);
//        }
//    }
//
//    public List<Match.SimplePlayer> getSimplePlayersData(Long csid, Long teamId, String formation, List<MatchLiveScore.TeamPlayer> players) {
//        if (CollectionUtils.isEmpty(players)) return null;
//        List<Match.SimplePlayer> lists = Lists.newArrayList();
//        int[] pos1 = new int[3];
//        if (!StringUtils.isBlankOrNull(formation)) {
//            String[] formation1s = formation.split("-");
//            int sum1 = 0;
//            pos1[0] = Integer.parseInt(formation1s[0]) + 1;
//            for (int i = 0; i < formation1s.length - 1; i++) {
//                sum1 += Integer.parseInt(formation1s[i]);
//            }
//            pos1[1] = sum1 + 1;
//            pos1[2] = sum1 + Integer.parseInt(formation1s[formation1s.length - 1]) + 1;
//        }
//        for (MatchLiveScore.TeamPlayer teamPlayer : players) {
//            Match.SimplePlayer simplePlayer = new Match.SimplePlayer();
//            simplePlayer.setNumber(teamPlayer.getShirtNumber());
//            Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(teamPlayer.getPlayerId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//            if (null != player) {
//                simplePlayer.setPid(player.getId());
//            } else {
//                logger.warn("the player not found,partnerId:{}", teamPlayer.getPlayerId());
//                player = new Player();
//                player.setName(teamPlayer.getPlayerName());
//                player.setMultiLangNames(getMultiLang(teamPlayer.getPlayerName()));
//                player.setPartnerId(teamPlayer.getPlayerId());
//                player.setPartnerType(Integer.parseInt(SportrardConstants.partnerSourceId));
//                player.setGameFType(SbdsInternalApis.getDictEntriesByName("^足球$").get(0).getId());
//                player.setAllowCountries(getAllowCountries());
//                SbdsInternalApis.savePlayer(player);
//                logger.info("insert into player sucess,partnerId:{}", teamPlayer.getPlayerId());
//                //deal with team season
//                TeamSeason teamSeason = new TeamSeason();
//                List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
//                if (CollectionUtils.isNotEmpty(teamSeasons)) {
//                    teamSeason = teamSeasons.get(0);
//                    SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(teamPlayer.getShirtNumber()));
//                } else {
//                    teamSeason.setCsid(csid);
//                    teamSeason.setTid(teamId);
//                    SbdsInternalApis.saveTeamSeason(teamSeason);
//                    SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(teamPlayer.getShirtNumber()));
//                }
//            }
//            simplePlayer.setSquadOrder(teamPlayer.getPos());
//            simplePlayer.setPosition(teamPlayer.getPosition());
//            simplePlayer.setStarting(teamPlayer.isSubstitte());
//            lists.add(simplePlayer);
//        }
//        return lists;
//    }
//
//    public List<Match.SectionResult> getSectionResults(CommonLiveScore liveScore1, String type) {
//        MatchLiveScore liveScore = (MatchLiveScore) liveScore1;
//        Element scores = liveScore.getScores();
//        String hthomeScore = "0";
//        String htawayScore = "0";
//        String fthomeScore = "0";
//        String ftawayScore = "0";
//        String othomeScore = "0";
//        String otawayScore = "0";
//        String athomeScore = "0";
//        String atawayScore = "0";
//        List<Match.SectionResult> sectionResults = new ArrayList<>();
//        try {
//            if (scores != null) {
//                Iterator scoreIterator = scores.elementIterator("Score");
//                while (scoreIterator.hasNext()) {
//                    Element score = (Element) scoreIterator.next();
//
//                    if (score.attributeValue("type").equals("Period1")) {
//                        hthomeScore = score.elementText("Team1");
//                        htawayScore = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        if ("home".equals(type))
//                            sectionResult.setResult(hthomeScore);
//                        else
//                            sectionResult.setResult(htawayScore);
//                        sectionResult.setOrder(1);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                    if (score.attributeValue("type").equals("Normaltime")) {
//                        fthomeScore = score.elementText("Team1");
//                        ftawayScore = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        sectionResult.setOrder(2);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                    //加时赛结束
//                    if (score.attributeValue("type").equals("Overtime")) {
//                        othomeScore = score.elementText("Team1");
//                        otawayScore = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        sectionResult.setOrder(3);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^加时$").get(0).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                    //点球大战结束
//                    if (score.attributeValue("type").equals("Penalties")) {
//                        athomeScore = score.elementText("Team1");
//                        atawayScore = score.elementText("Team2");
//                        Match.SectionResult sectionResult = new Match.SectionResult();
//                        sectionResult.setOrder(4);
//                        sectionResult.setSection(SbdsInternalApis.getDictEntryByNameAndParentId("^点球$", SbdsInternalApis.getDictEntriesByName("^比赛分段$").get(0).getId()).getId());
//                        sectionResults.add(sectionResult);
//                    }
//                }
//            }
//            for (Match.SectionResult sectionResult : sectionResults) {
//                if (sectionResult.getOrder() == 2) {
//                    Integer lthomeScore = Integer.parseInt(fthomeScore) - Integer.parseInt(hthomeScore);
//                    Integer ltawayScore = Integer.parseInt(ftawayScore) - Integer.parseInt(htawayScore);
//                    if ("home".equals(type))
//                        sectionResult.setResult(lthomeScore + "");
//                    else
//                        sectionResult.setResult(ltawayScore + "");
//                }
//                if (sectionResult.getOrder() == 3) {
//                    Integer lthomeScore = Integer.parseInt(othomeScore) - Integer.parseInt(fthomeScore);
//                    Integer ltawayScore = Integer.parseInt(otawayScore) - Integer.parseInt(ftawayScore);
//                    if ("home".equals(type))
//                        sectionResult.setResult(lthomeScore + "");
//                    else
//                        sectionResult.setResult(ltawayScore + "");
//                }
//                if (sectionResult.getOrder() == 4) {
//                    Integer lthomeScore = 0;
//                    Integer ltawayScore = 0;
//                    if ("0".equals(othomeScore) && "0".equals(otawayScore)) {
//                        lthomeScore = Integer.parseInt(athomeScore) - Integer.parseInt(fthomeScore);
//                        ltawayScore = Integer.parseInt(atawayScore) - Integer.parseInt(ftawayScore);
//                    } else {
//                        lthomeScore = Integer.parseInt(athomeScore) - Integer.parseInt(othomeScore);
//                        ltawayScore = Integer.parseInt(atawayScore) - Integer.parseInt(otawayScore);
//                    }
//                    if ("home".equals(type))
//                        sectionResult.setResult(lthomeScore + "");
//                    else
//                        sectionResult.setResult(ltawayScore + "");
//                }
//            }
//        } catch (Exception e) {
//            logger.error("generateSectionResult  error", e);
//        }
//        return sectionResults;
//    }
//
//    public Set<Match.CompetitorStat> getCompetitorStatsData(CommonLiveScore liveScore1) {
//        MatchLiveScore liveScore = (MatchLiveScore) liveScore1;
//        if (liveScore.getStatistics() == null) return null;
//        Set<Match.CompetitorStat> stats = Sets.newHashSet();
//        Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getHometeamId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//        Team team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getAwayteamId(), Integer.parseInt(SportrardConstants.partnerSourceId));
//        Match.CompetitorStat homeCompetitorStat = new Match.CompetitorStat();
//        homeCompetitorStat.setCompetitorId(team1.getId());
//        Map<String, Object> homeMapStats = getCompetitorStatsMap("1", liveScore.getStatistics(), SportrardConstants.scorrerStatPath);
//        homeMapStats.put("red", getCardNum("Red", "1", liveScore.getCardActions()));
//        homeMapStats.put("yellow", getCardNum("Yellow", "1", liveScore.getCardActions()));
//        homeCompetitorStat.setStats(homeMapStats);
//        //awayTeamStat
//        Match.CompetitorStat awayCompetitorStat = new Match.CompetitorStat();
//        Map<String, Object> awayMapStats = getCompetitorStatsMap("2", liveScore.getStatistics(), SportrardConstants.scorrerStatPath);
//        awayMapStats.put("red", getCardNum("Red", "2", liveScore.getCardActions()));
//        awayMapStats.put("yellow", getCardNum("Yellow", "2", liveScore.getCardActions()));
//        awayCompetitorStat.setStats(awayMapStats);
//        awayCompetitorStat.setCompetitorId(team2.getId());
//        stats.add(homeCompetitorStat);
//        stats.add(awayCompetitorStat);
//        return stats;
//    }
//
//    private Map<String, Object> getCompetitorStatsMap(String type, Element statistics, Map<String, String> paths) {
//        Map<String, Object> stats = Maps.newHashMap();
//        Iterator iter = paths.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            String key = String.valueOf(entry.getKey());
//            String path = String.valueOf(entry.getValue()).replace("?", type);
//            stats.put(key, CommonUtil.getIntegerValue(statistics.selectObject(path)));
//        }
//        return stats;
//    }
//
//    private Integer getCardNum(String type, String teamType, List<MatchLiveScore.Card> cards) {
//        if (CollectionUtils.isEmpty(cards)) return 0;
//        Integer num = 0;
//        for (MatchLiveScore.Card card : cards)
//            if (card.getCardType().endsWith(type) && card.getTeam().equals(teamType)) num++;
//        return num;
//    }
//}
//
//
//
//
//
//
//
//
//
