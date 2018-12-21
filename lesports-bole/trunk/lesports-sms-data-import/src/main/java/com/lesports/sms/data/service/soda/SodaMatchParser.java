package com.lesports.sms.data.service.soda;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
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
import java.util.*;

/**
 * Created by ruiyuansheng on 2016/2/22.
 */
@Service("sodaMatchParser")
public class SodaMatchParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(SodaMatchParser.class);

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
            Element rootElmement = document.getRootElement();

            // 赛事基本信息，赛事名称、当前赛季、当前轮次
            Element competitionInfo = rootElmement.element("Competition");
            String competitionId = competitionInfo.attributeValue("id");
            String competitionName = competitionInfo.attributeValue("name");

            Competition competition = SbdsInternalApis.getCompetitionById(Constants.sodaSMSCompetittionMap.get(competitionId));
            if (null == competition) {
                logger.warn("can not find relative event,name:{}", competitionName);
                return result;
            }
            String currentSeason = competitionInfo.attributeValue("season");
            long cid = competition.getId();

            String currentRound = competitionInfo.elementText("currentRound");

            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("cid").is(cid));
            internalQuery.addCriteria(InternalCriteria.where("season").regex(currentSeason));
            List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);

            if (CollectionUtils.isEmpty(competitionSeasons)) {
                logger.warn("can not find relative event,name:{},season:{}", competitionName, currentSeason);
                return result;
            }
            CompetitionSeason competitionSeason = competitionSeasons.get(0);
            long csid = competitionSeason.getId();
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");
            if (CollectionUtils.isEmpty(dictEntryList)) {
                logger.warn("can not find gameFType,name:足球");
                return result;
            }
            Long gameFType = dictEntryList.get(0).getId();

            //赛程信息
            Iterator<Element> matches = competitionInfo.elementIterator("MatchData");
            while (matches.hasNext()) {
                Element match = matches.next();
                String matchId = match.attributeValue("id");
                //根据第三方id查询赛程是否已经存在

                Match sodaMatch = SbdsInternalApis.getMatchBySodaId(matchId);

                String round = match.attributeValue("round");//轮次
                String time = match.element("Date").getText();//比赛时间

                String homeId = "";
                String homeName = "";
                String awayName = "";
                String awayId = "";
                String homeScore = "";
                String homeHalfscore = "";
                String awayScore = "";
                String awayHalfscore = "";


                Iterator<Element> teams = match.elementIterator("Team");
                while (teams.hasNext()) {
                    Element team = teams.next();
                    if(team.attributeValue("side").equals("Home")){
                        homeId = team.attributeValue("id");
                        homeName = team.attributeValue("name");
                        homeScore = team.attributeValue("score");
                        homeHalfscore = team.attributeValue("halfScore");
                    }
                    if(team.attributeValue("side").equals("Away")){
                        awayId = team.attributeValue("id");
                        awayName = team.attributeValue("name");
                        awayScore = team.attributeValue("score");
                        awayHalfscore = team.attributeValue("halfScore");
                    }
                }



                String matchTime = LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.parseYMDHMS(time));
                String matchDate = LeDateUtils.formatYYYYMMDD(LeDateUtils.parseYMDHMS(time));
                //已经存在则更新赛程
                if (null != sodaMatch) {
//                    //如果有锁则不更新
//                    if (sodaMatch.getLock() != null && sodaMatch.getLock() == true) {
//                        logger.warn("the match may be locked,sodaId:{}", matchId);
//                        continue;
//                    }
//                    //纠正已导入赛程比赛时间错误
                    if (!sodaMatch.getStartTime().equals(matchTime)) {

                        sodaMatch.setStartTime(matchTime);
                        sodaMatch.setStartDate(matchDate);
                        saveMatch(sodaMatch);
                        logger.info("update matchtime, sodaId: " + matchId);
                    }

                } else { //不存在则直接插入新赛程

                    Team homeTeam = getTeam(homeId, gameFType, cid, csid);
                    Team awayTeam = getTeam(awayId, gameFType, cid, csid);

                    if (homeTeam == null || awayTeam == null) continue;
                    Long homeTeamId = homeTeam.getId();
                    Long awayTeamId = awayTeam.getId();
                    internalQuery = new InternalQuery();
                    List<Long> ids = Lists.newArrayList();
                    ids.add(homeTeamId);
                    ids.add(awayTeamId);
                    internalQuery.addCriteria(new InternalCriteria("competitors.competitor_id", "all", ids));
                    internalQuery.addCriteria(new InternalCriteria("start_date", "eq", matchDate));
                    internalQuery.addCriteria(new InternalCriteria("deleted", "eq", false));
                    List<Match> matches1 = SbdsInternalApis.getMatchsByQuery(internalQuery);
                    Long mid = 0L;
                    if (CollectionUtils.isNotEmpty(matches1)) {
                        mid = matches1.get(0).getId();
                    }
//                    List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);
//                    Long mid = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(matchTime, homeTeamId, awayTeamId);
                    Match match2 = null;
                    if (mid <= 0) {
//                        match2 = new Match();
//                        //轮次保存于比赛阶段
//                        DictEntry dicts = getRoundDictEntry(round);
//                        homeScore = "0";
//                        awayScore = "0";
//                        Byte matchState = new Byte("0");
//                        Match.Competitor homeCompetitor = new Match.Competitor();
//                        Match.Competitor awayCompetitor = new Match.Competitor();
//                        //比赛结果
//                        if (StringUtils.isNotEmpty(homeScore) && StringUtils.isNotEmpty(awayScore)) {
//                            homeCompetitor.setFinalResult(homeScore);
//                            awayCompetitor.setFinalResult(awayScore);
//                            homeCompetitor.setSectionResults(generateSectionResult(homeHalfscore, homeScore, "home"));
//                            awayCompetitor.setSectionResults(generateSectionResult(awayHalfscore, awayScore, "away"));
//                            matchState = new Byte("2");
//                        }
//
//                        homeCompetitor.setCompetitorId(homeTeamId);
//                        homeCompetitor.setGround(GroundType.HOME);
//                        homeCompetitor.setType(CompetitorType.TEAM);
//                        awayCompetitor.setCompetitorId(awayTeamId);
//                        awayCompetitor.setGround(GroundType.AWAY);
//                        awayCompetitor.setType(CompetitorType.TEAM);
//
//                        Set<Match.Competitor> competitors = new HashSet<Match.Competitor>();
//                        competitors.add(homeCompetitor);
//                        competitors.add(awayCompetitor);
//                        match2.setCompetitors(competitors);
//                        Set<Long> tagIds = competition.getTagIds();
//                        match2.getTagIds().addAll(tagIds);
//                        String match_name = currentSeason + competitionName + " 第" + round + "轮 " + homeName + " VS " + awayName;
//                        match2.setCid(cid);
//                        match2.setCsid(csid);
//                        match2.setStartTime(matchTime);
//                        match2.setRound(dicts.getId());
//                        match2.setStatus(MatchStatus.findByValue(matchState));
//                        match2.setSodaId(matchId);
//                        match2.setDeleted(false);
//                        match2.setName(match_name);
//                        match2.setMultiLangNames(getMultiLang(match_name));
//                        match2.setAllowCountries(getAllowCountries());
//                        match2.setOnlineLanguages(getOnlineLanguage());
//                        match2.setGameFType(gameFType);
//                        match2.setCreateAt(match2.getCreateAt() == null ? LeDateUtils.formatYYYYMMDDHHMMSS(new Date()) : match2.getCreateAt());
                    } else {
                        match2 = SbdsInternalApis.getMatchById(mid);
                        match2.setSodaId(matchId);
                    }

                    if(match2 == null){
                        logger.info("match2 is not exist , homeTeamId: {},awayTeamId: {},startDate: {} ",homeTeamId,awayTeamId,matchDate);
                    }
                    else{
                        saveMatch(match2);
                        logger.info("insert matchFb success,matchName:{},sodaId:{},fileName:{}", match2.getName(), matchId, file);
                        logger.info("赛程sodaId:" + match2.getSodaId() + "添加成功");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("insert into match  error: ", e);
        }
        return result;

    }


    private List<Match.SectionResult> generateSectionResult(String halfScore, String score, String type) {

        List<Match.SectionResult> sectionResults = new ArrayList<>();

        Integer ltScore = Integer.parseInt(score) - Integer.parseInt(halfScore);


        Match.SectionResult halfsectionResult = new Match.SectionResult();
        halfsectionResult.setResult(halfScore);
        halfsectionResult.setOrder(1);
        halfsectionResult.setSection(SbdsInternalApis.getDictEntriesByName("^上半场$").get(0).getId());
        sectionResults.add(halfsectionResult);

        Match.SectionResult secondHalfResult = new Match.SectionResult();
        secondHalfResult.setResult(ltScore + "");
        secondHalfResult.setOrder(2);
        secondHalfResult.setSection(SbdsInternalApis.getDictEntriesByName("^下半场$").get(0).getId());
        sectionResults.add(secondHalfResult);

        return sectionResults;
    }

    private Team getTeam(String teamId, Long gameFType, Long cid, Long csid) {
        try {

            Team team = SbdsInternalApis.getTeamBySodaId(teamId);
            if (team != null) return team;
            logger.warn("cannot find related team,sodaId:{}", teamId);

        } catch (Exception e) {
            logger.error("get Team info fail", e);
        }
        return null;
    }

    private DictEntry getRoundDictEntry(String currentRound) {
        DictEntry dictEntry = new DictEntry();
        List<DictEntry> dicConfig = SbdsInternalApis.getDictEntriesByName("^轮次$");
        dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + currentRound + "轮", dicConfig.get(0).getId());
        return dictEntry;
    }


}
