package com.lesports.sms.data.service.stats.cba;

import com.google.common.collect.ImmutableMap;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
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
 * Created by qiaohongxin on 2016/10/20.
 */

@Service("statsMatchParser")
public class StatsMatchParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(StatsMatchParser.class);
    private Map<String, MatchStatus> stateMap = ImmutableMap.of("Pre-Game", MatchStatus.MATCH_NOT_START, "In-Progress", MatchStatus.MATCHING, "Final", MatchStatus.MATCH_END);
    private Map<String, String> elementIdentifyMap = ImmutableMap.of("NBA", "nba", "CBACHN", "bk");

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
            // get sport-schedule
            Element sportSchedule = rootElmement.element("sports-schedule");
            String name = sportSchedule.element("league").attributeValue("alias");
            Long cid = Constants.statsNameMap.get(name);
            String elementIdentify = elementIdentifyMap.get(name);
            String season = sportSchedule.element("season").attributeValue("season");
            Competition competition = SbdsInternalApis.getCompetitionById(cid);
            CompetitionSeason competitionseason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(cid, season);
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + name + "season is" + season);
                return result;
            }
            Long gameFTypeId = competition.getGameFType();
            Iterator gameSchedules = sportSchedule.element(elementIdentify + "-schedule").elementIterator("game-schedule");
            while (gameSchedules.hasNext()) {
                Element game = (Element) gameSchedules.next();
                boolean isSuccess = false;
                int tryCount = 0;
                while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
                    try {
                        isSuccess = upsertMatch(competition, competitionseason.getId(), gameFTypeId, game, season);
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

        } catch (Exception e) {
            logger.error("team info error: ", e);
        }
        return result;
    }

    //upsert match infomation
    private boolean upsertMatch(Competition competition, Long csid, long gameFType, Element game, String season) {
        //get source parnter game id
        String partnerId = game.element("gamecode").attributeValue("global-id");
        String partnerCode = game.element("gamecode").attributeValue("code");
        String gameType = Constants.nbaGameType.get(game.element("gametype").attributeValue("id"));
        String statusId = game.element("status").attributeValue("status");
        MatchStatus status = stateMap.get(statusId);
        //get matchTime
        String month = game.element("date").attributeValue("month");
        String date = game.element("date").attributeValue("date");
        String hour = game.element("time").attributeValue("hour");
        String year = game.element("date").attributeValue("year");
        String minute = game.element("time").attributeValue("minute");
        String utcHour = game.element("time").attributeValue("utc-hour");
        String startTime = CommonUtil.getDataYYYYMMDDHHMMSS(year, month, date, hour, minute, utcHour);
        String startDate = startTime.substring(0, 8);
        Team homeTeam = SbdsInternalApis.getTeamByStatsId(game.element("home-team").element("team-code").attributeValue("global-id"));
        Team visitTeam = SbdsInternalApis.getTeamByStatsId(game.element("visiting-team").element("team-code").attributeValue("global-id"));
        if (homeTeam == null || visitTeam == null) {
            logger.warn("team is not exist");
            return false;
        }
        //define match object
        Match match = SbdsInternalApis.getMatchByPartnerIdAndType(partnerId, Constants.PartnerSourceStaticId);
        Long matchId = SbdsInternalApis.getmatchIdByStartTimeAndCompetitorIds(startTime, homeTeam.getId(), visitTeam.getId());
        if (null != match) {
            if (isInValidUpdate(match.getStatus(), status))
                return false;
        } else if (matchId > 0) {
            match = SbdsInternalApis.getMatchById(matchId);
        } else {
            match = new Match();
            match.setAllowCountries(competition.getAllowCountries());
            match.setOnlineLanguages(getOnlineLanguage());
            match.setGameFType(gameFType);
            match.setCid(competition.getId());
            match.setCsid(csid);
            //set match Name;
            match.setCity(game.element("stadium").attributeValue("city"));
            match.setMultiLangCitys(getMultiLang(game.element("stadium").attributeValue("city")));
            match.setStatus(status);
            //get stage id
            match.setStage(SbdsInternalApis.getDictEntriesByName(gameType).get(0).getId());
            String homeTeamName = homeTeam.getName();
            String visitTeamName = visitTeam.getName();
            if (match.getCompetitors() == null || match.getCompetitors().isEmpty()) {
                match.setCompetitors(createCompetitor(game.element("home-team").element("team-code").attributeValue("global-id"), game.element("visiting-team").element("team-code").attributeValue("global-id"), Constants.PartnerSourceStaticId));
            }
            Integer nextseason = Integer.parseInt(season) + 1;
            String matchName = season + "/" + nextseason.toString() + " " + competition.getName() + gameType + " " + homeTeamName + " VS " + visitTeamName;
            if (competition.getName().equals("NBA"))
                matchName = season + "/" + nextseason.toString() + " " + competition.getName() + gameType + " " + visitTeamName + " VS " + homeTeamName;
            match.setName(matchName);
            match.setMultiLangNames(getMultiLang(matchName));
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
        match.setPartnerType(Constants.PartnerSourceStaticId);
        match.setPartnerId(partnerId);
        match.setPartnerCode(partnerCode);
        match.setStartTime(startTime);
        match.setStartDate(startDate);
        return SbdsInternalApis.saveMatch(match) > 0;
    }

    private Set<Match.Competitor> createCompetitor(String homeTeamId, String visitiongTeamId, Integer partnerSourceId) {
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

    private boolean isInValidUpdate(MatchStatus oldStatus, MatchStatus newStatus) {
        boolean isInValidChange = false;
        if (oldStatus.equals(MatchStatus.MATCHING) && newStatus.equals(MatchStatus.MATCH_NOT_START)) {
            isInValidChange = true;
        }
        if (oldStatus.equals(MatchStatus.MATCH_END) && newStatus.equals(MatchStatus.MATCHING)) {
            isInValidChange = true;
        }
        if (oldStatus.equals(MatchStatus.MATCH_END) && newStatus.equals(MatchStatus.MATCH_NOT_START)) {
            isInValidChange = true;
        }
        return isInValidChange;
    }

}

