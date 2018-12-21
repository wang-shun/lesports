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
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("sogoService")
public class SogoGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(SogoGenerator.class);

    public String getFilePath() {
        String filePath = "scheduleandresult_sogo_copaAmerican2016.xml";
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
        displayElement.addContent(createElement("title", "2016赛季美洲杯_乐视体育"));
        displayElement.addContent(createElement("url", goalRankingUrl + sogo_logo));
        displayElement.addContent(getTabMatchElement());
        displayElement.addContent(getTabRankingElement());
        return displayElement;
    }

    private Element getTabMatchElement() {
        //添加赛程tab
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("current", "1");
        attributes.put("name", "比赛");
        Element tabElement = getElementWithAttributes("tab1", attributes);
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
        tabElement.addContent(createElement("source", "乐视体育"));
        Map linkContentMap = Maps.newHashMap();
        linkContentMap.put("linkContent", "查看更多");
        tabElement.addContent(getElementWithAttributesAndContent("more", matchesUrl + sogo_logo, linkContentMap));
        return tabElement;
    }

    private Element getTabRankingElement() {
        //添加球队积分榜
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("current", "0");
        attributes.put("name", "积分榜");
        Element tabElement = getElementWithAttributes("tab2", attributes);
        updateElement(tabElement, getTeamGroupRankElements());
        return tabElement;

    }

    //赛程列表
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
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("date", RoundNum);
        attributes.put("past", getPast(RoundNum));
        Element dayElement = getElementWithAttributes("day", attributes);
        updateElement(dayElement, getMatchContents(matches));
        return dayElement;
    }

    private String getPast(String roundName) {
        Date currentDate = CommonUtil.getDataYYMMDD(CommonUtil.getYYYYMMDDDate(new Date()));
        if (roundName.equals("第1轮") && currentDate.before(CommonUtil.getDataYYMMDD("20160608"))) {
            return "1";
        } else if (roundName.equals("第2轮") && currentDate.before(CommonUtil.getDataYYMMDD("20160612")) && currentDate.after(CommonUtil.getDataYYMMDD("20160607"))) {
            return "1";
        } else if (roundName.equals("第3轮") && currentDate.before(CommonUtil.getDataYYMMDD("20160617")) && currentDate.after(CommonUtil.getDataYYMMDD("20160611"))) {
            return "1";
        } else if (roundName.equals("1/4决赛") && currentDate.before(CommonUtil.getDataYYMMDD("20160622")) && currentDate.after(CommonUtil.getDataYYMMDD("20160616"))) {
            return "1";
        } else if (roundName.equals("半决赛") && currentDate.before(CommonUtil.getDataYYMMDD("20160626"))&& currentDate.after(CommonUtil.getDataYYMMDD("20160621"))) {
            return "1";
        }
        else if (roundName.equals("三四名决赛") && currentDate.before(CommonUtil.getDataYYMMDD("20160627"))&& currentDate.after(CommonUtil.getDataYYMMDD("20160625"))) {
            return "1";
        }else if (roundName.equals("决赛") && currentDate.after(CommonUtil.getDataYYMMDD("20160623"))&& currentDate.after(CommonUtil.getDataYYMMDD("20160626"))) {
            return "1";
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
            Element homeTeamElement = createRooElement("team1");
            homeTeamElement.addContent(createElement("name", homeTeam.getName()));
            homeTeamElement.addContent(createElement("img", getValidLogo15(homeTeam.getLogo())));
            homeTeamElement.addContent(createElement("link", teamUrl.replace("?", homeTeam.getId().toString())));
            Element awayTeamElement = createRooElement("team2");
            awayTeamElement.addContent(createElement("name", awayTeam.getName()));
            awayTeamElement.addContent(createElement("img", getValidLogo15(awayTeam.getLogo())));
            awayTeamElement.addContent(createElement("link", teamUrl.replace("?", awayTeam.getId().toString())));
            String status = getmatchStatus(currentMatch.getStatus());
            Map attributes = Maps.newHashMap();
            attributes.put("statu", status);
            Element game = createRooElement("game");
            game.addContent(homeTeamElement);
            Element statusElement = getElementWithAttributes("status", attributes);
            statusElement.addContent(createElement("date", getDate(currentMatch.getStartDate())));
            statusElement.addContent(createElement("time", getMatchTime(currentMatch.getStartTime())));
            statusElement.addContent(createElement("score", homeScore + "-" + awayScore));
            statusElement.addContent(createElement("url", matchPageUrl("",currentMatch.getId(),sogo_logo)));
            game.addContent(statusElement);
            game.addContent(awayTeamElement);

            elements.add(game);

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
        Map attributes = Maps.newHashMap();
        attributes.put("current", group.equals("A") ? "1" : "0");
        attributes.put("name", group + "组");
        Element groupRank = getElementWithAttributes("subtab", attributes);
        Map thAttributes = Maps.newHashMap();
        thAttributes.put("col1", "排名");
        thAttributes.put("col2", "球队");
        thAttributes.put("col3", "胜/平/负");
        thAttributes.put("col4", "积分");
        groupRank.addContent(getElementWithAttributes("th", thAttributes));
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isNotEmpty(topList)) {
            updateElement(groupRank, getLists(topList.get(0).getItems()));
            groupRank.addContent(createElement("source", "乐视体育"));
            Map linkContentMap = Maps.newHashMap();
            linkContentMap.put("linkContent", "查看更多");
            groupRank.addContent(getElementWithAttributesAndContent("more", teamRankingUrl, linkContentMap));
        }
        return groupRank;
    }

    private List<Element> getLists(List<TopList.TopListItem> items) {
        if (CollectionUtils.isEmpty(items)) return null;
        List<Element> tds = Lists.newArrayList();
        for (TopList.TopListItem item : items) {
            Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());
            Element list = createRooElement("list");
            list.addContent(createElement("col1", getValidLogo15(currentTeam.getLogo())));
            list.addContent(createElement("col2", currentTeam.getName()));
            String content = item.getStats().get("winMatch").toString() + "/" + item.getStats().get("flatMatch").toString() + "/" + item.getStats().get("lossMatch").toString();
            list.addContent(createElement("col3", content));
            list.addContent(createElement("col4", item.getStats().get("teamScore").toString()));
            list.addContent(createElement("col5", teamUrl.replace("?", currentTeam.getId().toString())));
            tds.add(list);
        }
        return tds;
    }

    private String getDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "月" + day + "日";
    }

    public String getMatchDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String weekDay = CommonUtil.getWeekday(date);
        return month + "-" + day + "周" + weekDay.substring(2, 3);
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
        return month + "/" + day;
    }

    public String getmatchStatus(MatchStatus status) {
        if (status.toString().equals("MATCH_END")) return "0";
        else if (status.toString().equals("MATCHING")) return "1";
        else return "2";
    }
}

