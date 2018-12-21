package com.lesports.sms.data.service;

import com.lesports.api.common.CountryCode;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
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
 * Created by yangyu on 2015/5/4.
 */
@Service("cupFbParser")
public class CupFbParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(CupFbParser.class);

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
            String sportsId = rootElement.element("Sport").attributeValue("id");

            //查找比赛类型
            String sportsType = Constants.sportsTypeMap.get(rootElement.element("Sport").attributeValue("id"));
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();
            //查找赛事Id
            Element tournament = rootElement.element("Sport").element("Category").element("Tournament");
            String gameS = tournament.attribute("uniqueTournamentId").getValue();
            String season = tournament.element("Season").attribute("start").getValue().substring(0, 4);
            String name = Constants.nameMap.get(gameS);
            //赛事查询

            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType(name, gameFType);
            if (CollectionUtils.isEmpty(competitions)) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{}", gameS, name);
                return result;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionSeason == null) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{},season:{}", gameS, name, season);
                return result;
            }
            List cupTreeList = rootElement.selectNodes("//CupTree");
            if (CollectionUtils.isNotEmpty(cupTreeList)) {
                Iterator cupTrees_iter = cupTreeList.iterator();
                while (cupTrees_iter.hasNext()) {
                    Element cupTree = (Element) cupTrees_iter.next();
                    Iterator cupRoundIterator = cupTree.elementIterator("CupRound");
                    while (cupRoundIterator.hasNext()) {
                        Element cupRound = (Element) cupRoundIterator.next();
                        if (cupRound == null) {
                            logger.warn("cupRound is not exist,file_name:{}", file);
                            continue;
                        }
                        String round = cupRound.attributeValue("description");
                        String gname = Constants.groupsMap.get(round);
                        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^阶段");
                        if (CollectionUtils.isEmpty(dictEntries)) {
                            logger.warn("not dict round,dictName:{},file_name:{}", "阶段", file);
                            continue;
                        }
                        DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(gname, dictEntries.get(0).getId());
                        Long group = dictEntry.getId();
                        String gameRound = dictEntry.getName();
                        List cupBlockList = cupRound.selectNodes("./CupBlock");
                        if (CollectionUtils.isEmpty(cupBlockList)) {
                            logger.warn("cupBlock is not exist,file_name:{}", file);
                            continue;
                        }
                        Iterator cupBlock_iter = cupBlockList.iterator();
                        String homeCity = "";
                        while (cupBlock_iter.hasNext()) {
                            Element cupBlock = (Element) cupBlock_iter.next();
                            List blockPartcipantList = cupBlock.selectNodes("./BlockParticipant");
                            if (CollectionUtils.isNotEmpty(blockPartcipantList)) {
                                Iterator blockPartcipant_iter = blockPartcipantList.iterator();
                                while (blockPartcipant_iter.hasNext()) {
                                    Element blockPartcipant = (Element) blockPartcipant_iter.next();
                                    List countryList = blockPartcipant.selectNodes("./Team/Country");
                                    if (CollectionUtils.isNotEmpty(countryList)) {
                                        Element coutry = (Element) countryList.iterator().next();
                                        List translatedValueList = coutry.selectNodes("./Translation/TranslatedValue");
                                        if (CollectionUtils.isNotEmpty(translatedValueList)) {
                                            Element city = (Element) translatedValueList.get(0);
                                            homeCity = city.attributeValue("value");
                                            break;
                                        }
                                    }
                                }
                            }
                            Element matches = cupBlock.element("Matches");
                            if (matches == null) {
                                logger.warn("matches is not exist,file_name:{}", file);
                                continue;
                            }
                            List matchList = matches.selectNodes("./Match");
                            if (CollectionUtils.isNotEmpty(matchList)) {
                                Iterator match_iter = matchList.iterator();
                                while (match_iter.hasNext()) {
                                    Element match = (Element) match_iter.next();
                                    String partnerId = match.attribute("id").getValue();
                                    Date matchTime = LeDateUtils.parseYYYY_MM_DDTHH_MM_SSZ(match.attribute("dateOfMatch").getValue());
                                    String startTime = LeDateUtils.formatYYYYMMDDHHMMSS(matchTime);

                                    Match match2 = SbdsInternalApis.getMatchByPartnerIdAndType(partnerId, Integer.parseInt(Constants.partnerSourceId));

                                    //如果有锁则不更新
                                    if (match2 != null && match2.getLock() != null && match2.getLock() == true) {
                                        logger.warn("the match may be locked,partnerId:{}", partnerId);
                                        continue;
                                    }
                                    if (match2 != null) {
                                        //纠正已导入赛程比赛时间错误
                                        Date matchTime2 = LeDateUtils.parseYYYY_MM_DDTHH_MM_SSZ(match.attribute("dateOfMatch").getValue());
                                        if (!match2.getStartTime().equals(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime2))) {

                                            match2.setStartTime(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime));
                                            match2.setStartDate(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime).substring(0, 8));
                                            boolean isSuccess = false;
                                            int tryCount = 0;
                                            while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
                                                try {
                                                    isSuccess = SbdsInternalApis.saveMatch(match2) > 0;
                                                } catch (Exception e) {
                                                    logger.error("fail to update match. id : {}. sleep and try again.", match2.getId(), e);
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (InterruptedException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            }

                                            logger.info("update matchtime, partner_id: " + partnerId);
                                        }
                                        //更新延迟比赛partner_id


                                    } else {

                                        Match match1 = null;
                                        Element teams = match.element("Teams");
                                        if (teams == null) {
                                            logger.warn("teams is not exist,file_name:{}", file);
                                            continue;
                                        }
                                        Team homeTeam = getTeamByType("1", teams, gameFType, competitions.get(0).getId(), competitionSeason.getId());
                                        Team awayTeam = getTeamByType("2", teams, gameFType, competitions.get(0).getId(), competitionSeason.getId());
                                        if (homeTeam == null || awayTeam == null) continue;
                                        Long homeTeamId = homeTeam.getId();
                                        Long awayTeamId = awayTeam.getId();
                                        String homeTeamName = homeTeam.getName();
                                        String awayTeamName = awayTeam.getName();
                                        String date = LeDateUtils.formatYYYYMMDDHHMMSS(matchTime).toString();

                                        Long matchId = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(date, homeTeamId, awayTeamId);
                                        if (matchId <= 0) {
                                            match1 = new Match();
                                        } else {
                                            match1 = SbdsInternalApis.getMatchById(matchId);
                                        }
                                        String othomeScore = "0";
                                        String otawayScore = "0";
                                        String fthomeScore = "0";
                                        String ftawayScore = "0";
                                        String athomeScore = "0";
                                        String atawayScore = "0";
                                        String homeScore = "0";
                                        String awayScore = "0";
                                        Byte matchState = new Byte("0");
                                        Match.Competitor homeCompetitor = new Match.Competitor();
                                        Match.Competitor awayCompetitor = new Match.Competitor();
                                        // 比赛结果
                                        Element matchResult = match.element("Result");
                                        if (matchResult != null) {
                                            if (matchResult.attributeValue("canceled").equals("true") || matchResult.attributeValue("postponed").equals("true")) {
                                                logger.warn("the match may be canceled,partnerId:{},fileName:{}", partnerId, file);
                                                if (match1.getId() != null)
                                                    SbdsInternalApis.deleteMatch(match1.getId());
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
                                            homeCompetitor.setFinalResult(homeScore);
                                            awayCompetitor.setFinalResult(awayScore);
                                            matchState = new Byte("2");
                                        }
                                        homeCompetitor.setCompetitorId(homeTeamId);
                                        homeCompetitor.setGround(GroundType.HOME);
                                        homeCompetitor.setType(CompetitorType.TEAM);
                                        awayCompetitor.setCompetitorId(awayTeamId);
                                        awayCompetitor.setGround(GroundType.AWAY);
                                        awayCompetitor.setType(CompetitorType.TEAM);

                                        Set<Match.Competitor> competitors = new HashSet<Match.Competitor>();
                                        competitors.add(homeCompetitor);
                                        competitors.add(awayCompetitor);
                                        match1.setCompetitors(competitors);
                                        Set<Long> tagIds = competitions.get(0).getTagIds();
                                        match1.getTagIds().addAll(tagIds);
                                        match1.setCid(competitions.get(0).getId());
                                        match1.setCsid(competitionSeason.getId());
                                        match1.setStartTime(startTime);
                                        match1.setStartDate(startTime.substring(0, 8));
                                        match1.setStage(group);
                                        match1.setStatus(MatchStatus.findByValue(matchState));
                                        match1.setPartnerId(partnerId);
                                        match1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                                        match1.setDeleted(false);
                                        Integer nextSeason = Integer.parseInt(season) + 1;
                                        String matchName = season + competitions.get(0).getName() + " " + gameRound + " " + homeTeamName + " VS " + awayTeamName;
                                        match1.setName(matchName);
                                        match1.setMultiLangNames(getMultiLang(matchName));
                                        match1.setGameFType(gameFType);
                                        match1.setOnlineLanguages(getOnlineLanguage());
                                        match1.setAllowCountries(getAllowCountries());
                                        match1.setCreateCountry(CountryCode.CN);
                                        match1.setCreateLanguages(getOnlineLanguage());
                                        match1.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
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
                                        logger.info("insert matchFb success,matchName:{},matchPartnerId:{},fileName:{}", match1.getName(), match1.getPartnerId(), file);
                                        logger.info("赛程partnerId:" + match1.getPartnerId() + "添加成功");

                                    }
                                }
                            }
                        }
                    }
                }
            }
            result = true;
        } catch (DocumentException e) {
            logger.error("parse cup" + file + " xml error", e);
        }
        return result;
    }

    private Team getTeamByType(String type, Element matchTeams, Long gameFType, Long cid, Long csid) {
        try {
            if (matchTeams == null) return null;
            Iterator teams = matchTeams.elementIterator("Team");
            Long teamId = 0L;
            String partnerId = "";
            while (teams.hasNext()) {
                Element team = (Element) teams.next();
                if (team.attribute("type").getValue().equals(type)) {
                    partnerId = team.attribute("id").getValue();
                    Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerId, Integer.parseInt(Constants.partnerSourceId));
                    if (team1 != null) return team1;
                    logger.warn("cannot find related team,PartnerId:{},fileName:{}", partnerId);
                    String enName = team.attributeValue("name");
                    team1 = new Team();
                    team1.setName(enName);
                    team1.setMultiLangNames(getMultiLang(enName));
                    Set<Long> cids = team1.getCids();
                    cids.add(cid);
                    team1.setCids(cids);
                    Element translation = team.element("Translation");
                    if (null != translation) {
                        Iterator translationIterator = translation.elementIterator("TranslatedValue");
                        while (translationIterator.hasNext()) {
                            Element translatedValue = (Element) translationIterator.next();
                            if ("zh".equals(translatedValue.attributeValue("lang")))
                                team1.setName(translatedValue.attributeValue("value"));
                            team1.setMultiLangNames(getMultiLang(translatedValue.attributeValue("value")));
                        }
                    }
                    team1.setPartnerId(partnerId);
                    team1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    team1.setGameFType(gameFType);
                    team1.setOnlineLanguages(getOnlineLanguage());
                    team1.setAllowCountries(getAllowCountries());
                    SbdsInternalApis.saveTeam(team1);
                    logger.info("insert into team success,partnerId:{},fileName:{}", partnerId);
                    teamId = team1.getId();
                    List<TeamSeason> seasonList = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
                    if (seasonList == null || seasonList.isEmpty()) {
                        TeamSeason teamSeason = new TeamSeason();
                        teamSeason.setTid(teamId);
                        teamSeason.setCsid(csid);
                        teamSeason.setAllowCountries(getAllowCountries());
                        SbdsInternalApis.saveTeamSeason(teamSeason);
                        logger.info("insert into teamSeason success,partnerId:{}", partnerId);
                    }
                    return team1;
                }
            }
        } catch (Exception e) {
            logger.error("get Team info fail", e);
        }
        return null;
    }

}
