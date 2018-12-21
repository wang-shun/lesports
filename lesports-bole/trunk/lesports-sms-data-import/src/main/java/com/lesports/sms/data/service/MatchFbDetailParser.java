package com.lesports.sms.data.service;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.FbStatistics;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by lufei1 on 2014/11/25.
 */
@Service("matchFbDetailParser")
public class MatchFbDetailParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(MatchFbDetailParser.class);

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
            //查找赛事Id
            String gameF = rootElement.element("Sport").attribute("name").getValue();
            Element tournament = rootElement.element("Sport").element("Category").element("Tournament");
            String gameS = tournament.attribute("uniqueTournamentId").getValue();
            String season = tournament.element("Season").attribute("start").getValue().substring(0, 4);
            String name = Constants.nameMap.get(gameS);
            String sportsType = Constants.sportsTypeMap.get(rootElement.element("Sport").attributeValue("id"));
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType(name, gameFType);
            if (CollectionUtils.isEmpty(competitions)) {
                logger.warn("can not find relative event,name:{}", name);
                return result;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionSeason == null) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{},season:{}", gameS, name, season);
                return result;
            }
            Iterator matches = tournament.element("Matches").elementIterator("Match");
            while (matches.hasNext()) {
                Element match = (Element) matches.next();
                String partnerId = match.attribute("id").getValue();

                Match match1 = SbdsInternalApis.getMatchByPartnerIdAndType(partnerId, Integer.parseInt(Constants.partnerSourceId));


                // 没有比赛
                if (match1 != null) {
                    logger.warn("matchFbDetail:the match may be not exists,partnerId:{},fileName:{}", partnerId, file);
                    continue;
                }
                match1.setStatus(MatchStatus.MATCH_END);
                // 比赛没有结束
//				if (match1.getStatus() != MatchStatus.MATCH_END){
//				logger.warn("matchFbDetail:the match is not ended,partnerId:{}", partnerId);
//					continue;
//				}


                //如果有锁则不更新
                if (match1 != null && match1.getLock() != null && match1.getLock() == true) {
                    logger.warn("matchFbDetail:the match may be locked,partnerId:{},fileName:{}", partnerId, file);
                    continue;
                }

                Iterator teams = match.element("Teams").elementIterator("Team");
                String homePartnerId = "";
                String awayPartnerId = "";
                String homeScore = "0";
                String awayScore = "0";
                String fthomeScore = "0";
                String ftawayScore = "0";
                String othomeScore = "0";
                String otawayScore = "0";
                String athomeScore = "0";
                String atawayScore = "0";
                String homeName = "";
                String awayName = "";
                String homeFormation = "";
                String awayFormation = "";
                Team team1 = new Team();
                Team team2 = new Team();
                Map<String, Object> homeStat = null;
                Map<String, Object> awayStat = null;
                List<Match.SimplePlayer> homeSquads = null;
                List<Match.SimplePlayer> awaySquads = null;
                while (teams.hasNext()) {
                    Element team = (Element) teams.next();
                    if (team.attribute("type").getValue().equals("1")) {
                        homePartnerId = team.attribute("id").getValue();
                        homeFormation = team.attributeValue("formation");
                        team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(homePartnerId, Integer.parseInt(Constants.partnerSourceId));
                        if (team1 == null) {
                            logger.warn("cannot find related team,homePartnerId:{},fileName:{}", homePartnerId, file);
                            return result;
                        }
                        homeName = team1.getName();
                        FbStatistics fbStatistics = generateStatistics(match, team, "1", team1.getId());
                        homeStat = CommonUtil.convertBeanToMap(fbStatistics);
                        homeSquads = generateSimplePlayer(team, competitions.get(0).getId(), competitionSeason.getId(), team1);
                    } else if (team.attribute("type").getValue().equals("2")) {
                        awayPartnerId = team.attribute("id").getValue();
                        awayFormation = team.attributeValue("formation");
                        team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(awayPartnerId, Integer.parseInt(Constants.partnerSourceId));
                        if (team2 == null) {
                            logger.warn("cannot find related team,awayPartnerId:{},fileName:{}", awayPartnerId, file);
                            return result;
                        }
                        awayName = team2.getName();
                        FbStatistics fbStatistics = generateStatistics(match, team, "2", team2.getId());
                        awayStat = CommonUtil.convertBeanToMap(fbStatistics);
                        awaySquads = generateSimplePlayer(team, competitions.get(0).getId(), competitionSeason.getId(), team2);
                    }
                }
                // 比赛结果
                Element matchResult = match.element("Result");
                if (matchResult != null) {
                    if (matchResult.attributeValue("canceled").equals("true")) {
                        logger.warn("matchFbDetail:the match may be canceled,partnerId:{},fileName:{}", partnerId, file);
                        continue;
                    }
                    Iterator<Element> scoreEleIt = matchResult.elementIterator("Score");
                    boolean isap = false;
                    boolean isot = false;
                    while (scoreEleIt.hasNext()) {
                        Element scoreEle = scoreEleIt.next();
                        if (scoreEle.attributeValue("type").equals("FT")) {
                            String score = scoreEle.attributeValue("value");
                            String[] scoreArr = score.split(":");
                            fthomeScore = scoreArr[0];
                            ftawayScore = scoreArr[1];
                            homeScore = scoreArr[0];
                            awayScore = scoreArr[1];
                        }
                        //加时赛结束
                        if (scoreEle.attributeValue("type").equals("OT")) {
                            String score = scoreEle.attributeValue("value");
                            String[] scoreArr = score.split(":");
                            othomeScore = scoreArr[0];
                            otawayScore = scoreArr[1];
                            isot = true;
                        }
                        //点球大战结束
                        if (scoreEle.attributeValue("type").equals("AP")) {
                            String score = scoreEle.attributeValue("value");
                            String[] scoreArr = score.split(":");
                            athomeScore = scoreArr[0];
                            atawayScore = scoreArr[1];
                            isap = true;
                        }
                    }
                    if (isap) {
                        homeScore = athomeScore;
                        awayScore = atawayScore;
                    } else if (isot) {
                        homeScore = othomeScore;
                        awayScore = otawayScore;
                    }
                }


                Element goals = match.element("Goals");
                if (goals != null) {
                    Iterator goalIterator = goals.elementIterator("Goal");
                    while (goalIterator.hasNext()) {
                        Element goal = (Element) goalIterator.next();
                        String time = goal.attributeValue("time");
                        String ScoringTeam = goal.attributeValue("team");
                        if (ScoringTeam.equals("1")) {
                            logger.info("matchFbDetail:matchName: " + match1.getName() + "，比赛进行" + time + "分钟时" + homeName + "进球了！！！比赛开始时间：" + match.attributeValue("dateOfMatch"));
                        } else if (ScoringTeam.equals("2")) {
                            logger.info("matchFbDetail:matchName: " + match1.getName() + "，比赛进行" + time + "分钟时" + awayName + "进球了！！！比赛开始时间：" + match.attributeValue("dateOfMatch"));
                        }
                    }
                }


                Set<Match.Competitor> competitors = match1.getCompetitors();
                Set<Match.Competitor> competitors2 = new HashSet<>();
                Set<Match.CompetitorStat> competitorStats = new HashSet<>();
                //List<Match.Squad> squads = match1.getSquads();
                if (null != competitors && competitors.size() > 0) {
                    Iterator competitorIterator = competitors.iterator();
                    while (competitorIterator.hasNext()) {
                        Match.Competitor competitor = (Match.Competitor) competitorIterator.next();
                        Match.CompetitorStat competitorStat = new Match.CompetitorStat();
                        if (competitor.getCompetitorId().equals(team1.getId())) {
                            competitor.setFinalResult(homeScore);
                            competitorStat.setCompetitorId(competitor.getCompetitorId());
                            competitorStat.setStats(homeStat);
                            if (matchResult != null) {
                                Element scoreEle = (Element) matchResult.elementIterator("Score").next();
                                competitor.setSectionResults(generateSectionResult(matchResult, "home"));
                            }
                        }
                        if (competitor.getCompetitorId().equals(team2.getId())) {
                            competitor.setFinalResult(awayScore);
                            competitorStat.setCompetitorId(competitor.getCompetitorId());
                            competitorStat.setStats(awayStat);
                            if (matchResult != null) {
                                Element scoreEle = (Element) matchResult.elementIterator("Score").next();
                                competitor.setSectionResults(generateSectionResult(matchResult, "away"));
                            }
                        }
                        competitors2.add(competitor);
                        competitorStats.add(competitorStat);
                    }
                } else {
                    Match.Competitor homeCompetitor = new Match.Competitor();
                    Match.Competitor awayCompetitor = new Match.Competitor();
                    Match.CompetitorStat homeCompetitorStat = new Match.CompetitorStat();
                    Match.CompetitorStat awayCompetitorStat = new Match.CompetitorStat();
                    homeCompetitor.setCompetitorId(team1.getId());
                    homeCompetitor.setType(CompetitorType.TEAM);
                    awayCompetitor.setCompetitorId(team2.getId());
                    awayCompetitor.setType(CompetitorType.TEAM);
                    homeCompetitorStat.setCompetitorId(homeCompetitor.getCompetitorId());
                    homeCompetitorStat.setStats(homeStat);
                    awayCompetitorStat.setCompetitorId(awayCompetitor.getCompetitorId());
                    awayCompetitorStat.setStats(awayStat);
                    competitors2.add(homeCompetitor);
                    competitors2.add(awayCompetitor);
                    competitorStats.add(homeCompetitorStat);
                    competitorStats.add(awayCompetitorStat);
                }
                match1.setCompetitors(competitors2);
                match1.setCompetitorStats(competitorStats);
                Match.Squad squad1 = new Match.Squad();
                squad1.setTid(team1.getId());
                squad1.setFormation(homeFormation);
                squad1.setPlayers(homeSquads);

                Match.Squad squad2 = new Match.Squad();
                squad2.setTid(team2.getId());
                squad2.setFormation(awayFormation);
                squad2.setPlayers(awaySquads);

                List<Match.Squad> squads = new ArrayList<Match.Squad>();
                squads.add(squad1);
                squads.add(squad2);
                match1.setSquads(squads);

                List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(match1.getId());
                if (CollectionUtils.isNotEmpty(matchActions)) {
                    while (matchActions.iterator().hasNext()) {
                        MatchAction matchAction = matchActions.iterator().next();
                        SbdsInternalApis.deleteMatchAction(matchAction.getId());
                        matchActions = SbdsInternalApis.getMatchActionsByMid(match1.getId());
                    }
                }
                generateMatchAction(match, match1.getId(), team1.getId(), team2.getId());
                match1.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                boolean isSuccess = false;
                int tryCount = 0;
                while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
                    try {
                        isSuccess = SbdsInternalApis.saveMatch(match1) > 0;
                    } catch (Exception e) {
                        logger.error("fail to update match. id : {}. sleep and try again.", match1.getId(), e);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                logger.info("matchFbDetail:update matchFbDetail success,{},matchName:{},matchPartnerId:{},fileName:{}", match1.getCompetitorStats().toString(), match1.getName(), match1.getPartnerId(), file);
            }
            result = true;
        } catch (DocumentException e) {
            logger.error("parse match" + file + " xml error", e);
        }
        return result;
    }


    private List<Match.SectionResult> generateSectionResult(Element matchResult, String type) {
        String hthomeScore = "0";
        String htawayScore = "0";
        String fthomeScore = "0";
        String ftawayScore = "0";
        String othomeScore = "0";
        String otawayScore = "0";
        String athomeScore = "0";
        String atawayScore = "0";
        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            Iterator<Element> scoreEleIt = matchResult.elementIterator("Score");
            while (scoreEleIt.hasNext()) {
                Element scoreEle = scoreEleIt.next();
                if (scoreEle.attributeValue("type").equals("HT")) {
                    String score = scoreEle.attributeValue("value");
                    String[] scoreArr = score.split(":");
                    hthomeScore = scoreArr[0];
                    htawayScore = scoreArr[1];
                    Match.SectionResult sectionResult = new Match.SectionResult();
                    if ("home".equals(type))
                        sectionResult.setResult(hthomeScore);
                    else
                        sectionResult.setResult(htawayScore);
                    sectionResult.setOrder(1);
                    sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
                    sectionResults.add(sectionResult);
                }

                if (scoreEle.attributeValue("type").equals("FT")) {
                    String score = scoreEle.attributeValue("value");
                    String[] scoreArr = score.split(":");
                    fthomeScore = scoreArr[0];
                    ftawayScore = scoreArr[1];
                    Match.SectionResult sectionResult = new Match.SectionResult();
                    sectionResult.setOrder(2);
                    sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
                    sectionResults.add(sectionResult);
                }
                //加时赛结束
                if (scoreEle.attributeValue("type").equals("OT")) {
                    String score = scoreEle.attributeValue("value");
                    String[] scoreArr = score.split(":");
                    othomeScore = scoreArr[0];
                    otawayScore = scoreArr[1];
                    Match.SectionResult sectionResult = new Match.SectionResult();
                    sectionResult.setOrder(3);
                    sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^加时$").get(0).getId());
                    sectionResults.add(sectionResult);
                }
                //点球大战结束
                if (scoreEle.attributeValue("type").equals("AP")) {
                    String score = scoreEle.attributeValue("value");
                    String[] scoreArr = score.split(":");
                    athomeScore = scoreArr[0];
                    atawayScore = scoreArr[1];
                    Match.SectionResult sectionResult = new Match.SectionResult();
                    sectionResult.setOrder(4);
                    DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("^点球$", SbdsInternalApis.getDictEntriesByName("^比赛分段$").get(0).getId());
                    if (null != dictEntry)
                        sectionResult.setSection(dictEntry.getId());
                    sectionResults.add(sectionResult);
                }
            }

            for (Match.SectionResult sectionResult : sectionResults) {
                if (sectionResult.getOrder() == 2) {
                    Integer lthomeScore = Integer.parseInt(fthomeScore) - Integer.parseInt(hthomeScore);
                    Integer ltawayScore = Integer.parseInt(ftawayScore) - Integer.parseInt(htawayScore);
                    if ("home".equals(type))
                        sectionResult.setResult(lthomeScore + "");
                    else
                        sectionResult.setResult(ltawayScore + "");
                }
                if (sectionResult.getOrder() == 3) {
                    Integer lthomeScore = Integer.parseInt(othomeScore) - Integer.parseInt(fthomeScore);
                    Integer ltawayScore = Integer.parseInt(otawayScore) - Integer.parseInt(ftawayScore);
                    if ("home".equals(type))
                        sectionResult.setResult(lthomeScore + "");
                    else
                        sectionResult.setResult(ltawayScore + "");
                }
                if (sectionResult.getOrder() == 4) {
                    Integer lthomeScore = 0;
                    Integer ltawayScore = 0;
                    if ("0".equals(othomeScore) && "0".equals(otawayScore)) {
                        lthomeScore = Integer.parseInt(athomeScore) - Integer.parseInt(fthomeScore);
                        ltawayScore = Integer.parseInt(atawayScore) - Integer.parseInt(ftawayScore);
                    } else {
                        lthomeScore = Integer.parseInt(athomeScore) - Integer.parseInt(othomeScore);
                        ltawayScore = Integer.parseInt(atawayScore) - Integer.parseInt(otawayScore);
                    }
                    if ("home".equals(type))
                        sectionResult.setResult(lthomeScore + "");
                    else
                        sectionResult.setResult(ltawayScore + "");
                }
            }
        } catch (Exception e) {
            logger.error("generateSectionResult error", e);
        }
        return sectionResults;
    }

    private List<Match.SimplePlayer> generateSimplePlayer(Element team, Long cid, Long csid, Team team1) {
        List<Match.SimplePlayer> simplePlayers = new ArrayList<Match.SimplePlayer>();
        try {
            Element lineups = team.element("Lineups");
            String formation1 = team.attributeValue("formation");
            String gender = team.attributeValue("gender");
            int[] pos1 = new int[3];
            if (StringUtils.isNotEmpty(formation1)) {


                String[] formation1s = formation1.split("-");

                int sum1 = 0;
                pos1[0] = Integer.parseInt(formation1s[0]) + 1;
                for (int i = 0; i < formation1s.length - 1; i++) {
                    sum1 += Integer.parseInt(formation1s[i]);
                }
                pos1[1] = sum1 + 1;
                pos1[2] = sum1 + Integer.parseInt(formation1s[formation1s.length - 1]) + 1;
            }

            if (null != lineups) {
                Iterator lineupIterator = lineups.elementIterator("Lineup");

                while (lineupIterator.hasNext()) {
                    Element lineup = (Element) lineupIterator.next();

                    Iterator playerIterator = lineup.element("Players").elementIterator("Player");
                    String playeNumber = "-1";
                    while (playerIterator.hasNext()) {
                        Match.SimplePlayer simplePlayer = new Match.SimplePlayer();
                        Long id = 0L;
                        Element player = (Element) playerIterator.next();
                        String pid = player.attributeValue("playerID");
                        String pos = player.attributeValue("matchPosistion");
                        Element playerInfo = player.element("PlayerInfo");
                        if (null != playerInfo) {
                            Element playerInfoEntry = playerInfo.element("PlayerInfoEntry");
                            if (null != playerInfoEntry)
                                playeNumber = playerInfoEntry.attributeValue("value");
                        }
                        if (StringUtils.isNotEmpty(playeNumber)) {
                            simplePlayer.setNumber(Integer.parseInt(playeNumber));
                        }
                        Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(pid, Integer.parseInt(Constants.partnerSourceId));

                        if (null == player1) {
                            logger.warn("the player not found,partnerId:{}", pid);
                            player1 = new Player();
                            player1.setName(player.attributeValue("playerName"));
                            player1.setMultiLangNames(getMultiLang(player.attributeValue("playerName")));
                            Element translation = player.element("Translation");
                            if (null != translation) {
                                Iterator translationIterator = translation.elementIterator("TranslatedValue");
                                while (translationIterator.hasNext()) {
                                    Element translatedValue = (Element) translationIterator.next();
                                    if ("zh".equals(translatedValue.attributeValue("lang")))
                                        player1.setName(translatedValue.attributeValue("value"));
                                    player1.setMultiLangNames(getMultiLang(translatedValue.attributeValue("value")));
                                }
                            }
                            if ("F".equals(gender))
                                player1.setGender(Gender.FEMALE);
                            else {
                                player1.setGender(Gender.MALE);
                            }
                            player1.setPartnerId(pid);
                            player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                            player1.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                            player1.setDeleted(false);
                            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^足球$");
                            if (CollectionUtils.isNotEmpty(dictEntries)) {
                                player1.setGameFType(dictEntries.get(0).getId());
                            }
                            Set<Long> cids = player1.getCids();
                            cids.add(cid);
                            player1.setCids(cids);
                            player1.setAllowCountries(getAllowCountries());
                            player1.setOnlineLanguages(getOnlineLanguage());
                            SbdsInternalApis.savePlayer(player1);
                            logger.info("insert into player sucess,partnerId:" + pid);
                        }
                        //暂时过滤掉中超的阵容
                        if (47001L != cid) {
                            TeamSeason teamSeason = new TeamSeason();
                            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team1.getId(), csid);
                            if (CollectionUtils.isNotEmpty(teamSeasons)) {
                                teamSeason = teamSeasons.get(0);
                                SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player1.getId(), Long.valueOf(playeNumber));
                            } else {
                                teamSeason.setCsid(csid);
                                teamSeason.setTid(team1.getId());
                                SbdsInternalApis.saveTeamSeason(teamSeason);
                                SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player1.getId(), Long.valueOf(playeNumber));
                            }
                        }
                        simplePlayer.setPid(player1.getId());
                        if ("false".equals(lineup.attributeValue("Substitutes"))) {
                            simplePlayer.setStarting(true);
                        } else {
                            simplePlayer.setStarting(false);
                        }
                        if (StringUtils.isNotEmpty(pos)) {
                            int postion = Integer.parseInt(pos);
                            if (1 == postion) {
                                id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
                            }
                            if (1 < postion && postion <= pos1[0]) {
                                id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
                            }
                            if (pos1[0] < postion && postion <= pos1[1]) {
                                id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
                            }
                            if (pos1[1] < postion && postion <= pos1[2]) {
                                id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
                            }
                        }
                        simplePlayer.setSquadOrder(pos);
                        simplePlayer.setPosition(id);
                        simplePlayers.add(simplePlayer);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("generateSimplePlayer error", e);
        }
        return simplePlayers;
    }


    private void generateMatchAction(Element match, Long matchId, Long teamId1, Long teamId2) {
        Long yellowType;
        Long redType;
        Long goalType;
        try {
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

            List clist = match.selectNodes("./Cards");
            if (CollectionUtils.isNotEmpty(clist)) {
                Iterator cardEntrys = match.element("Cards").elementIterator("Card");
                while (cardEntrys.hasNext()) {
                    MatchAction matchAction = new MatchAction();
                    matchAction.setAllowCountries(getAllowCountries());
                    Element cardEntrysInfo = (Element) cardEntrys.next();
                    String cardType = cardEntrysInfo.attributeValue("color");
                    String playerTeam = cardEntrysInfo.attributeValue("team");
                    matchAction.setPassedTime(Double.parseDouble(cardEntrysInfo.attributeValue("time")) * 60);
                    matchAction.setMid(matchId);
                    if ("1".equals(playerTeam))
                        matchAction.setTid(teamId1);
                    else
                        matchAction.setTid((teamId2));
                    if ("Yellow".equals(cardType)) {
                        matchAction.setType(yellowType);
                    }
                    if ("Red".equals(cardType) || "YellowRed".equals(cardType)) {
                        matchAction.setType(redType);
                    }
                    Element playerElement = cardEntrysInfo.element("Player");
                    if (null != playerElement) {
                        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerElement.attributeValue("playerID"), Integer.parseInt(Constants.partnerSourceId));
                        if (null != player) {
                            matchAction.setPid(player.getId());
                        } else {
                            logger.warn("the player not found,partnerId:{}", cardEntrysInfo.element("Player").attributeValue("playerID"));
                            continue;
                        }
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                }

            }

            List glist = match.selectNodes("./Goals");
            if (CollectionUtils.isNotEmpty(glist)) {
                Iterator goalEntrys = match.element("Goals").elementIterator("Goal");
                while (goalEntrys.hasNext()) {
                    MatchAction matchAction = new MatchAction();
                    matchAction.setAllowCountries(getAllowCountries());
                    Element goalEntrysInfo = (Element) goalEntrys.next();
                    String socringTeam = goalEntrysInfo.attributeValue("team");
                    matchAction.setPassedTime(Double.parseDouble(goalEntrysInfo.attributeValue("time")) * 60);
                    matchAction.setMid(matchId);
                    if ("1".equals(socringTeam))
                        matchAction.setTid(teamId1);
                    else
                        matchAction.setTid((teamId2));
                    matchAction.setType(goalType);
                    Element goalPlayer = goalEntrysInfo.element("Player");
                    if (null != goalPlayer) {
                        String playerPartnerId = goalPlayer.attributeValue("playerID");
                        if (StringUtils.isNotEmpty(playerPartnerId)) {
                            Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerPartnerId, Integer.parseInt(Constants.partnerSourceId));
                            if (null != player) {
                                matchAction.setPid(player.getId());
                            } else {
                                logger.warn("the player not found,partnerId:{}", goalEntrysInfo.element("Player").attributeValue("playerID"));
                                continue;
                            }
                        }
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                }
            }
        } catch (Exception e) {
            logger.error("generateMatchAction error", e);
        }
    }

    private FbStatistics generateStatistics(Element match, Element team, String type, Long teamId) {
        FbStatistics fbStatistics = new FbStatistics();
        try {
            Element statistics = team.element("Matchstatistics");
            if (null == statistics) {
                logger.warn("matchFbDetail:matchDetail is broken,Matchstatistics is not exists, teamId:{}", teamId);
                return null;
            }
            int possession_percentage = CommonUtil.parseInt(statistics.attributeValue("ballpossession"), 0);
            int goalkeeperKicks = CommonUtil.parseInt(statistics.attributeValue("goalkeeperKicks"), 0);
            int goalkeeperSaves = CommonUtil.parseInt(statistics.attributeValue("goalkeeperSaves"), 0);
            int throwIns = CommonUtil.parseInt(statistics.attributeValue("throwIns"), 0);
            int ontarget_scoring_att = CommonUtil.parseInt(statistics.attributeValue("shotsOnGoal"), 0);
            int offtarget_scoring_att = CommonUtil.parseInt(statistics.attributeValue("shotsOffGoal"), 0);
            int fk_foul_won = CommonUtil.parseInt(statistics.attributeValue("freeKicks"), 0);
            int won_corners = CommonUtil.parseInt(statistics.attributeValue("corners"), 0);
            int fk_foul_lost = CommonUtil.parseInt(statistics.attributeValue("fouls"), 0);
            int off_sides = CommonUtil.parseInt(statistics.attributeValue("offsides"), 0);
            int yellow = 0;
            int red = 0;


            fbStatistics.setBallPossession(possession_percentage);
            fbStatistics.setShotsOnGoal(ontarget_scoring_att);
            fbStatistics.setShotsOffGoal(offtarget_scoring_att);
            fbStatistics.setFreeKicks(fk_foul_won);
            fbStatistics.setCornerKicks(won_corners);
            fbStatistics.setFouls(fk_foul_lost);
            fbStatistics.setThrowIns(throwIns);
            fbStatistics.setGoalKicks(goalkeeperKicks);
            fbStatistics.setGoalkeeperSaves(goalkeeperSaves);
            fbStatistics.setOffsides(off_sides);


            List clist = match.selectNodes("./Cards");
            if (CollectionUtils.isNotEmpty(clist)) {
                Iterator cardEntrys = match.element("Cards").elementIterator("Card");
                while (cardEntrys.hasNext()) {
                    Element cardEntrysInfo = (Element) cardEntrys.next();
                    String teamType = cardEntrysInfo.attributeValue("team");
                    String cardType = cardEntrysInfo.attributeValue("color");

                    if (cardType.equals("Yellow")) {
                        if (type.equals(teamType)) {
                            ++yellow;
                        }
                    } else if (cardType.equals("Red") || "YellowRed".equals(cardType)) {
                        if (type.equals(teamType)) {
                            ++red;
                        }
                    }
                }

            }

            fbStatistics.setYellow(yellow);
            fbStatistics.setRed(red);
        } catch (Exception e) {
            logger.error("generateStatistics error", e);
        }
        return fbStatistics;

    }


}
