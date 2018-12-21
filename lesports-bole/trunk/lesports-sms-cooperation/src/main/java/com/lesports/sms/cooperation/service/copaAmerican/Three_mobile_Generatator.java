package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.CommonUtil;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TopList;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("three_mobileService")
public class Three_mobile_Generatator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Three_mobile_Generatator.class);

    public String getFilePath() {
        String filePath = "scheduleandresult_onebox_mobile_copaAmerican2016.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        root.addContent(getItemElement());
        return new Document(root);
    }

    private Element getItemElement() {
        Element item = createRooElement("item");//一级目录
        item.addContent(createElement("key", "美洲杯"));
        item.addContent(getDisplayElement());
        return item;
    }

    private Element getDisplayElement() {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("title", "2016美洲杯赛程_乐视体育"));
        displayElement.addContent(createElement("url", resourceMobileUrl + one_box_logo));
        displayElement.addContent(createElement("currenttab", "赛程"));
        displayElement.addContent(getTabMatchElement());
        displayElement.addContent(getTabMatchGroupElement());
        displayElement.addContent(getTabRankingElement());
        displayElement.addContent(createElement("source", "乐视体育"));
        displayElement.addContent(createElement("more", "进入美洲杯专题"));
        displayElement.addContent(createElement("more_url",resourceMobileUrl+one_box_logo ));
        return displayElement;
    }

    private Element getTabMatchElement() {
        //添加赛程tab
        Element tabElement = createRooElement("tab_match");
        tabElement.addContent(createElement("title", "赛程"));
        tabElement.addContent(createElement("currentmatchs", getCurrentTab()));
        tabElement.addContent(getRoundElements("第1轮"));
        tabElement.addContent(getRoundElements("第2轮"));
        tabElement.addContent(getRoundElements("第3轮"));
        Element element2 = getRoundElements("1/4决赛");
        if (element2 != null) tabElement.addContent(element2);
        Element element3 = getRoundElements("半决赛");
        if (element3 != null) tabElement.addContent(element3);
        Element element5 = getRoundElements("三四名决赛");
        if (element5 != null) tabElement.addContent(element5);
        Element element4 = getRoundElements("决赛");
        if (element4 != null) tabElement.addContent(element4);
        return tabElement;
    }

    private Element getTabMatchGroupElement() {
        //添加赛程tab
        Element tabElement = createRooElement("tab_group_match");
        tabElement.addContent(createElement("title", "小组赛程"));
        tabElement.addContent(getGroupElements("A"));
        tabElement.addContent(getGroupElements("B"));
        tabElement.addContent(getGroupElements("C"));
        tabElement.addContent(getGroupElements("D"));
        return tabElement;
    }

    private Element getTabRankingElement() {
        //添加球队积分榜
        Element tabElement = createRooElement("tab_group_score");
        tabElement.addContent(createElement("title", "小组积分榜"));
        tabElement.addContent(createElement("li", "球队"));
        tabElement.addContent(createElement("li", "胜"));
        tabElement.addContent(createElement("li", "平"));
        tabElement.addContent(createElement("li", "负"));
        tabElement.addContent(createElement("li", "净胜球"));
        tabElement.addContent(createElement("li", "积分"));
        updateElement(tabElement, getTeamGroupRankElements());
        return tabElement;

    }

    //xiaozu列表
    private Element getGroupElements(String groupName) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        if (idMaps.get(groupName) != null) {
            q.addCriteria(new InternalCriteria("group", "eq", idMaps.get(groupName)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new Sort(Lists.newArrayList(new Sort.Order(Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        Element matchs = createRooElement("tab_group");
        matchs.addContent(createElement("group", groupName));
        updateElement(matchs, getMatchContents(matches));
        return matchs;
    }

    //jieduan列表
    private Element getRoundElements(String RoundNum) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        Long currentStage = 0L;
        if (idRoundMaps.get(RoundNum) != null) {
            currentStage = idRoundMaps.get(RoundNum);
            q.addCriteria(new InternalCriteria("round", "eq", idRoundMaps.get(RoundNum)));
        } else {
            currentStage = idStageMaps.get(RoundNum);
            q.addCriteria(new InternalCriteria("stage", "eq", idStageMaps.get(RoundNum)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new Sort(Lists.newArrayList(new Sort.Order(Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        Element matchs = createRooElement("matchs");
        matchs.addContent(createElement("desc",getRoundDate(RoundNum)));
        updateElement(matchs, getMatchContents(matches));
        return matchs;
    }

    private String getCurrentTab() {
        if (new Date().before(CommonUtil.getDataYYMMDD("20160608"))) {
            return getRoundDate("第1轮");
        } else if (new Date().before(CommonUtil.getDataYYMMDD("20160612")) && new Date().after(CommonUtil.getDataYYMMDD("20160607"))) {
            return getRoundDate("第2轮");
        } else if (new Date().before(CommonUtil.getDataYYMMDD("20160617")) && new Date().after(CommonUtil.getDataYYMMDD("20160612"))) {
            return getRoundDate("第3轮");
        } else if (new Date().before(CommonUtil.getDataYYMMDD("20160622")) && new Date().after(CommonUtil.getDataYYMMDD("20160616"))) {
            return getRoundDate("1/4决赛");
        } else if (new Date().before(CommonUtil.getDataYYMMDD("20160626")) && new Date().after(CommonUtil.getDataYYMMDD("20160621"))) {
            return getRoundDate("半决赛");
        }
        else if (new Date().before(CommonUtil.getDataYYMMDD("20160627")) && new Date().after(CommonUtil.getDataYYMMDD("20160625"))) {
            return getRoundDate("三四名决赛");
        } else if (new Date().after(CommonUtil.getDataYYMMDD("20160623"))) {
            return getRoundDate("决赛");
        }
        return "0";
    }

    private String getRoundDate(String roundName) {
        if (roundName.equals("第1轮")) {
            return roundName + "(6/04-6/07)";
        } else if (roundName.equals("第2轮")) {
            return roundName + "(6/08-6/11)";
        } else if (roundName.equals("第3轮")) {
            return roundName + "(6/12-6/15)";
        } else if (roundName.equals("1/4决赛")) {
            return roundName + "(6/17-6/19)";
        } else if (roundName.equals("半决赛")) {
            return roundName + "(6/22-6/23)";
        } else if (roundName.equals("三四名决赛")) {
            return roundName + "(6/26)";
        }
        else if (roundName.equals("决赛")) {
            return roundName + "(6/27)";
        }
        return "0";
    }

    List<Element> getMatchContents(List<Match> matches) {
        List<Element> elements = Lists.newArrayList();
        for (Match currentMatch : matches) {
            Team homeTeam = null;
            Team awayTeam = null;
            String homeScore = "0";
            String awayScore = "0";
            Set<Match.Competitor> teams = currentMatch.getCompetitors();
            for (Match.Competitor currentCompetitor : teams) {
                Team team = SbdsInternalApis.getTeamById(currentCompetitor.getCompetitorId());
                if (currentCompetitor.getGround().equals(GroundType.HOME)) {
                    homeTeam = team;
                    homeScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            Element teamVs = createRooElement("team_vs");
            teamVs.addContent(createElement("team1", homeTeam.getName()));
            teamVs.addContent(createElement("team1_icon", getValidLogo15(homeTeam.getLogo())));
            teamVs.addContent(createElement("team2", awayTeam.getName()));
            teamVs.addContent(createElement("team2_icon", getValidLogo15(awayTeam.getLogo())));
            teamVs.addContent(createElement("score", getScore(currentMatch.getStatus(),homeScore,awayScore)));//weikaisi VS ,zhibo 0-2
            teamVs.addContent(createElement("date", getmatchDate(currentMatch.getStartDate())));//19:45
            teamVs.addContent(createElement("time", getMatchTime(currentMatch.getStartTime())));//19:45
            teamVs.addContent(createElement("state", getmatchStatus(currentMatch.getStatus())));//09-26
            teamVs.addContent(createElement("url", matchPageUrl("",currentMatch.getId(),one_box_logo)));//09-26
            elements.add(teamVs);
        }
        return elements;
    }

    //  team rankings列表
    private List<Element> getTeamGroupRankElements() {
        List<Element> groupRankings = Lists.newArrayList();
        groupRankings.add(getGroudRank("A"));
        groupRankings.add(getGroudRank("B"));
        groupRankings.add(getGroudRank("C"));
        groupRankings.add(getGroudRank("D"));
        return groupRankings;
    }

    private Element getGroudRank(String group) {
        Element tab_group = createRooElement("tab_group");
        tab_group.addContent(createElement("group", group));
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isNotEmpty(topList)) {
            updateElement(tab_group, getLists(topList.get(0).getItems()));
        }
        return tab_group;
    }

    private List<Element> getLists(List<TopList.TopListItem> items) {
        if (CollectionUtils.isEmpty(items)) return null;
        List<Element> tds = Lists.newArrayList();
        for (TopList.TopListItem item : items) {
            Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());
            Element list = createRooElement("list");
            list.addContent(createElement("rank", item.getRank().toString()));
            list.addContent(createElement("team_info", currentTeam.getName()));
            list.addContent(createElement("team_info", item.getStats().get("winMatch").toString()));
            list.addContent(createElement("team_info", item.getStats().get("flatMatch").toString()));
            list.addContent(createElement("team_info", item.getStats().get("lossMatch").toString()));
            Integer goalDif = Integer.valueOf(item.getStats().get("goal").toString()) - Integer.valueOf(item.getStats().get("fumble").toString());
            list.addContent(createElement("team_info", goalDif.toString()));
            list.addContent(createElement("team_info", item.getStats().get("teamScore").toString()));
            tds.add(list);
        }
        return tds;
    }


    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }

    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "-" + day;
    }

}

