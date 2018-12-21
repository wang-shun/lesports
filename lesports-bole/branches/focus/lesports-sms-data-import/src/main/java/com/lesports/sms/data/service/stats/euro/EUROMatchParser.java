package com.lesports.sms.data.service.stats.euro;

import com.google.common.collect.ImmutableMap;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.StatsConstants;
import com.lesports.sms.model.*;
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
@Service("EUROMatchParser")
public class EUROMatchParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(EUROMatchParser.class);
    private Map<String, MatchStatus> stateMap = ImmutableMap.of("1", MatchStatus.MATCH_NOT_START, "2", MatchStatus.MATCHING, "4", MatchStatus.MATCH_END);


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
            // get sport-standings
            Element sportsStandings = rootElmement.element("sports-schedule");
            String name = sportsStandings.element("league").attributeValue("alias");
            String season = sportsStandings.element("season").attributeValue("year");

            Competition competition = SbdsInternalApis.getCompetitionById(StatsConstants.COMPETITIONS_ID);
            if(competition == null){
                logger.warn("can not find relative competions,name is" + name);
                return result;
            } else {
                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("cid").is(competition.getId()));
                internalQuery.addCriteria(InternalCriteria.where("season").regex(season));
                List<CompetitionSeason> competitionseasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);
                if (competitionseasons == null || competitionseasons.isEmpty()) {
                    logger.warn("can not find the right time competitionseasons,cid is " + competition.getId() + " season is " + season);
                    return result;
                } else {
                    Iterator gameSchedules = sportsStandings.element("soccer-ifb-schedule").elementIterator("game-schedule");
                    while (gameSchedules.hasNext()) {
                        Element game = (Element) gameSchedules.next();
                        boolean isSuccess = false;
                        int tryCount = 0;
                        while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
                            try {
                                isSuccess = upsertMatch(game, season, competition.getName(), competitionseasons.get(0));
                            } catch (Exception e) {
                                logger.error("fail to update match. id : {}. sleep and try again.", e);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("team info error: ", e);
        }
        return result;
    }

    //upsert match infomation
    private boolean upsertMatch(Element game, String season, String name, CompetitionSeason competitionSeason) {
        //get source parnter game id
        String partnerId = game.element("gamecode").attributeValue("global-code");
        String partnerCode = game.element("gamecode").attributeValue("code");
        String gameType = Constants.euroTypeMap.get(game.element("game-type").attributeValue("id"));
        String statusId = game.element("status").attributeValue("status-id");
        MatchStatus status = stateMap.get(statusId);
        //get matchTime
        String month = game.element("date").attributeValue("month");
        String date = game.element("date").attributeValue("date");
        String hour = game.element("time").attributeValue("hour");
        String year = game.element("date").attributeValue("year");
        String minute = game.element("time").attributeValue("minute");
        String utc = game.element("time").attributeValue("utc-hour");
        String startDate = CommonUtil.getDataYYMMDD(year, month, date, hour, minute, utc);
        String startTime = CommonUtil.getDataYYYYMMDDHHMMSS(year, month, date, hour, minute, utc);

        Team homeTeam = SbdsInternalApis.getTeamByStatsId(game.element("home-team").element("team-info").attributeValue("global-id"));
        Team visitTeam = SbdsInternalApis.getTeamByStatsId(game.element("visiting-team").element("team-info").attributeValue("global-id"));
        if (homeTeam == null || visitTeam == null) {
            logger.warn("team is not exist"  );
            if(homeTeam==null){
                logger.warn("homeId: " + game.element("home-team").element("team-info").attributeValue("global-id"));
            }
            if(visitTeam==null){
                logger.warn("awayId: " + game.element("visiting-team").element("team-info").attributeValue("global-id"));
            }

            return false;
        }
        //define match object
        Match match = SbdsInternalApis.getMatchByStatsId(partnerId);
        Long matchId = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(startDate, homeTeam.getId(), visitTeam.getId());
        if (null != match) {
            if (match.getStartTime().equals(startTime)) {
                return true;
            }
            match.setStartTime(startTime);
            match.setStartDate(startDate);
        } else {
            if (matchId > 0) {
                match = SbdsInternalApis.getMatchById(matchId);
            } else {
                match = new Match();
            }

            if (status == null) {
                if (match == null || match.getId() == null) {
                    logger.warn("the current match is postponed or cancelled,create match schedule false");
                    return false;
                }
                SbdsInternalApis.deleteMatch(match.getId());
                logger.warn("the current match is postponed or cancelled,delete the match" + match.getId());
                return false;
            }
            match.setStatsId(partnerId);
            match.setStatsCode(partnerCode);
            match.setCid(competitionSeason.getCid());
            match.setCsid(competitionSeason.getId());
            //set match Name;
            match.setCity(game.element("stadium").attributeValue("city"));
            match.setStatus(status);

            //get stage id
            if (gameType == null) {
                logger.warn("game type is not right" + partnerId);
            }
            List<DictEntry> dicts = SbdsInternalApis.getDictEntriesByName(gameType);
            if (dicts == null || dicts.isEmpty()) {
                DictEntry tmp = new DictEntry();
                tmp.setName(gameType);
                SbdsInternalApis.saveDict(tmp);
            }
            DictEntry stage = SbdsInternalApis.getDictEntriesByName(gameType).get(0);
            match.setStage(stage.getId());
            //小组赛 get group id
            if ("7".equals(game.element("game-type").attributeValue("id"))) {
                List<DictEntry> dictEntry = SbdsInternalApis.getDictEntriesByName("分组");
                String group = Constants.euroGroupMap.get(game.element("home-team").element("group").attributeValue("id"));
                DictEntry dc = SbdsInternalApis.getDictEntryByNameAndParentId(group, dictEntry.get(0).getId());
                if (dc != null) {
                    match.setGroup(dc.getId());
                }
            }
            //不是小组赛和1/8决赛 计算排列顺序

            if(StatsConstants.showOrderMap.get(match.getStatsId())!=null){
                match.setTheRoadOrder(StatsConstants.showOrderMap.get(match.getStatsId()));
            }


            //大项 足球
            List<DictEntry> gameTypeDicts = SbdsInternalApis.getDictEntriesByName("^" +"足球$");
            if (gameTypeDicts == null || gameTypeDicts.isEmpty()) {
                DictEntry tmp = new DictEntry();
                tmp.setName("足球");
                SbdsInternalApis.saveDict(tmp);
            }
            match.setGameFType(SbdsInternalApis.getDictEntriesByName("^" +"足球$").get(0).getId());

            match.setStartTime(startTime);
            match.setStartDate(startDate);

            String homeTeamName = homeTeam.getName();
            String visitTeamName = visitTeam.getName();
            if (match.getCompetitors() == null || match.getCompetitors().isEmpty()) {
                match.setCompetitors(createCompetitor(game.element("home-team").element("team-info").attributeValue("global-id"), game.element("visiting-team").element("team-info").attributeValue("global-id")));
            }

            match.setName(name + " " + stage.getName() + " " + homeTeamName + " VS " + visitTeamName);
            match.setAllowCountries(getAllowCountries());
            match.setMultiLangNames(getMultiLang(match.getName()));
            match.setOnlineLanguages(getOnlineLanguage());
        }

        return SbdsInternalApis.saveMatch(match) > 0L;
    }

    private Set<Match.Competitor> createCompetitor(String homeTeamId, String visitiongTeamId) {
        Team visitingTeam = SbdsInternalApis.getTeamByStatsId(visitiongTeamId);
        Team homeTeam = SbdsInternalApis.getTeamByStatsId(homeTeamId);
        Set<Match.Competitor> competitors = new HashSet<Match.Competitor>();
        //bulid home team information
        Match.Competitor competitorHome = new Match.Competitor();
        competitorHome.setCompetitorId(homeTeam.getId());
        competitorHome.setType(CompetitorType.TEAM);
        competitorHome.setGround(GroundType.HOME);
        //bulid visiting team information
        Match.Competitor competitorAwy = new Match.Competitor();
        competitorAwy.setCompetitorId(visitingTeam.getId());
        competitorAwy.setType(CompetitorType.TEAM);
        competitorAwy.setGround(GroundType.AWAY);
        competitors.add(competitorHome);
        competitors.add(competitorAwy);
        return competitors;
    }


}
