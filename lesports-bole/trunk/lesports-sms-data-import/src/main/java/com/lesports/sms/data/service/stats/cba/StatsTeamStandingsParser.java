package com.lesports.sms.data.service.stats.cba;

import com.google.common.collect.ImmutableMap;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.StatConstats;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.Competition;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TopList;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2015/9/17.
 */
@Service("StatsTeamStandingsParser")
public class StatsTeamStandingsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(StatsTeamStandingsParser.class);
    private Map<String, String> elementIdentifyMap = ImmutableMap.of("NBA", "nba", "CBACHN", "bk");

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
            String cName = file.contains("NBA") ? "NBA" : "CBACHN";
            String elementIdentify = elementIdentifyMap.get(cName);
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Long stage = 0L;
            Element statsStandingsRoot = document.getRootElement().element("sports-standings");
            Element statsStandings = statsStandingsRoot.element(elementIdentify + "-standings");
            String season = statsStandings.element("season").attributeValue("season");//get tournament name and season
            Long cid = Constants.statsNameMap.get(cName);
            CompetitionSeason competitionseason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(cid, season);
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + cName + "season is" + season);
                return result;
            }
            Long csid = competitionseason.getId();
            Iterator<Element> parentStandings = null;
            Boolean isContainParentElement = true;//判定当前的技术统计节点是含有多层榜单
            parentStandings = statsStandings.elementIterator("nba-conference-standings");
            if (parentStandings == null || !parentStandings.hasNext()) isContainParentElement = false;
            while (!isContainParentElement || (parentStandings != null && parentStandings.hasNext())) {
                Iterator<Element> nbaSonStandings = null;
                Long conferenceId = 0L;
                TopList parentTopList = null;
                List<TopList.TopListItem> parentTopListItems = null;
                if (isContainParentElement) {
                    Element curentElement = parentStandings.next();
                    nbaSonStandings = curentElement.elementIterator(("nba-division-standings"));
                    String substation = curentElement.attributeValue("conference");
                    conferenceId = Constants.nbaConferenceMap.get(substation);
                    parentTopList = getTopList(cid, csid, substation, 0L);
                    parentTopListItems = new ArrayList<TopList.TopListItem>();
                } else {
                    isContainParentElement = true;
                    nbaSonStandings = statsStandingsRoot.elementIterator(elementIdentify + "-standings");
                }
                while (nbaSonStandings.hasNext()) {
                    Element nbaivisionStanding = nbaSonStandings.next();
                    String sonName = nbaivisionStanding.attributeValue("division");
                    Long divisionId = 0L;
                    if (sonName != null) divisionId = Constants.nbaConferenceMap.get(sonName);
                    TopList divisionTopList = getTopList(cid, csid, sonName, 0L);//get TopList By Name
                    List<TopList.TopListItem> divisionTopListItems = new ArrayList<TopList.TopListItem>();
                    Iterator<Element> teamStandings = nbaivisionStanding.elementIterator(elementIdentify + "-team-standings");
                    while (teamStandings.hasNext()) {
                        Element teamStanding = teamStandings.next();
                        String teamPartnerId = teamStanding.element("team-code").attributeValue("global-id");
                        Team team = SbdsInternalApis.getTeamByStatsId(teamPartnerId);
                        if (team == null) {
                            logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
                            continue;
                        } else if (team.getConference() == null || team.getRegion() == null) {
                            team.setConference(conferenceId);
                            team.setRegion(divisionId);
                            SbdsInternalApis.saveTeam(team);
                        }
                        addCurrentTeamStandingToList(elementIdentify, team, parentTopListItems, divisionTopListItems, teamStanding);
                    }
                    divisionTopList.setItems(divisionTopListItems);
                    SbdsInternalApis.saveTopList(divisionTopList);
                    logger.info("sub-dividion-all Ranking is created sucessfully,content:{}", divisionTopList);
                }
                if (parentStandings != null && CollectionUtils.isNotEmpty(parentTopListItems)) {
                    parentTopList.setItems(parentTopListItems);
                    SbdsInternalApis.saveTopList(parentTopList);
                    logger.info("parent-conference Ranking is created sucessfully,content:{}", parentTopList);
                }
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
    private void addCurrentTeamStandingToList(String elementIdentify, Team team, List ConferenceList, List divisionList, Element teamStanding) {
        if (divisionList != null) {  //init the sub-standing  item
            TopList.TopListItem divisionTopListItem = new TopList.TopListItem();
            Map<String, Object> mapDivision = null;
            if (elementIdentify.contains("bk")) {
                mapDivision = getNBAStats(teamStanding, StatConstats.CBADivisionStandingStatPath);
                Integer wins = Integer.valueOf(mapDivision.get("winMatch").toString());
                Integer losses = Integer.valueOf(mapDivision.get("lossMatch").toString());
                mapDivision.put("matchNumber", wins + losses);
                mapDivision.put("teamScore", wins * 2 + losses);
            } else {
                mapDivision = getNBAStats(teamStanding, StatConstats.NBADivisionStandingStatPath);
            }
            divisionTopListItem.setStats(mapDivision);
            divisionTopListItem.setCompetitorId(team.getId());
            divisionTopListItem.setCompetitorType(CompetitorType.TEAM);
            divisionTopListItem.setRank(CommonUtil.parseInt(mapDivision.get("rank").toString(), 0));
            divisionTopListItem.setShowOrder(CommonUtil.parseInt(mapDivision.get("rank").toString(), 0));
            divisionList.add(divisionTopListItem);
            logger.info("add  team information to the sub-team-standing (division or cid) list");
        }
        if (ConferenceList != null) { //init  the parent standing item
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
        logger.info("team standing item add to the parent-team-standing list");
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
        Long standinType = 100162000L;//积分榜默认球队积分榜
        Long scopeId = 0L;
        ScopeType scopeType = null;
        if (Constants.TeamrankingType.get(standingname) != null) {
            standinType = Constants.TeamrankingType.get(standingname);
            scopeId = Constants.nbaConferenceMap.get(standingname);
            if (standingname != null && (standingname.equalsIgnoreCase("Eastern") || standingname.equalsIgnoreCase("Western"))) {
                scopeType = ScopeType.CONFERENCE;
            } else
                scopeType = ScopeType.DIVISION;
        }
        TopList topList = new TopList();
        List<TopList> topLists = new ArrayList<>();
        topLists = SbdsInternalApis.getTopListsByCsidAndType(csid, standinType);
        if (CollectionUtils.isNotEmpty(topLists)) {
            return topLists.get(0);
        }
        topList.setAllowCountries(getAllowCountries());
        topList.setOnlineLanguages(getOnlineLanguage());
        topList.setCompetitorType(CompetitorType.TEAM);
        topList.setScopType(scopeType);
        topList.setScope(scopeId);
        topList.setDeleted(false);
        topList.setCid(cid);
        topList.setCsid(csid);
        topList.setLatest(true);
        topList.setType(standinType);
        topList.setStage(stage);
        return topList;

    }
}
