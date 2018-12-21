package com.lesports.sms.data.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.api.common.CountryCode;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.MatchSystem;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Completion;
import java.io.File;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by lufei1 on 2014/11/25.
 */
@Service("matchFbParser")
public class MatchFbParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(MatchFbParser.class);

    public static Map<String, Integer> matchStageMap = Maps.newHashMap();

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
            //查找比赛类型
            String sportId = rootElement.element("Sport").attributeValue("id");
            String sportsType = Constants.sportsTypeMap.get(sportId);
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();
            //查找赛事Id
            Element tournament = rootElement.element("Sport").element("Category").element("Tournament");
            String tournamentName = tournament.attributeValue("name");
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
            Long csid = competitionSeason.getId();
            Iterator matches = tournament.element("Matches").elementIterator("Match");

            while (matches.hasNext()) {
                Element match = (Element) matches.next();
                Element matchResult = match.element("Result");
                String partnerId = match.attribute("id").getValue();
                Match match1 = SbdsInternalApis.getMatchByPartnerIdAndType(partnerId, Integer.parseInt(Constants.partnerSourceId));

                //如果有锁则不更新
                if (match1 != null && match1.getLock() != null && match1.getLock() == true) {
                    logger.warn("the match may be locked,partnerId:{}", partnerId);
                    continue;
                }
                if (match1 != null) {
                    Boolean isUpdated = false;
                    //纠正已导入赛程比赛时间错误
                    Date matchTime = LeDateUtils.parseYYYY_MM_DDTHH_MM_SSZ(match.attribute("dateOfMatch").getValue());
                    if (!match1.getStartTime().equals(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime))) {
                        match1.setStartTime(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime));
                        match1.setStartDate(match1.getStartTime().substring(0, 8));
                        isUpdated = true;
                    }
                    Byte statusByte = getMatchStatus(matchResult);
                    if (statusByte != null && !match1.getStatus().equals(MatchStatus.findByValue(statusByte))) {
                        match1.setStatus(MatchStatus.findByValue(statusByte));
                        for (Match.Competitor currentCompetitor : match1.getCompetitors()) {
                            if (currentCompetitor.getGround().equals(GroundType.HOME)) {
                                String score = getMatchScore("1", matchResult);
                                if (score != null) currentCompetitor.setFinalResult(score);
                            } else {
                                String score = getMatchScore("2", matchResult);
                                if (score != null) currentCompetitor.setFinalResult(score);
                            }

                        }
                        isUpdated = true;
                    }
                    if (isUpdated) {
                        saveMatch(match1);
                        logger.info("update match,sucessfully,partnerId:{},time:{},status:{}, partner_id: ", partnerId, match1.getStartTime(), match1.getStatus().toString());
                        return true;
                    }
                    //更新延迟比赛partner_id
                    if (matchResult != null) {
                        String isCanceled = matchResult.attributeValue("canceled");
                        String isPostponed = matchResult.attributeValue("postponed");
                        String newMatchId = matchResult.attributeValue("postponedNewMatchid");
                        Byte notStartStateId = new Byte("0");
                        if (isCanceled == null || isPostponed == null) continue;
                        if (isCanceled.equals("true") || isPostponed.equals("true")) {
                            logger.warn("the match may be canceled or postpond,partnerId:{}", partnerId);
                            if (newMatchId != null || match1.getStatus().equals(MatchStatus.findByValue(notStartStateId))) {
                                SbdsInternalApis.deleteMatch(match1.getId());
                                logger.info(match1.getId() + "比赛已被删除");
                            }
                            continue;
                        }
                    }
                } else {
                    Element teams = match.element("Teams");
                    Team homeTeam = getTeamByType("1", teams, gameFType, competitions.get(0).getId(), csid);
                    Team awayTeam = getTeamByType("2", teams, gameFType, competitions.get(0).getId(), csid);
                    if (homeTeam == null || awayTeam == null) continue;
                    Long homeTeamId = homeTeam.getId();
                    Long awayTeamId = awayTeam.getId();
                    String homeTeamName = homeTeam.getName();
                    String awayTeamName = awayTeam.getName();
                    Date matchTime = LeDateUtils.parseYYYY_MM_DDTHH_MM_SSZ(match.attribute("dateOfMatch").getValue());
                    Long matchId = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime), homeTeamId, awayTeamId);
                    Match match2 = null;
                    if (matchId <= 0) {
                        match2 = new Match();
                    } else {
                        match2 = SbdsInternalApis.getMatchById(matchId);
                    }
                    //轮次保存于比赛阶段
                    String round = "";
                    DictEntry dicts = new DictEntry();
                    DictEntry dictEntry1 = new DictEntry();
                    if (match.element("Roundinfo").attribute("round") != null) {
                        round = match.element("Roundinfo").attribute("round").getValue();
                        dicts = getRoundDict(sportId, round);
                    }
                    if (match.element("Roundinfo").attribute("roundname") != null) {
                        round = match.element("Roundinfo").attribute("roundname").getValue();
                        String roundName = Constants.groupsMap.get(round);
                        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^阶段");
                        if (CollectionUtils.isEmpty(dictEntries)) {
                            logger.warn("not dict round,dictName:{},file_name:{}", "阶段", file);
                            continue;
                        }
                        dictEntry1 = SbdsInternalApis.getDictEntryByNameAndParentId(roundName, dictEntries.get(0).getId());
                    }
                    Long roundNum = dicts.getId();
                    String roundName = dicts.getName();
                    Long stageNum = dictEntry1.getId();
                    String stageName = dictEntry1.getName();

                    Match.Competitor homeCompetitor = new Match.Competitor();
                    Match.Competitor awayCompetitor = new Match.Competitor();
                    //比赛结果
                    if (matchResult != null)
                        if (matchResult != null)
                            if ((matchResult.attributeValue("canceled") != null && matchResult.attributeValue("canceled").equals("true")) || (matchResult.attributeValue("postponed") != null && matchResult.attributeValue("postponed").equals("true"))) {
                                logger.warn("the match may be canceled,partnerId:{},fileName:{}", partnerId, file);
                                if (match2.getId() != null) SbdsInternalApis.deleteMatch(match2.getId());
                                continue;
                            }
                    String homeScore = getMatchScore("1", matchResult) == null ? "0" : getMatchScore("1", matchResult);
                    String awayScore = getMatchScore("2", matchResult) == null ? "0" : getMatchScore("2", matchResult);
                    Byte matchState = getMatchStatus(matchResult) == null ? new Byte("0") : getMatchStatus(matchResult);
                    homeCompetitor.setCompetitorId(homeTeamId);
                    homeCompetitor.setGround(GroundType.HOME);
                    homeCompetitor.setType(CompetitorType.TEAM);
                    homeCompetitor.setFinalResult(homeScore);
                    awayCompetitor.setCompetitorId(awayTeamId);
                    awayCompetitor.setGround(GroundType.AWAY);
                    awayCompetitor.setType(CompetitorType.TEAM);
                    awayCompetitor.setFinalResult(awayScore);

                    Set<Match.Competitor> competitors = new HashSet<Match.Competitor>();
                    competitors.add(homeCompetitor);
                    competitors.add(awayCompetitor);
                    match2.setCompetitors(competitors);
                    Set<Long> tagIds = competitions.get(0).getTagIds();
                    match2.getTagIds().addAll(tagIds);
                    String match_en_name = season + tournamentName + " " + getTeamNameByType("1", teams) + " VS " + getTeamNameByType("2", teams);
                    String match_name = "";
                    if (roundName != null) {
                        match_name = season + competitions.get(0).getName() + " " + roundName + " " + homeTeamName + " VS " + awayTeamName;
                    } else {
                        match_name = season + competitions.get(0).getName() + " " + stageName + " " + homeTeamName + " VS " + awayTeamName;
                        match2.setStage(stageNum);
                    }
                    match2.setCid(competitions.get(0).getId());
                    match2.setCsid(competitionSeason.getId());
                    match2.setStartTime(LeDateUtils.formatYYYYMMDDHHMMSS(matchTime));
                    match2.setStartDate(LeDateUtils.formatYYYYMMDD(matchTime));
                    //美国大联盟stage特殊处理
                    if ("242".equals(gameS)) {
                        roundNum = 69L;
                        match_name = season + competitions.get(0).getName() + "  " + homeTeamName + " VS " + awayTeamName;
                    }
                    //男篮亚锦赛特殊处理
                    if ("943".equals(gameS)) {
                        match_name = season + competitions.get(0).getName() + " " + stageName + " " + homeTeamName + " VS " + awayTeamName;
                        match2.setStage(stageNum);
                    }
                    if ("649".equals(gameS) || "109".equals(gameS)) {
                        match_name = season + competitions.get(0).getName() + " " + awayTeamName + " VS " + homeTeamName;
                    }
                    match2.setRound(roundNum);
                    match2.setStatus(MatchStatus.findByValue(matchState));
                    match2.setPartnerId(partnerId);
                    match2.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    match2.setDeleted(false);
                    match2.setName(match_name);
                    match2.setMultiLangNames(getMultiLang(getMultiLang(match_name), match_en_name));
                    match2.setGameFType(gameFType);
                    match2.setOnlineLanguages(getOnlineLanguage());
                    match2.setAllowCountries(competitions.get(0).getAllowCountries());
                    match2.setCreateCountry(CountryCode.CN);
                    match2.setCreateLanguages(getOnlineLanguage());
                    if (match1 == null) {
                        List<DictEntry> dictEntry = SbdsInternalApis.getDictEntriesByName("分组");
                        //如果有杯赛，则添加groups
                        if (competitions.get(0).getSystem() == MatchSystem.CUP && (!"943".equals(gameS))) {
                            int position = file.lastIndexOf(".xml");
                            String g = file.substring(position - 1, position);
                            String gname = g + "组";
                            DictEntry dc = SbdsInternalApis.getDictEntryByNameAndParentId(gname, dictEntry.get(0).getId());
                            if (dc != null) {
                                match2.setGroup(dc.getId());
                            }
                        }
                        match2.setCreateAt(match2.getCreateAt() == null ? LeDateUtils.formatYYYYMMDDHHMMSS(new Date()) : match2.getCreateAt());
                        saveMatch(match2);
                        logger.info("insert matchFb success,matchName:{},matchPartnerId:{},fileName:{}", match2.getName(), match2.getPartnerId(), file);
                    }
                }
            }
            result = true;
        } catch (DocumentException e) {
            logger.error("parse match" + file + " xml error", e);
        }
        return result;
    }

    private DictEntry getRoundDict(String type, String roundNumber) {
        DictEntry dictEntry = null;
        if (type.equals(Constants.AMERICAFOOTBALLID)) {
            List<DictEntry> dicConfig = SbdsInternalApis.getDictEntriesByName("^周");
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + roundNumber + "周", dicConfig.get(0).getId());
        } else {
            List<DictEntry> dicConfig = SbdsInternalApis.getDictEntriesByName("^轮次$");
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + roundNumber + "轮", dicConfig.get(0).getId());
        }
        return dictEntry;
    }

    private String getTeamNameByType(String type, Element matchTeam) {
        if (matchTeam == null) return null;
        Iterator teams = matchTeam.elementIterator("Team");
        Long teamId = 0L;
        String name = "";
        while (teams.hasNext()) {
            Element team = (Element) teams.next();
            if (team.attribute("type").getValue().equals(type)) {
                return team.attributeValue("name");
            }
        }
        return name;
    }


    private Team getTeamByType(String type, Element matchTeam, Long gameFType, Long cid, Long csid) {
        try {
            if (matchTeam == null) return null;
            Iterator teams = matchTeam.elementIterator("Team");
            Long teamId = 0L;
            String partnerId = "";
            String enName = "";
            while (teams.hasNext()) {
                Element team = (Element) teams.next();
                if (team.attribute("type").getValue().equals(type)) {
                    partnerId = team.attribute("id").getValue();
                    enName = team.attributeValue("name");
                    Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerId, Integer.parseInt(Constants.partnerSourceId));
                    if (team1 != null) {
                        return team1;
                    } else {
                        logger.warn("cannot find related team,PartnerId:{},fileName:{}", partnerId);
                        team1 = new Team();
                        team1.setName(enName);
                        team1.setMultiLangNames(getMultiLang(enName));
                        Set<Long> cids = team1.getCids();
                        cids.add(cid);
                        team1.setCids(cids);
                        team1.setOnlineLanguages(getOnlineLanguage());
                        team1.setAllowCountries(getAllowCountries());
                        Element translation = team.element("Translation");
                        if (null != translation) {
                            Iterator translationIterator = translation.elementIterator("TranslatedValue");
                            while (translationIterator.hasNext()) {
                                Element translatedValue = (Element) translationIterator.next();
                                if ("zh".equals(translatedValue.attributeValue("lang")))
                                    team1.setName(translatedValue.attributeValue("value"));
                                team1.setMultiLangNames(getMultiLang(getMultiLang(translatedValue.attributeValue("value")), enName));
                            }
                        }
                        team1.setPartnerId(partnerId);
                        team1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                        team1.setGameFType(gameFType);
                        teamId = SbdsInternalApis.saveTeam(team1);
                        if (teamId <= 0) return null;
                        team1.setId(teamId);
                        logger.info("insert into team success,partnerId:{},fileName:{}", partnerId);
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
            }
        } catch (Exception e) {
            logger.error("get Team info fail", e);
        }
        return null;
    }

    private Byte getMatchStatus(Element matchResult) {
        if (matchResult != null) {
            List list = matchResult.selectNodes("./Score");
            if (CollectionUtils.isNotEmpty(list))
                return new Byte("2");
        }
        return null;
    }

    private String getMatchScore(String type, Element matchResult) {
        if (matchResult != null) {
            List list = matchResult.selectNodes("./Score");
            if (CollectionUtils.isNotEmpty(list)) {
                String homeScore = "0";
                String awayScore = "0";
                Iterator<Element> scoreEleIt = matchResult.elementIterator("Score");
                boolean isap = false;
                boolean isot = false;
                while (scoreEleIt.hasNext()) {
                    Element scoreEle = scoreEleIt.next();
                    if (scoreEle.attributeValue("type").equals("FT")) {
                        String score = scoreEle.attributeValue("value");
                        String[] scoreArr = score.split(":");
                        homeScore = scoreArr[0];
                        awayScore = scoreArr[1];
                    }
                    //加时赛结束
                    if (scoreEle.attributeValue("type").equals("OT")) {
                        String score = scoreEle.attributeValue("value");
                        String[] scoreArr = score.split(":");
                        homeScore = scoreArr[0];
                        awayScore = scoreArr[1];
                    }
                    //点球大战结束
                    if (scoreEle.attributeValue("type").equals("AP")) {
                        String score = scoreEle.attributeValue("value");
                        String[] scoreArr = score.split(":");
                        homeScore = scoreArr[0];
                        awayScore = scoreArr[1];
                    }
                }
                if (type.equals("1")) return homeScore;
                else return awayScore;
            }
        }
        return null;
    }
}





