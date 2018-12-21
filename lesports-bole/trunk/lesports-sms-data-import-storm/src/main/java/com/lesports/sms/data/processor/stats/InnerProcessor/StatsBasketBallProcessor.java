package com.lesports.sms.data.processor.stats.InnerProcessor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.sms.api.common.TimeSort;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.processor.Processor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by qiaohongxin on 2016/4/1.
// */
//public class StatsBasketBallProcessor extends Processor {
//    private static Logger logger = LoggerFactory.getLogger(StatsBasketBallProcessor.class);

//    public List<Match.SectionResult> getSectionResults(CommonLiveScore liveScore1, String type) {
//        List<Match.SectionResult> sectionResults = new ArrayList<>();
//        StatsLiveScore liveScore = (StatsLiveScore) liveScore1;
//        Element currentSection = null;
//        if (type.equals("HOME")) {
//            currentSection = liveScore.getHomeSectiohScores();
//        } else {
//            currentSection = liveScore.getAwaySectiohScore();
//        }
//        Iterator scoreIterator = currentSection.elementIterator("quarter");
//        while (scoreIterator.hasNext()) {
//            Element quarter = (Element) scoreIterator.next();
//            Match.SectionResult sectionResult = new Match.SectionResult();
//            sectionResult.setResult(quarter.attributeValue("score"));
//            sectionResult.setOrder(CommonUtil.parseInt(quarter.attributeValue("quarter"), 0));
//            String quarter1 = quarter.attributeValue("quarter");
//            String sectionName = "第1节$";
//            if (CommonUtil.parseInt(quarter1, 0) > 4) {
//                int addtime = CommonUtil.parseInt(quarter1, 0) - 4;
//                sectionName = "加时" + addtime + "$";
//            } else {
//                sectionName = "第" + quarter1 + "节$";
//            }
//            sectionResult.setSection(SbdsInternalApis.getDictEntriesByName(sectionName).get(0).getId());
//            sectionResults.add(sectionResult);
//        }
//        return sectionResults;
//    }
//
//
//    public List<Match.Squad> getSquadData(CommonLiveScore liveScore1) {
//        StatsLiveScore liveScore = (StatsLiveScore) liveScore1;
//        List<Match.Squad> squads = Lists.newArrayList();
//        Match.Squad squad1 = new Match.Squad();
//        squad1.setTid(SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getHomeTeamId(), 499).getId());
//        squad1.setPlayers(getSimplePlayersData(liveScore.getPlayerStatics(), "1", squad1.getTid(), 0L, 0L));
//        Match.Squad squad2 = new Match.Squad();
//        squad2.setTid(SbdsInternalApis.getTeamByPartnerIdAndPartnerType(liveScore.getAwayTeamId(), 469).getId());
//        squad2.setPlayers(getSimplePlayersData(liveScore.getPlayerStatics(), "1", squad2.getTid(), 0L, 0L));
//        squads.add(squad1);
//        squads.add(squad2);
//        return squads;
//    }
//
//    /**
//     * 获取指定球队的队员阵容详情和技术统计数据详情
//     *
//     * @param playerstats
//     * @param type
//     * @param teamId
//     * @param csid
//     * @param cid
//     * @return
//     */
//    private List<Match.SimplePlayer> getSimplePlayersData(Element playerstats, String type, Long teamId, Long csid, Long cid) {
//        List<Match.SimplePlayer> list = new ArrayList<Match.SimplePlayer>();
//        if (null != playerstats) {
//            Iterator playerIterator = null;
//            if (type.equals("0")) {
//                playerIterator = playerstats.elementIterator("home-player-stats");
//            }
//            if (type.equals("1")) {
//                playerIterator = playerstats.elementIterator("visiting-player-stats");
//            }
//            if (null == playerIterator) {
//                logger.warn("player infotmation is empty");
//                return null;
//            }
//            while (playerIterator.hasNext()) {
//                Match.SimplePlayer simplePlayer = new Match.SimplePlayer();
//                Element teamPlayer = (Element) playerIterator.next();
//                String pid = teamPlayer.element("player-code").attributeValue("global-id");
//                String pName = teamPlayer.element("name").attributeValue("display-name");
//                String substitute = teamPlayer.element("games").attributeValue("games-started");
//                Long playerId = getPlayerBypartnerId(teamId, csid, pid, pName);
//                simplePlayer.setPid(playerId);
//                String playerNumber = "-1";
//                List<TeamSeason> season = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
//                if (season != null && !season.isEmpty() && season.get(0).getPlayers() != null) {
//                    for (TeamSeason.TeamPlayer player : season.get(0).getPlayers()) {
//                        if (player.getPid().longValue() != playerId.longValue())
//                            continue;
//                        playerNumber = player.getNumber().toString();
//
//                    }
//                }
//                simplePlayer.setNumber(Integer.parseInt(playerNumber));
//                Long id = 0L;
//                String pos = teamPlayer.element("games").attributeValue("position");
//                if (StringUtils.isNotEmpty(pos)) {
//                    logger.info("thecurre pos of the player" + playerId + pos);
//
//                    if (pos.equalsIgnoreCase("F") || pos.equalsIgnoreCase("PF") || pos.equalsIgnoreCase("SF")) {
//                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
//                    }
//                    if (pos.equalsIgnoreCase("C")) {
//
//                        id = SbdsInternalApis.getDictEntriesByName("^中锋$").get(0).getId();
//                    }
//                    if (pos.equalsIgnoreCase("G") || pos.equalsIgnoreCase("PG") || pos.equalsIgnoreCase("SG")) {
//                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
//                    }
//                }
//                logger.info("the position of the player" + playerId + id);
//                simplePlayer.setPosition(id);
//                if ("0".equals(substitute)) {
//                    simplePlayer.setStarting(false);
//                } else {
//                    simplePlayer.setStarting(true);
//                }
//                String isplay = teamPlayer.element("games").attributeValue("games");
//                int dnp = 1 - CommonUtil.parseInt(isplay, 0);
//                simplePlayer.setDnp(dnp);
//                String isOncourt = teamPlayer.element("games").attributeValue("on-court").equalsIgnoreCase("true") ? "1" : "0";
//                simplePlayer.setIsOnCourt(isOncourt);
//                //TODO Map<String, Object> map = get;
//                // simplePlayer.setStats(map);
//                list.add(simplePlayer);
//                logger.info("simplePlayer is builed and added to list sucessfully");
//            }
//        }
//        return list;
//    }
//
//    private Long getPlayerBypartnerId(Long teamId, Long csid, String partnerId, String pName) {
//        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, 499);
//        if (null == player) {
//            logger.warn("the player not found,partnerId:{}", partnerId);
//            player = new Player();
//            player.setName(pName);
//            player.setMultiLangNames(getMultiLang(pName));
//            player.setPartnerId(partnerId);
//            player.setPartnerType(499);
//            player.setDeleted(false);
//            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^篮球$");
//            if (CollectionUtils.isNotEmpty(dictEntries)) {
//                player.setGameFType(dictEntries.get(0).getId());
//            }
//            player.setAllowCountries(getAllowCountries());
//            SbdsInternalApis.savePlayer(player);
//            logger.info("insert into player sucess,partnerId:");
//        }
//        return SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, 499).getId();
//    }
//
//
//    @Override
//    public Match.CurrentMoment getCurrentMomentData(CommonLiveScore liveScore1) {
//        StatsLiveScore liveScore = (StatsLiveScore) liveScore1;
//        Match.CurrentMoment currentMoment = new Match.CurrentMoment();
//        double restTime = CommonUtil.parseDouble(((StatsLiveScore) liveScore1).getRestMinutes(), 15) * 60.0 + CommonUtil.parseDouble(liveScore.getCurrentSeconds(), 0);
//        String sectionName = "第1节";
//        if (CommonUtil.parseInt(liveScore.getCurrentQuarter(), 0) > 4) {
//            int addtime = CommonUtil.parseInt(liveScore.getCurrentQuarter(), 0) - 4;
//            sectionName = "加时" + addtime;
//        } else {
//            sectionName = "第" + liveScore.getCurrentQuarter() + "节";
//        }
//        currentMoment.setSection(SbdsInternalApis.getDictEntriesByName(sectionName + "$") == null ? 0L : SbdsInternalApis.getDictEntriesByName(sectionName + "$").get(0).getId());
//        currentMoment.setSectionName(sectionName);
//        currentMoment.setTime(restTime);
//        currentMoment.setSort(TimeSort.DESC);
//        return currentMoment;
//    }
//
//
//    public Set<Match.CompetitorStat> getCompetitorStatsData(MatchLiveScore liveScore) {
//
//        return null;
//    }
//
//    public Map<String, Object> getDivisionStandingStats(Element element) {
//        Map<String, Object> maps = Maps.newHashMap();
//        Double percentage = CommonUtil.getStatDoubleValue(element.selectObject("./winning-percentage/@percentage"));
//        Double pointWinPic = CommonUtil.getStatDoubleValue(element.selectObject("./points-for-per-game/@points"));
//        Double gameBack = CommonUtil.getStatDoubleValue(element.selectObject("./games-back/@number"));
//        maps.put("percentage", percentage);
//        maps.put("pointWinPic", pointWinPic);
//        maps.put("gameBack", gameBack);
//        return maps;
//
//    }
//
//    public Map<String, Object> getConferenceStandingStats(Element element) {
//        Map<String, Object> maps = Maps.newHashMap();
//        Double percentage = CommonUtil.getStatDoubleValue(element.selectObject("./winning-percentage/@percentage"));
//        Double pointWinPic = CommonUtil.getStatDoubleValue(element.selectObject("./points-for-per-game/@points"));
//        Double gameBack = CommonUtil.getStatDoubleValue(element.selectObject("./conference-games-back/@number"));
//        maps.put("percentage", percentage);
//        maps.put("pointWinPic", pointWinPic);
//        maps.put("gameBack", gameBack);
//        return maps;
//
//    }
//
//}
//

