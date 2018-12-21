package com.lesports.sms.data.service.stats;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.NBATeamRankStatistic;
import com.lesports.sms.data.model.StatConstats;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.Competition;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TopList;
import com.lesports.sms.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2015/9/17.
 */
@Service("NBATeamStandingsParser")
public class NBATeamStandingsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(NBATeamStandingsParser.class);

    @Override
    public Boolean parseData(String file) {
        logger.info("team standing file  parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Long stage = 0L;
            Element rootElement = document.getRootElement();
            Element nbaStandings = rootElement.element("sports-standings").element("nba-standings");
            //get tournament name and season
            String name = nbaStandings.element("league").attributeValue("alias");
            String season = nbaStandings.element("season").attributeValue("season");
            // get mathseason information
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + name, gameFTypeId);
            if (competitions == null || competitions.isEmpty()) {
                logger.warn("can not find relative competions,name is" + name);
                return result;
            }
            CompetitionSeason competitionseason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(competitions.get(0).getId(), season);
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + name + "season is" + season);
                return result;
            }
            Long csid = competitionseason.getId();
            Iterator<Element> nbaConferencesStandings = nbaStandings.elementIterator("nba-conference-standings");
            while (nbaConferencesStandings.hasNext()) {
                Element nbaConferenceStanding = nbaConferencesStandings.next();
                //判定topList is exits
                String substation = nbaConferenceStanding.attributeValue("conference");
                Long conferenceId = Constants.nbaConferenceMap.get(substation);
                TopList conferenceTopList = getTopList(competitions.get(0).getId(), csid, substation, 0L);
                List<TopList.TopListItem> conferenceTopListItems = new ArrayList<TopList.TopListItem>();
                //get division ranking data
                Iterator<Element> nbaivisionStandings = nbaConferenceStanding.elementIterator("nba-division-standings");
                while (nbaivisionStandings.hasNext()) {
                    Element nbaivisionStanding = nbaivisionStandings.next();
                    String divisionname = nbaivisionStanding.attributeValue("division");
                    Long divisionId = Constants.nbaConferenceMap.get(divisionname);
                    //get topList object
                    TopList divisionTopList = getTopList(competitions.get(0).getId(), csid, divisionname, 0L);
                    List<TopList.TopListItem> divisionTopListItems = new ArrayList<TopList.TopListItem>();
                    Iterator<Element> teamStandings = nbaivisionStanding.elementIterator("nba-team-standings");
                    while (teamStandings.hasNext()) {
                        Element teamStanding = teamStandings.next();
                        String teamPartnerId = teamStanding.element("team-code").attributeValue("global-id");
                        Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Constants.PartnerSourceStaticId);
                        if (team == null) {
                            logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
                            continue;
                        } else if (team.getConference() == null || team.getRegion() == null) {
                            team.setConference(conferenceId);
                            team.setRegion(divisionId);
                            SbdsInternalApis.saveTeam(team);
                        }
                        addCurrentTeamStandingToList(team, conferenceTopListItems, divisionTopListItems, teamStanding);
                    }
                    divisionTopList.setItems(divisionTopListItems);
                    SbdsInternalApis.saveTopList(divisionTopList);
                    logger.info("division Ranking is created sucessfully");
                }
                conferenceTopList.setItems(conferenceTopListItems);
                SbdsInternalApis.saveTopList(conferenceTopList);
                logger.info("cinference Ranking is created sucessfully");
            }
        } catch (Exception e) {
            logger.error("parser the teamranking file fail", e);
        }
        return result;
    }

    /**
     * 添加球队的技术排行榜
     *
     * @param ConferenceList
     * @param divisionList
     * @param teamStanding
     */
    private void addCurrentTeamStandingToList(Team team, List ConferenceList, List divisionList, Element teamStanding) {
        //init division item
        TopList.TopListItem divisionTopListItem = new TopList.TopListItem();
        Map<String, Object> mapDivision = getNBAStats(teamStanding, StatConstats.NBADivisionStandingStatPath);
        divisionTopListItem.setStats(mapDivision);
        divisionTopListItem.setCompetitorId(team.getId());
        divisionTopListItem.setCompetitorType(CompetitorType.TEAM);
        divisionTopListItem.setRank(CommonUtil.parseInt(mapDivision.get("rank").toString(), 0));
        divisionTopListItem.setShowOrder(CommonUtil.parseInt(mapDivision.get("rank").toString(), 0));
        divisionList.add(divisionTopListItem);
        logger.info("add  team information to division list");
        //init and and confence
        Map<String, Object> mapConference = getNBAStats(teamStanding, StatConstats.NBAConferenceStandingStatPath);
        TopList.TopListItem conferenceTopListItem = new TopList.TopListItem();
        conferenceTopListItem.setCompetitorId(team.getId());
        conferenceTopListItem.setCompetitorType(CompetitorType.TEAM);
        conferenceTopListItem.setRank(CommonUtil.parseInt(mapConference.get("rank").toString(), 0));
        conferenceTopListItem.setShowOrder(CommonUtil.parseInt(mapConference.get("rank").toString(), 0));
        conferenceTopListItem.setStats(mapConference);
        ConferenceList.add(conferenceTopListItem);
        logger.info("add  team information to conference list");
    }

    /**
     * 依据指定条件获取榜单对象
     *
     * @param cid
     * @param csid
     * @param standingname
     * @param stage
     * @return
     */
    private TopList getTopList(Long cid, Long csid, String standingname, Long stage) {
        Long standinTtype = 0L;
        Long scopeId = 0L;
        ScopeType scopeType = ScopeType.DIVISION;
        if (Constants.TeamrankingType.get(standingname) != null) {
            standinTtype = Constants.TeamrankingType.get(standingname);
            scopeId = Constants.nbaConferenceMap.get(standingname);
            if (standingname.equalsIgnoreCase("Eastern") || standingname.equalsIgnoreCase("Western")) {
                scopeType = ScopeType.CONFERENCE;
            }
        }
        TopList topList = new TopList();
        List<TopList> topLists = new ArrayList<>();
        topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, standinTtype);
        if (CollectionUtils.isNotEmpty(topLists)) {
            topList = topLists.get(0);
        } else {
            topList.setAllowCountries(getAllowCountries());
            topList.setOnlineLanguages(getOnlineLanguage());
        }
        topList.setCompetitorType(CompetitorType.TEAM);
        topList.setScopType(scopeType);
        topList.setScope(scopeId);
        topList.setDeleted(false);
        topList.setCid(cid);
        topList.setCsid(csid);
        topList.setLatest(true);
        topList.setType(standinTtype);
        topList.setStage(stage);
        return topList;
    }
}
