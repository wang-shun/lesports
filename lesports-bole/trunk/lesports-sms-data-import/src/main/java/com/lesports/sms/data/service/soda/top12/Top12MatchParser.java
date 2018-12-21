package com.lesports.sms.data.service.soda.top12;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.StatsConstants;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.*;

/**
 * Created by zhonglin on 2016/8/29.
 */
@Service("top12MatchParser")
public class Top12MatchParser extends Parser{
    private static Logger logger = LoggerFactory.getLogger(Top12MatchParser.class);
    @Resource
    private WarningMailUtil warningMailUtil;

    public List<Match.Squad> parseSquadsData(String sodaId) {
        String matchresultFile = "s9-412-2018-" + sodaId + "-matchresult.xml";
        try {
            Match match = SbdsInternalApis.getMatchBySodaId(sodaId);
            if (match == null) {
                logger.info("soda match result parser end");
                return null;
            }
            File xmlFile = new File(Constants.SODA_LIVE_PATH + matchresultFile);
//            File xmlFile = new File("E:\\soda\\" + matchresultFile);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", matchresultFile);
                return null;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            //match相关信息
            Element matchElement = rootElement.element("Match");


            Iterator<Element> teamEventIterator = matchElement.elementIterator("Team");
            List<Match.Squad> squads = new ArrayList<Match.Squad>();

            Team homeTeam = null;
            Team awayTeam = null;

            //按队处理实时比赛结果
            while (teamEventIterator.hasNext()) {
                Element teamEvent = teamEventIterator.next();
                String tSodaId = teamEvent.attributeValue("id");
                Team team = SbdsInternalApis.getTeamBySodaId(tSodaId);
                if (team == null) {
                    continue;
                }

                String side = teamEvent.attributeValue("side");


                //队伍信息
                if (side.equals("Away")) {
                    awayTeam = team;
                } else {
                    homeTeam = team;
                }

                //判断是否有了出场阵容
                String formation = teamEvent.elementText("Formation_used");
                String newFormation = "";
                if (StringUtils.isNotBlank(formation)) {
                    for (int i = 0; i < formation.length(); i++) {
                        newFormation += formation.charAt(i) + "-";
                    }
                    newFormation = newFormation.substring(0, newFormation.length() - 1);
                }

                Element PlayerLineUpElement = teamEvent.element("PlayerLineUp");
                Match.Squad squad = new Match.Squad();
                squad.setTid(team.getId());
                squad.setPlayers(generateSimplePlayer(PlayerLineUpElement, team.getId(), match.getCsid(), match.getCid(), formation, newFormation));

                //判断出场阵容中首发是不是11人，不是的话，直接返回空

                if (CollectionUtils.isNotEmpty(squad.getPlayers())) {
                    int startingSize = 0;
                    for (Match.SimplePlayer player : squad.getPlayers()) {
                        if (player.getStarting()) {
                            startingSize++;
                        }
                    }
                    if (startingSize != 11) {
                        warningMailUtil.sendMai("SODA-12强DATA ERROR WARNING", match.getName() + "比赛阵容出错! 搜达提供的文件名：" + matchresultFile);
                        return null;
                    }
                }
                //队伍阵型
                squad.setFormation(newFormation);
                squads.add(squad);
            }
            logger.info("soda match result parser end");
            return squads;

        } catch (Exception e) {
            logger.error("parse soda match result file {}  error", matchresultFile, e);
        }
        return null;
    }

    //比赛事件
    public Boolean parseMatchActionData(String sodaId) {
        String timelineFile = "s8-412-2018-" + sodaId + "-timeline.xml";
        Boolean result = false;
        try {
            Match match = SbdsInternalApis.getMatchBySodaId(sodaId);
            if (match == null) {
                return result;
            }

            File xmlFile = new File(Constants.SODA_LIVE_PATH + timelineFile);
//            File xmlFile = new File("E:\\soda\\" + timelineFile);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", timelineFile);
                return result;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();

            //比赛信息
            Element matchElement = rootElement.element("Match");
            String homeId = matchElement.attributeValue("homeId");
            String awayId = matchElement.attributeValue("awayId");

            Team homeTeam = SbdsInternalApis.getTeamBySodaId(homeId);
            Team awayTeam = SbdsInternalApis.getTeamBySodaId(awayId);

            if (homeTeam == null || awayTeam == null) {
                return result;
            }

            //获取matchAction和moment
            Element eventsElement = matchElement.element("Events");
            //如果获取到的事件为空
            Iterator<Element> eventIterator = eventsElement.elementIterator("Event");
            List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(match.getId());
            if (!eventIterator.hasNext()) {
                if (CollectionUtils.isNotEmpty(matchActions)) {
                    warningMailUtil.sendMai("SODA-12强DATA ERROR WARNING", match.getName() + "事件为空! 搜达提供的文件名：" + timelineFile);
                }
                return result;
            }

            //删除原有matchAction

            if (CollectionUtils.isNotEmpty(matchActions)) {
                Iterator iterator1 = matchActions.iterator();
                while (iterator1.hasNext()) {
                    MatchAction matchAction = (MatchAction) iterator1.next();
                    SbdsInternalApis.deleteMatchAction(matchAction.getId());
                }
            }


            String moment = generateMatchAction(eventsElement, match.getId(), homeTeam, awayTeam);
            if (StringUtils.isNotBlank(moment)) {
                match.setMoment(moment);
            }


            result = true;
            logger.info("soda time line parser end");
        } catch (Exception e) {
            logger.error("parse time line  file {}  error", timelineFile, e);
        }
        return result;
    }


    //球队阵容
    private List<Match.SimplePlayer> generateSimplePlayer(Element playerLineUp, Long tid, Long csid, Long cid, String formation, String newFormation) {
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
                        player.setSodaId(pSodaId);
                        player.setAllowCountries(getAllowCountries());
                        player.setOnlineLanguages(getOnlineLanguage());
                        Long pid = SbdsInternalApis.savePlayer(player);
                        if(pid <=0 ){
                            continue;
                        }
                        simplePlayer.setPid(player.getId());

                        //赛季里增加球员
                        TeamSeason teamSeason = new TeamSeason();
                        List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(tid, csid);
                        if (CollectionUtils.isNotEmpty(teamSeasons)) {
                            SbdsInternalApis.addTeamPlayer(teamSeasons.get(0).getId(), player.getId(), Long.valueOf(playerNumber));
                        } else {
                            teamSeason.setCsid(csid);
                            teamSeason.setTid(tid);
                            SbdsInternalApis.saveTeamSeason(teamSeason);
                            SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(playerNumber));
                        }
                    }
                    String[] positions = newFormation.split("-");


                    long pos = 0;
                    Iterator<Element> statIterator = teamPlayer.elementIterator("Stat");
                    while (statIterator.hasNext()) {
                        Element statElement = statIterator.next();
                        if ("formation_place".equals(statElement.attributeValue("type"))) {
                            pos = LeNumberUtils.toLong(statElement.getText());
                        }
                    }
                    if (StatsConstants.squadOrderMap.get(formation + "_" + pos) != null) {
                        pos = LeNumberUtils.toLong(StatsConstants.squadOrderMap.get(formation + "_" + pos));
                    }

                    Long id = 0L;


//                    if (pos == 1) {
//                        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
//                    } else if (pos > 1 && pos <= (1 + LeNumberUtils.toLong(positions[0]))) {
//                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
//                    } else if (pos > (11 - LeNumberUtils.toLong(positions[positions.length - 1]))) {
//                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
//                    } else {
//                        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
//                    }

                    if ("门将".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
                    }
                    if ("后卫".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
                    }
                    if ("前卫".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
                    }
                    if ("前锋".equals(position)) {
                        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
                    }

                    simplePlayer.setSquadOrder(pos + "");

                    String substitute = teamPlayer.attributeValue("status");

                    //是否首发
                    if ("首发".equals(substitute)) {
                        simplePlayer.setStarting(true);
                        simplePlayer.setPosition(id);
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
                if ("进球".equals(eventName) || "乌龙球".equals(eventName)) {
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
