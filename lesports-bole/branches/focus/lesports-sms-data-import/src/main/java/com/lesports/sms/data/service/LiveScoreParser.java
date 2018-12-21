package com.lesports.sms.data.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.TimeSort;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.FbStatistics;
import com.lesports.sms.data.service.soda.SodaFormationParser;
import com.lesports.sms.data.service.soda.top12.Top12MatchParser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
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
import java.io.IOException;
import java.util.*;

@Service("liveScoreParser")
public class LiveScoreParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(LiveScoreParser.class);

    @Resource
    private SodaFormationParser sodaFormationParser;
    @Resource
    private Top12MatchParser matchParser;

    @Override
    public Boolean parseData(String file) {
        logger.info("live score  parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();
            String betradar = "-1";
            String liveSocreType = rootElement.attributeValue("type");
            List betradarList = rootElement.selectNodes("//BetradarLivescoreData");
            if (CollectionUtils.isNotEmpty(betradarList)) {
                Element element = (Element) betradarList.get(0);
                try {
                    betradar = element.attributeValue("generatedAt");
                } catch (Exception ex) {
                    logger.warn("文件名为：" + file + " 没有push文件时间");
                }
            }
            List sportList = rootElement.selectNodes("//Sport");
            Iterator<Element> sportIterator = sportList.iterator();
            while (sportIterator.hasNext()) {
                Element sport = sportIterator.next();
                String sportType = sport.attributeValue("BetradarSportId");
                List tourList = sport.selectNodes("Category/Tournament");

                Iterator iterator = tourList.iterator();
                while (iterator.hasNext()) {
                    Element tournament = (Element) iterator.next();
                    String gameS = tournament.attributeValue("UniqueTournamentId");
                    //五大联赛、中超...
                    if (Constants.nameMap.keySet().contains(gameS)) {
                        //中超转soda
//                        if (gameS.equals("649")) continue;
                        Iterator matches = tournament.elementIterator("Match");
                        while (matches.hasNext()) {
                            Element match = (Element) matches.next();
                            String partnerId = match.attributeValue("Id");
                            logger.info("begin update the match,partnerId:{}", partnerId);
                            Match match1 = SbdsInternalApis.getMatchByPartnerIdAndType(partnerId, Integer.parseInt(Constants.partnerSourceId));
                            //如果有锁则不更新
                            if (match1 != null && match1.getLock() != null && match1.getLock() == true) {
                                logger.warn("the match may be locked,partnerId:{}", partnerId);
                                continue;
                            }

                            if (match1 != null) {
                                Match match2 = new Match();
                                match2.setStatus(match1.getStatus());
                                //比赛状态 0,60,61：未开始 100,110,120,70,80,90：已结束 其他：比赛中
                                String matchStatus = match.element("Status").attributeValue("Code");
                                String team1Id = match.element("Team1").attributeValue("UniqueTeamId");
                                String team2Id = match.element("Team2").attributeValue("UniqueTeamId");

                                Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(team1Id, Integer.parseInt(Constants.partnerSourceId));
                                Team team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(team2Id, Integer.parseInt(Constants.partnerSourceId));
                                if (null == team1 || null == team2) {
                                    logger.warn("the team not found,partner1Id:{},partner2Id:{}", team1Id, team2Id);
                                    continue;
                                }
                                //更新赛程实时比分
                                Element scores = match.element("Scores");
                                String teamScore1 = "";
                                String teamScore2 = "";
                                if (scores != null) {
                                    Iterator scoreIterator = scores.elementIterator("Score");
                                    while (scoreIterator.hasNext()) {
                                        Element score = (Element) scoreIterator.next();
                                        if ("Current".equals(score.attributeValue("type"))) {
                                            teamScore1 = score.elementText("Team1");
                                            teamScore2 = score.elementText("Team2");
                                            break;
                                        }
                                    }
                                }
                                Byte matchStatusByte = new Byte("0");
                                if ("100".equals(matchStatus) || "110".equals(matchStatus) || "120".equals(matchStatus) || "70".equals(matchStatus) || "80".equals(matchStatus) || "90".equals(matchStatus)) {
                                    matchStatusByte = new Byte("2");
                                } else if ("0".equals(matchStatus) || "60".equals(matchStatus) || "61".equals(matchStatus)) {
                                    matchStatusByte = new Byte("0");
                                } else {
                                    matchStatusByte = new Byte("1");
                                }
                                match1.setStatus(MatchStatus.findByValue(matchStatusByte));
                                Set<Match.Competitor> competitors = match1.getCompetitors();
                                Set<Match.Competitor> competitors2 = new HashSet<>();
                                Set<Match.Competitor> oldCompetitors = new HashSet<>();
                                Set<Match.CompetitorStat> competitorStats = new HashSet<>();
                                if (null != competitors && competitors.size() > 0) {
                                    Iterator competitorIterator = competitors.iterator();
                                    while (competitorIterator.hasNext()) {
                                        Match.Competitor competitor = (Match.Competitor) competitorIterator.next();
                                        Match.Competitor oldCompetitor = new Match.Competitor();
                                        Match.CompetitorStat competitorStat = null;
                                        if (competitor.getCompetitorId().equals(team1.getId())) {
                                            oldCompetitor.setFinalResult(competitor.getFinalResult());
                                            oldCompetitor.setCompetitorId(competitor.getCompetitorId());
                                            oldCompetitor.setGround(GroundType.HOME);
                                            competitor.setFinalResult(teamScore1);

                                            if (Constants.SOCCERID.equals(sportType) || Constants.FUSTAL.equals(sportType)) {
                                                FbStatistics fbStatistics = generateStatistics(match, "1", team1.getId());
                                                if (fbStatistics != null) {
                                                    competitorStat = new Match.CompetitorStat();
                                                    Map<String, Object> map = CommonUtil.convertBeanToMap(fbStatistics);
                                                    competitorStat.setStats(map);
                                                    competitorStat.setCompetitorId(competitor.getCompetitorId());
                                                }
                                                competitor.setSectionResults(generateSectionResult(match, "home"));

                                            } else if (Constants.AMERICAFOOTBALLID.equals(sportType) || Constants.BASKETBALLID.equals(sportType)) {
                                                competitor.setSectionResults(generateNFLSectionResult(match, "home"));
                                            }


                                        }
                                        if (competitor.getCompetitorId().equals(team2.getId())) {
                                            oldCompetitor.setFinalResult(competitor.getFinalResult());
                                            oldCompetitor.setCompetitorId(competitor.getCompetitorId());
                                            oldCompetitor.setGround(GroundType.AWAY);
                                            competitor.setFinalResult(teamScore2);
                                            if (Constants.SOCCERID.equals(sportType) || Constants.FUSTAL.equals(sportType)) {
                                                FbStatistics fbStatistics = generateStatistics(match, "2", team2.getId());
                                                if (fbStatistics != null) {
                                                    competitorStat = new Match.CompetitorStat();
                                                    Map<String, Object> map = CommonUtil.convertBeanToMap(fbStatistics);
                                                    competitorStat.setStats(map);
                                                    competitorStat.setCompetitorId(competitor.getCompetitorId());
                                                }
                                                competitor.setSectionResults(generateSectionResult(match, "away"));

                                            } else if (Constants.AMERICAFOOTBALLID.equals(sportType) || Constants.BASKETBALLID.equals(sportType)) {
                                                competitor.setSectionResults(generateNFLSectionResult(match, "away"));
                                            }


                                        }
                                        competitors2.add(competitor);
                                        oldCompetitors.add(oldCompetitor);
                                        if (competitorStat != null) {
                                            competitorStats.add(competitorStat);
                                        }
                                    }
                                } else {
                                    Match.Competitor homeCompetitor = new Match.Competitor();
                                    Match.Competitor awayCompetitor = new Match.Competitor();
                                    homeCompetitor.setCompetitorId(team1.getId());
                                    homeCompetitor.setGround(GroundType.HOME);
                                    homeCompetitor.setType(CompetitorType.TEAM);
                                    awayCompetitor.setCompetitorId(team2.getId());
                                    awayCompetitor.setType(CompetitorType.TEAM);
                                    awayCompetitor.setGround(GroundType.AWAY);
                                    competitors2.add(homeCompetitor);
                                    competitors2.add(awayCompetitor);
                                    oldCompetitors.add(homeCompetitor);
                                    oldCompetitors.add(awayCompetitor);
                                }
                                match1.setCompetitors(competitors2);
                                match2.setCompetitors(oldCompetitors);
                                match1.setCompetitorStats(competitorStats);
                                if (Constants.SOCCERID.equals(sportType)) {
                                    match1.setSquads(getMatchSquad(gameS, match, match1, team1, team2));
                                    updateMatchAction(match, match1, team1.getId(), team2.getId());
                                    updateMatchScore(match1, match2, match, betradar, liveSocreType, matchStatus);
                                    //matchState
                                    updateMatchStats(match1);

                                } else if ((Constants.FUSTAL.equals(sportType) || Constants.BASEBALL.equals(sportType) || Constants.AMERICAFOOTBALLID.equals(sportType) || Constants.BASKETBALLID.equals(sportType)) && isMatchPropertyChange(match2, match1)) {
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
                                    logger.info("update match success,matchId:" + match1.getId() + ",matchName:" + match1.getName() + ",matchPartnerId:" + match1.getPartnerId() +
                                            ",matchStatus:" + match1.getStatus() + "文件push时间：" + betradar);
                                }

                            }
                        }
                    }
                }
            }
            result = true;
            logger.info("live score parser end");
        } catch (Exception e) {
            try {
                FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
                ftpUtil.loginFtp(60);
                ftpUtil.downloadFile(file.substring(file.lastIndexOf(File.separator)), "/letv/logs/", "//Sport//livescore//");
                ftpUtil.logOutFtp();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            logger.error("parse livescore_letv_4505_delta  error", e);
        }
        return result;
    }

    private void updateMatchAction(Element match, Match match1, Long teamId1, Long teamId2) {
        if (match1.getCsid().longValue() == Constants.TOP12_COMPETITION_SEASON_ID) {
            matchParser.parseMatchActionData(match1.getSodaId());
            return;
        }
        List<MatchAction> matchActions = SbdsInternalApis.getMatchActionsByMid(match1.getId());
        if (CollectionUtils.isNotEmpty(matchActions)) {
            Iterator iterator1 = matchActions.iterator();
            while (iterator1.hasNext()) {
                MatchAction matchAction = (MatchAction) iterator1.next();
                SbdsInternalApis.deleteMatchAction(matchAction.getId());
            }
        }
        generateMatchAction(match, match1.getId(), teamId1, teamId2);
        logger.warn("the current match:{},matchAction is updated", match1.getId());
    }

    public void updateMatchStats(Match match1) {
        MatchStats matchStats = SbdsInternalApis.getMatchStatsById(match1.getId());
        if (matchStats == null) {
            matchStats = new MatchStats();
            matchStats.setAllowCountries(getAllowCountries());
            matchStats.setId(match1.getId());
        }
        matchStats.setCompetitorStats(CompetitorStatToStats(match1.getCompetitorStats(), match1.getSquads()));
        saveMatchState(matchStats);
        logger.warn("the current match:{},Stats is saved", match1.getId());
    }

    private List<Match.Squad> getMatchSquad(String gameS, Element match, Match match1, Team homeTeam, Team awayTeam) {
        List<Match.Squad> squads = Lists.newArrayList();
        Match.Squad squad1 = new Match.Squad();
        squad1.setTid(homeTeam.getId());
        String formation1 = match.element("Team1").attributeValue("formation");
        logger.warn("the current team" + homeTeam.getId() + "team1 fomation is" + formation1);
        squad1.setFormation(formation1);
        squad1.setPlayers(generateSimplePlayer(match, "1", homeTeam.getId(), match1.getCsid(), match1.getCid()));
        Match.Squad squad2 = new Match.Squad();
        squad2.setTid(awayTeam.getId());
        String formation2 = match.element("Team2").attributeValue("formation");
        logger.warn("the current match" + match1.getPartnerId() + "team2 fomation is" + formation2);
        squad2.setFormation(formation2);
        squad2.setPlayers(generateSimplePlayer(match, "2", awayTeam.getId(), match1.getCsid(), match1.getCid()));
        if (gameS.equals("649")) {
            squads = sodaFormationParser.parseSquads(match1, squad1, squad2);
            if (CollectionUtils.isEmpty(squads)) {
                squads.add(squad1);
                squads.add(squad2);
            }
            return squads;
        } else if (match1.getCsid().longValue() == Constants.TOP12_COMPETITION_SEASON_ID) {
            squads = matchParser.parseSquadsData(match1.getSodaId());
            if (CollectionUtils.isEmpty(squads)) squads = match1.getSquads();
            return squads;
        }
        squads.add(squad1);
        squads.add(squad2);
        return squads;
    }

    private List<Match.SectionResult> generateNFLSectionResult(Element match, String type) {
        String homeScore1 = "0";
        String awayScore1 = "0";
        String homeScore2 = "0";
        String awayScore2 = "0";
        String homeScore3 = "0";
        String awayScore3 = "0";
        String homeScore4 = "0";
        String awayScore4 = "0";
        String homeScore5 = "0";
        String awayScore5 = "0";
        Element scores = match.element("Scores");
        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            if (scores != null) {
                Iterator scoreIterator = scores.elementIterator("Score");
                while (scoreIterator.hasNext()) {
                    Element score = (Element) scoreIterator.next();

                    if (score.attributeValue("type").equals("Period1")) {
                        homeScore1 = score.elementText("Team1");
                        awayScore1 = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        if ("home".equals(type))
                            sectionResult.setResult(homeScore1);
                        else
                            sectionResult.setResult(awayScore1);
                        sectionResult.setOrder(1);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第1节$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }

                    if (score.attributeValue("type").equals("Period2")) {
                        homeScore2 = score.elementText("Team1");
                        awayScore2 = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        if ("home".equals(type))
                            sectionResult.setResult(homeScore2);
                        else
                            sectionResult.setResult(awayScore2);
                        sectionResult.setOrder(2);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第2节$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }
                    if (score.attributeValue("type").equals("Period3")) {
                        homeScore3 = score.elementText("Team1");
                        awayScore3 = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        if ("home".equals(type))
                            sectionResult.setResult(homeScore3);
                        else
                            sectionResult.setResult(awayScore3);
                        sectionResult.setOrder(3);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第3节$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }
                    if (score.attributeValue("type").equals("Period4")) {
                        homeScore4 = score.elementText("Team1");
                        awayScore4 = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        if ("home".equals(type))
                            sectionResult.setResult(homeScore4);
                        else
                            sectionResult.setResult(awayScore4);
                        sectionResult.setOrder(4);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^第4节$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }
                    if (score.attributeValue("type").equals("Overtime")) {
                        homeScore5 = score.elementText("Team1");
                        awayScore5 = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        if ("home".equals(type))
                            sectionResult.setResult(homeScore5);
                        else
                            sectionResult.setResult(awayScore5);
                        sectionResult.setOrder(5);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^加时$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }

                }
            }
            for (Match.SectionResult sectionResult : sectionResults) {
                if (sectionResult.getOrder() == 5) {
                    Integer othomeScore = Integer.parseInt(homeScore5) - (Integer.parseInt(homeScore1) + Integer.parseInt(homeScore2) + Integer.parseInt(homeScore3) + Integer.parseInt(homeScore4));
                    Integer otawayScore = Integer.parseInt(awayScore5) - (Integer.parseInt(awayScore1) + Integer.parseInt(awayScore2) + Integer.parseInt(awayScore3) + Integer.parseInt(awayScore4));
                    if ("home".equals(type))
                        sectionResult.setResult(othomeScore + "");
                    else
                        sectionResult.setResult(otawayScore + "");
                }
            }
        } catch (Exception e) {
            logger.error("generateSectionResult  error", e);
        }

        return sectionResults;
    }

    private List<Match.SectionResult> generateSectionResult(Element match, String type) {
        String hthomeScore = "0";
        String htawayScore = "0";
        String fthomeScore = "0";
        String ftawayScore = "0";
        String othomeScore = "0";
        String otawayScore = "0";
        String athomeScore = "0";
        String atawayScore = "0";
        Element scores = match.element("Scores");
        List<Match.SectionResult> sectionResults = new ArrayList<>();
        try {
            if (scores != null) {
                Iterator scoreIterator = scores.elementIterator("Score");
                while (scoreIterator.hasNext()) {
                    Element score = (Element) scoreIterator.next();

                    if (score.attributeValue("type").equals("Period1")) {
                        hthomeScore = score.elementText("Team1");
                        htawayScore = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        if ("home".equals(type))
                            sectionResult.setResult(hthomeScore);
                        else
                            sectionResult.setResult(htawayScore);
                        sectionResult.setOrder(1);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }

                    if (score.attributeValue("type").equals("Normaltime")) {
                        fthomeScore = score.elementText("Team1");
                        ftawayScore = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setOrder(2);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }
                    //加时赛结束
                    if (score.attributeValue("type").equals("Overtime")) {
                        othomeScore = score.elementText("Team1");
                        otawayScore = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setOrder(3);
                        sectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^加时$").get(0).getId());
                        sectionResults.add(sectionResult);
                    }
                    //点球大战结束
                    if (score.attributeValue("type").equals("Penalties")) {
                        athomeScore = score.elementText("Team1");
                        atawayScore = score.elementText("Team2");
                        Match.SectionResult sectionResult = new Match.SectionResult();
                        sectionResult.setOrder(4);
                        sectionResult.setSection(SbdsInternalApis.getDictEntryByNameAndParentId("^点球$", SbdsInternalApis.getDictEntriesByName("^比赛分段$").get(0).getId()).getId());
                        sectionResults.add(sectionResult);
                    }
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
            logger.error("generateSectionResult  error", e);
        }
        return sectionResults;
    }

    private List<Match.SimplePlayer> generateSimplePlayer(Element match, String type, Long teamId, Long csid, Long cid) {
        List<Match.SimplePlayer> simplePlayers = new ArrayList<Match.SimplePlayer>();
        try {
            Element lineups = match.element("Lineups");
            String formation1 = match.element("Team" + type).attributeValue("formation");
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
                Iterator playerIterator = lineups.elementIterator("TeamPlayer");

                while (playerIterator.hasNext()) {
                    Match.SimplePlayer simplePlayer = new Match.SimplePlayer();

                    Element teamPlayer = (Element) playerIterator.next();
                    String pid = teamPlayer.element("Player").attributeValue("id");
                    String pName = teamPlayer.selectSingleNode("./Player/Name[@language='zh']").getText();
                    String team = teamPlayer.elementText("PlayerTeam");
                    String playerNumber = "-1";
                    playerNumber = teamPlayer.elementText("ShirtNumber");
                    String pos = teamPlayer.attributeValue("pos");
                    if (StringUtils.isNotEmpty(playerNumber))
                        simplePlayer.setNumber(Integer.parseInt(playerNumber));
                    if (type.equals(team)) {
                        Long id = 0L;
                        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(pid, Integer.parseInt(Constants.partnerSourceId));
                        String substitute = teamPlayer.elementText("Substitute");
                        if (null != player) {
                            simplePlayer.setPid(player.getId());
                        } else {
                            logger.warn("the player not found,partnerId:{}", pid);
                            player = new Player();
                            player.setName(pName);
                            player.setMultiLangNames(getMultiLang(pName));
                            player.setPartnerId(pid);
                            player.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
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
                            Long playerId = SbdsInternalApis.savePlayer(player);
                            player.setId(playerId);
                            simplePlayer.setPid(playerId);
                            logger.info("insert into player sucess,partnerId:" + pid);
                            //暂时过滤掉中超的阵容
                            if (47001L != cid) {
                                TeamSeason teamSeason = new TeamSeason();
                                List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
                                if (CollectionUtils.isNotEmpty(teamSeasons)) {
                                    teamSeason = teamSeasons.get(0);
                                    SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(playerNumber));
                                } else {
                                    teamSeason.setCsid(csid);
                                    teamSeason.setTid(teamId);
                                    SbdsInternalApis.saveTeamSeason(teamSeason);
                                    SbdsInternalApis.addTeamPlayer(teamSeason.getId(), player.getId(), Long.valueOf(playerNumber));
                                }
                            }
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
                        logger.info("the current match" + match.attributeValue("Id") + "add  pos to simplePlayer" + pos + simplePlayer.getSquadOrder());

                        if ("1".equals(substitute)) {
                            simplePlayer.setStarting(false);
                            simplePlayer.setPosition(player.getPosition() == null ? id : player.getPosition());
                        } else {
                            simplePlayer.setStarting(true);
                            simplePlayer.setPosition(id);
                        }
                        simplePlayers.add(simplePlayer);

                    }
                }


            }
        } catch (Exception e) {
            logger.error("generateSimplePlayer  error", e);
        }
        return simplePlayers;
    }

    private void generateMatchAction(Element match, Long matchId, Long teamId1, Long teamId2) {
        Long yellowType;
        Long redType;
        Long goalType;
        Long subtitutionType;
        Long goalDetailType;
        try {
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^黄牌$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                yellowType = dictEntries.get(0).getId();
            } else {
                logger.warn("no dict 黄牌 found,the current matchId:{}", matchId);
                return;
            }
            List<DictEntry> dictEntries1 = SbdsInternalApis.getDictEntriesByName("^红牌$");
            if (CollectionUtils.isNotEmpty(dictEntries1)) {
                redType = dictEntries1.get(0).getId();
            } else {
                logger.warn("no dict 红牌 found the current matchId:{}", matchId);
                return;
            }

            List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^进球$");
            if (CollectionUtils.isNotEmpty(dictEntries2)) {
                goalType = dictEntries2.get(0).getId();
            } else {
                logger.warn("no dict 进球 found the current matchId:{}", matchId);
                return;
            }
            List<DictEntry> dictEntries3 = SbdsInternalApis.getDictEntriesByName("^换人$");
            if (CollectionUtils.isNotEmpty(dictEntries3)) {
                subtitutionType = dictEntries3.get(0).getId();
            } else {
                logger.warn("no dict 换人 found");
                return;
            }

            List clist = match.selectNodes("./Cards");
            if (CollectionUtils.isNotEmpty(clist)) {
                Iterator cardEntrys = match.element("Cards").elementIterator("Card");
                while (cardEntrys.hasNext()) {
                    MatchAction matchAction = new MatchAction();
                    Element cardEntrysInfo = (Element) cardEntrys.next();
                    String cardType = cardEntrysInfo.attributeValue("type");
                    String playerTeam = cardEntrysInfo.elementText("PlayerTeam");
                    matchAction.setPassedTime(Double.parseDouble(cardEntrysInfo.elementText("Time")) * 60);
                    matchAction.setAllowCountries(getAllowCountries());
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
                    Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(cardEntrysInfo.element("Player").attributeValue("id"), Integer.parseInt(Constants.partnerSourceId));
                    if (null != player) {
                        matchAction.setPid(player.getId());
                    } else {
                        logger.warn("the player not found,partnerId:{}", cardEntrysInfo.element("Player").attributeValue("id"));
                        continue;
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                }

            }
            //add subtition information
            List subList = match.selectNodes("./Substitutions");
            if (CollectionUtils.isNotEmpty(subList)) {
                Iterator substitutionEntry = match.element("Substitutions").elementIterator("Substitution");
                while (substitutionEntry.hasNext()) {
                    MatchAction matchAction = new MatchAction();
                    Element subEntrysInfo = (Element) substitutionEntry.next();
                    String playerTeam = subEntrysInfo.elementText("PlayerTeam");
                    String playerOut = subEntrysInfo.element("PlayerOut").attributeValue("id");
                    String playerIn = subEntrysInfo.element("PlayerIn").attributeValue("id");
                    matchAction.setPassedTime(Double.parseDouble(subEntrysInfo.elementText("Time")) * 60);
                    matchAction.setMid(matchId);
                    matchAction.setType(subtitutionType);
                    matchAction.setAllowCountries(getAllowCountries());
                    if ("1".equals(playerTeam))
                        matchAction.setTid(teamId1);
                    else
                        matchAction.setTid((teamId2));
                    Player playerOutId = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerOut, Integer.parseInt(Constants.partnerSourceId));
                    Player playerInId = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerIn, Integer.parseInt(Constants.partnerSourceId));
                    if (null == playerOut || null == playerIn) {
                        logger.warn("the player not found,partnerId:{}", subEntrysInfo.element("Player").attributeValue("id"));
                        continue;
                    }
                    matchAction.setPid(playerOutId.getId());
                    HashMap<String, Long> map = new HashMap();
                    map.put("playerOut", playerOutId.getId());
                    map.put("playerIn", playerInId.getId());
                    matchAction.setContent(JSON.toJSONString(map));
                    SbdsInternalApis.saveMatchAction(matchAction);
                }

            }

            List glist = match.selectNodes("./Goals");
            if (CollectionUtils.isNotEmpty(glist)) {
                Iterator goalEntrys = match.element("Goals").elementIterator("Goal");
                while (goalEntrys.hasNext()) {
                    MatchAction matchAction = new MatchAction();
                    Element goalEntrysInfo = (Element) goalEntrys.next();
                    String goalFrom = goalEntrysInfo.attributeValue("from");
                    String socringTeam = goalEntrysInfo.elementText("ScoringTeam");
                    goalDetailType = Constants.goalType.get(goalFrom) == null ? 0L : Constants.goalType.get(goalFrom);
                    matchAction.setPassedTime(Double.parseDouble(goalEntrysInfo.elementText("Time")) * 60);
                    matchAction.setMid(matchId);
                    matchAction.setAllowCountries(getAllowCountries());
                    if ("1".equals(socringTeam))
                        matchAction.setTid(teamId1);
                    else
                        matchAction.setTid((teamId2));
                    matchAction.setType(goalType);
                    HashMap<String, Long> map = new HashMap();
                    map.put("goalType", goalDetailType);
                    matchAction.setContent(JSON.toJSONString(map));
                    Element goalPlayer = goalEntrysInfo.element("Player");
                    if (null != goalPlayer) {
                        Player player = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(goalPlayer.attributeValue("id"), Integer.parseInt(Constants.partnerSourceId));
                        if (null != player) {
                            matchAction.setPid(player.getId());
                        } else {
                            logger.warn("the player not found,partnerId:{}", goalEntrysInfo.element("Player").attributeValue("id"));
                            continue;
                        }
                    }
                    SbdsInternalApis.saveMatchAction(matchAction);
                }
            }
        } catch (Exception e) {
            logger.error("generateMatchAction  error", e);
        }
    }

    private FbStatistics generateStatistics(Element match, String type, Long teamId) {
        FbStatistics fbStatistics = new FbStatistics();
        String possession_percentage = "0";
        String ontarget_scoring_att = "0";
        String offtarget_scoring_att = "0";
        String fk_foul_won = "0";
        String won_corners = "0";
        String fk_foul_lost = "0";
        String off_sides = "0";
        String throw_Ins = "0";
        String goal_keeper_saves = "0";
        String goal_kicks = "0";
        int red = 0;
        int yellow = 0;
        try {
            Element statistics = match.element("Statistics");
            List clist = match.selectNodes("./Cards");
            if (null == statistics && CollectionUtils.isEmpty(clist)) {
                return null;
            }
            if (null != statistics) {
                //控球率
                Element ballPossession = statistics.element("BallPossession");
                if (ballPossession != null) {
                    possession_percentage = ballPossession.elementText("Team" + type);
                    fbStatistics.setBallPossession(Integer.parseInt(possession_percentage));
                }

                //射正球门
                Element shotsOnGoal = statistics.element("ShotsOnGoal");
                if (shotsOnGoal != null) {
                    ontarget_scoring_att = shotsOnGoal.elementText("Team" + type);
                    fbStatistics.setShotsOnGoal(Integer.parseInt(ontarget_scoring_att));
                }
                //射偏球门
                Element shotsOffGoal = statistics.element("ShotsOffGoal");
                if (shotsOffGoal != null) {
                    offtarget_scoring_att = shotsOffGoal.elementText("Team" + type);
                    fbStatistics.setShotsOffGoal(Integer.parseInt(offtarget_scoring_att));
                }

                //任意球
                Element freeKicks = statistics.element("FreeKicks");
                if (freeKicks != null) {
                    fk_foul_won = freeKicks.elementText("Team" + type);
                    fbStatistics.setFreeKicks(Integer.parseInt(fk_foul_won));
                }

                //角球
                Element cornerKicks = statistics.element("CornerKicks");
                if (cornerKicks != null) {
                    won_corners = cornerKicks.elementText("Team" + type);
                    fbStatistics.setCornerKicks(Integer.parseInt(won_corners));
                }

                //犯规
                Element fouls = statistics.element("Fouls");
                if (fouls != null) {
                    fk_foul_lost = fouls.elementText("Team" + type);
                    fbStatistics.setFouls(Integer.parseInt(fk_foul_lost));
                }

                Element offsides = statistics.element("Offsides");
                if (offsides != null) {
                    off_sides = offsides.elementText("Team" + type);
                    fbStatistics.setOffsides(Integer.parseInt(off_sides));
                }

                Element throwIns = statistics.element("ThrowIns");
                if (throwIns != null) {
                    throw_Ins = throwIns.elementText("Team" + type);
                    fbStatistics.setThrowIns(Integer.parseInt(throw_Ins));
                }

                Element goalkeeperSaves = statistics.element("GoalkeeperSaves");
                if (goalkeeperSaves != null) {
                    goal_keeper_saves = goalkeeperSaves.elementText("Team" + type);
                    fbStatistics.setGoalkeeperSaves(Integer.parseInt(goal_keeper_saves));
                }
                Element goalKicks = statistics.element("GoalKicks");
                if (goalKicks != null) {
                    goal_kicks = goalKicks.elementText("Team" + type);
                    fbStatistics.setGoalKicks(Integer.parseInt(goal_kicks));
                }
            }
            if (CollectionUtils.isNotEmpty(clist)) {
                Iterator cardEntrys = match.element("Cards").elementIterator("Card");
                while (cardEntrys.hasNext()) {
                    Element cardEntrysInfo = (Element) cardEntrys.next();
                    String cardType = cardEntrysInfo.attributeValue("type");
                    if (cardType.equals("Yellow")) {
                        if (cardEntrysInfo.elementText("PlayerTeam").equals(type)) {
                            ++yellow;
                        }
                    } else if (cardType.equals("Red") || "YellowRed".equals(cardType)) {
                        if (cardEntrysInfo.elementText("PlayerTeam").equals(type)) {
                            ++red;
                        }
                    }
                }

            }


            fbStatistics.setYellow(yellow);
            fbStatistics.setRed(red);
        } catch (Exception e) {
            logger.error("generateStatistics  error", e);
        }
        return fbStatistics;

    }

    /**
     * 更新赛事比分
     */
    private void updateMatchScore(Match match1, Match match2, Element match, String betradar, String liveSocreType, String matchStatus) {
        String currentPeriodStart = "-1";
        String time = "-1";
        String team = "-1";
        try {
            Element periodStart = match.element("CurrentPeriodStart");
            if (periodStart != null) {
                currentPeriodStart = periodStart.getTextTrim();
            }
            Element lastGoal = match.element("LastGoal");
            if (lastGoal != null) {
                time = lastGoal.element("Time").getTextTrim();
                team = lastGoal.element("Team").getTextTrim();
            }
            if ("delta".equals(liveSocreType)) {
                if (match1.getStatus().equals(MatchStatus.MATCHING)) {
                    long countTime = LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(betradar.substring(0, betradar.length() - 5)).getTime() - LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(currentPeriodStart.substring(0, currentPeriodStart.length() - 5)).getTime();
                    long currentMatchTime = countTime / 60000L;

                    Match.CurrentMoment currentMoment = new Match.CurrentMoment();
                    if ("6".equals(matchStatus)) {
                        match1.setMoment("上半场-" + currentMatchTime);
                        match1.setMultiLangMoments(getMultiLang("上半场-" + currentMatchTime));

                        currentMoment.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$") == null ? 0L : SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
                        currentMoment.setSectionName("上半场");
                    } else if ("7".equals(matchStatus)) {
                        match1.setMoment("下半场-" + currentMatchTime);
                        match1.setMultiLangMoments(getMultiLang("下半场-" + currentMatchTime));

                        currentMoment.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$") == null ? 0L : SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
                        currentMoment.setSectionName("下半场");
                    } else if ("41".equals(matchStatus)) {
                        match1.setMoment("加时上半场-" + currentMatchTime);
                        match1.setMultiLangMoments(getMultiLang("加时上半场-" + currentMatchTime));

                        currentMoment.setSection(SbdsInternalApis.getDictEntriesByName("^加时上半场$") == null ? 0L : SbdsInternalApis.getDictEntriesByName("^加时上半场$").get(0).getId());
                        currentMoment.setSectionName("加时上半场");
                    } else if ("42".equals(matchStatus)) {
                        match1.setMoment("加时下半场-" + currentMatchTime);
                        match1.setMultiLangMoments(getMultiLang("加时下半场-" + currentMatchTime));

                        currentMoment.setSection(SbdsInternalApis.getDictEntriesByName("^加时下半场$") == null ? 0L : SbdsInternalApis.getDictEntriesByName("^加时下半场$").get(0).getId());
                        currentMoment.setSectionName("加时下半场");

                    } else if ("50".equals(matchStatus)) {
                        match1.setMoment("点球-" + currentMatchTime);
                        match1.setMultiLangMoments(getMultiLang("点球-" + currentMatchTime));
                        currentMoment.setSection(SbdsInternalApis.getDictEntriesByName("点球") == null ? 0L : SbdsInternalApis.getDictEntriesByName("点球").get(0).getId());
                        currentMoment.setSectionName("点球");
                    }

                    currentMoment.setTime(currentMatchTime);
                    currentMoment.setSort(TimeSort.ASC);
                    match1.setCurrentMoment(currentMoment);

                    logger.info("当前比赛状态和时间:{}, currentMoment: {} ,partnerId:{}", match1.getMoment(), match1.getCurrentMoment(), match1.getPartnerId());
                }
                if (saveMatch(match1)) {
                    logger.info("update delta match success,matchId:" + match1.getId() + ",matchName:" + match1.getName() + ",matchPartnerId:" + match1.getPartnerId() +
                            ",matchStatus:" + match1.getStatus() + "," + ",进球时间为：" + time + ",进球球队：" + team + ",回合开始时间：" + currentPeriodStart + "文件push时间：" + betradar);
                } else {
                    logger.warn("update delta match fail,matchId:" + match1.getId());
                }
            } else if ("full".equals(liveSocreType)) {
                if (isMatchPropertyChange(match2, match1)) {
                    if (saveMatch(match1)) {
                        logger.info("update full match success,matchId:" + match1.getId() + ",matchName:" + match1.getName() + ",matchPartnerId:" + match1.getPartnerId() +
                                ",matchStatus:" + match1.getStatus() + "," + ",进球时间为：" + time + ",进球球队：" + team + ",回合开始时间：" + currentPeriodStart + "文件push时间：" + betradar);
                    } else {
                        logger.warn("update full match fail,matchId:" + match1.getId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error: ", e);
        }

    }

    private String getMatchScore(Set<Match.Competitor> competitors, GroundType type) {
        String score = "";
        if (CollectionUtils.isNotEmpty(competitors)) {
            for (Match.Competitor competitor : competitors) {
                if (competitor.getGround().equals(type)) {
                    score = competitor.getFinalResult();
                    break;
                }
            }
        }

        return score;
    }

    private boolean isNewScoreBigger(String oldScore, String newScore) {
        if (StringUtils.isEmpty(oldScore)) {
            oldScore = "0";
        }
        if (StringUtils.isEmpty(newScore)) {
            newScore = "0";
        }
        int os = Integer.parseInt(oldScore);
        int ns = Integer.parseInt(newScore);
        if (ns > os) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isStatusChange(MatchStatus oldStatus, MatchStatus newStatus) {
        boolean isChange = false;
        if (oldStatus.equals(MatchStatus.MATCH_NOT_START) && newStatus.equals(MatchStatus.MATCHING)) {
            isChange = true;
        }
        if (oldStatus.equals(MatchStatus.MATCHING) && newStatus.equals(MatchStatus.MATCH_END)) {
            isChange = true;
        }
        if (oldStatus.equals(MatchStatus.MATCH_NOT_START) && newStatus.equals(MatchStatus.MATCH_END)) {
            isChange = true;
        }
        return isChange;
    }

    private Boolean isMatchPropertyChange(Match oldMatch, Match newMatch) {
        boolean isChange = false;
        Set<Match.Competitor> oldCompetitors = oldMatch.getCompetitors();
        Set<Match.Competitor> newCompetitors = newMatch.getCompetitors();
        MatchStatus oldMatchStatus = oldMatch.getStatus();
        MatchStatus newMatchStatus = newMatch.getStatus();
        String oldHomeScore = getMatchScore(oldCompetitors, GroundType.HOME);
        String oldAwayScore = getMatchScore(oldCompetitors, GroundType.AWAY);
        String newHomeScore = getMatchScore(newCompetitors, GroundType.HOME);
        String newAwayScore = getMatchScore(newCompetitors, GroundType.AWAY);
        if (isNewScoreBigger(oldAwayScore, newAwayScore) || isNewScoreBigger(oldHomeScore, newHomeScore) || isStatusChange(oldMatchStatus, newMatchStatus)) {
            isChange = true;
        }
        return isChange;
    }


}
