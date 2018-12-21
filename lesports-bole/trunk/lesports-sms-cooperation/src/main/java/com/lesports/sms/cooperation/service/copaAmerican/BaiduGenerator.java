package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.CommonUtil;
import com.lesports.sms.model.*;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduService")
public class BaiduGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduGenerator.class);

    public String getFilePath() {
        String filePath = "D:\\copaAmerican_baidu.xml";
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
        displayElement.addContent(createElement("url", resourceUrl));
        displayElement.addContent(getTabMatchElement());
        displayElement.addContent(getTabNewsElement());
        displayElement.addContent(getTabRankingElement());
        displayElement.addContent(getTabPlayerRankElement());
        return displayElement;
    }

    private Element getTabMatchElement() {
        //添加赛程tab
        Element tabElement = createRooElement("tab");
        tabElement.addContent(createElement("type", "match"));
        tabElement.addContent(createElement("tab_name", "赛程"));
        tabElement.addContent(createElement("selected", "true"));
        updateElement(tabElement, getVilistElements());
        return tabElement;
    }

    //添加新闻tab
    private Element getTabNewsElement() {
        Element tabElement2 = createRooElement("tab");
        tabElement2.addContent(createElement("type", "news"));
        tabElement2.addContent(createElement("tab_name", "新闻"));
        updateElement(tabElement2, getNewsListElements());
        return tabElement2;
    }

    //添加球员排行榜
    private Element getTabPlayerRankElement() {
        Element tabElement3 = createRooElement("tab");
        tabElement3.addContent(createElement("type", "bestplayer"));
        tabElement3.addContent(createElement("tab_name", "最佳球员"));
        Element tabInnner = createRooElement("tab_inner");
        tabInnner.addContent(createElement("inner_name", "今日最佳"));
        updateElement(tabInnner, getPlayerRanksListElements());
        tabElement3.addContent(tabInnner);
        return tabElement3;
    }

    private Element getTabRankingElement() {
        //添加球队积分榜
        Element tabElement4 = createRooElement("tab");
        tabElement4.addContent(createElement("type", "rank"));
        tabElement4.addContent(createElement("tab_name", "球队排名"));
        Element tabInnner2 = createRooElement("tab_inner");
        tabInnner2.addContent(createElement("inner_type", "teamrank"));
        tabInnner2.addContent(createElement("inner_name", "球队排名"));
        updateElement(tabInnner2, getTeamGroupRankElements());
        tabElement4.addContent(tabInnner2);
        return tabElement4;
    }


    //赛程列表
    private List<Element> getVilistElements() {
        List<Element> Vilists = Lists.newArrayList();
        Date validDate = CommonUtil.getDataYYMMDD(copa_start_date);
        Date endDate = CommonUtil.getDataYYMMDD(copa_end_date);
        while (validDate.before(endDate)) {
            validDate = CommonUtil.getNextDate(validDate);
            String validDate1 = CommonUtil.getYYYYMMDDDate(validDate);
            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("csid", "eq", csid));
            q.addCriteria(new InternalCriteria("start_date", "eq", validDate1));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
            if (CollectionUtils.isEmpty(matches)) continue;
            Element vsListElement = createRooElement("vsList");
            vsListElement.addContent(createElement("state", "1"));
            vsListElement.addContent(createElement("date", getMatchDate(validDate1)));
            updateElement(vsListElement, getVsboxElements(matches));
            Vilists.add(vsListElement);
        }
        return Vilists;
    }

    private List<Element> getVsboxElements(List<Match> matchList) {
        List<Element> Vsboxes = Lists.newArrayList();
        Element vsboxtElement = createRooElement("vsboxt");
        vsboxtElement.addContent(createElement("img_type", "w"));
        List<Element> Vslines = Lists.newArrayList();
        for (Match currentMatch : matchList) {
            Team homeTeam = null;
            Team awayTeam = null;
            String homeScore = "0";
            String awayScore = "0";
            Set<Match.Competitor> teams = currentMatch.getCompetitors();
            for (Match.Competitor currentCompetitor : teams) {
                Team team = SbdsInternalApis.getTeamById(currentCompetitor.getCompetitorId());
                if (currentCompetitor.getGround().equals(GroundType.HOME)) {
                    homeTeam = team;
                    homeScore = currentCompetitor.getFinalResult() == null ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = currentCompetitor.getFinalResult() == null ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            Map<String, Object> attributes = Maps.newHashMap();
            attributes.put("ranks1", homeTeam.getName());
            attributes.put("icon1", homeTeam.getLogo());
            attributes.put("record1", "");
            attributes.put("ranks1", awayTeam.getName());
            attributes.put("icon2", awayTeam.getLogo());
            attributes.put("record2", "");
            attributes.put("score", homeScore + "-" + awayScore);
            attributes.put("vstime", getMatchTime(currentMatch.getStartTime()));
            attributes.put("state", currentMatch.getStartDate().equals(CommonUtil.getYYYYMMDDDate(new Date())) ? "1" : "0");
            attributes.put("statetext", currentMatch.getMoment() == null ? "" : currentMatch.getMoment());
            attributes.put("url", matchPageUrl("",currentMatch.getId(),baidu_logo));
            vsboxtElement.addContent(getElementWithAttributes("vsline", attributes));
        }
        updateElement(vsboxtElement, Vslines);
        Vsboxes.add(vsboxtElement);

        return Vsboxes;
    }

    //新闻列表
    private List<Element> getNewsListElements() {
        List<Element> newsLit = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("tag_ids", "eq", copaNewTagId));
        List<News> news = SopsInternalApis.getNewsByQuery(q);
        if (CollectionUtils.isEmpty(news)) return null;
        for (News currentnew : news) {
            Element newsListElement = createRooElement("newslist");
            newsListElement.addContent(createElement("title", currentnew.getName()));
            newsListElement.addContent(createElement("img", MapUtils.isEmpty(currentnew.getImageUrl()) ? "" : currentnew.getImageUrl().get("120*90")));
            Video currentVideo = SopsInternalApis.getVideoById(currentnew.getVid());
            newsListElement.addContent(createElement("time", currentVideo == null ? "0" : currentVideo.getDuration().toString()));
            newsListElement.addContent(createElement("provider", "乐视体育"));
            newsListElement.addContent(createElement("url", currentVideo == null ? "" : "http://sports.le.com/video/" + currentnew.getVid() + ".html"));

            newsLit.add(newsListElement);
        }
        return newsLit;
    }

    //players列表
    private List<Element> getPlayerRanksListElements() {
        List<Element> ranksLit = Lists.newArrayList();
        Element ranksListElement = createRooElement("ranklist");
        ranksListElement.addContent(createElement("rankname", "射手榜"));
        ranksListElement.addContent(createElement("rankurl", goalRankingUrl));
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndType(csid, 100160000L);
        if (CollectionUtils.isNotEmpty(topList)) {
            updateElement(ranksListElement, getRankPlayersListElements(topList.get(0).getItems()));
        }
        ranksLit.add(ranksListElement);

        Element ranksListElement2 = createRooElement("ranklist");
        ranksListElement2.addContent(createElement("rankname", "助攻榜"));
        ranksListElement2.addContent(createElement("rankurl", assistRankingUrl));
        List<TopList> topList2 = SbdsInternalApis.getTopListsByCsidAndType(csid, 100161000L);
        if (CollectionUtils.isNotEmpty(topList2)) {
            updateElement(ranksListElement2, getRankPlayersListElements(topList2.get(0).getItems()));
        }
        ranksLit.add(ranksListElement2);
        return ranksLit;
    }

    private List<Element> getRankPlayersListElements(List<TopList.TopListItem> players) {
        if (CollectionUtils.isEmpty(players)) return null;
        List<Element> ranksLit = Lists.newArrayList();
        for (TopList.TopListItem currentPlayer : players) {
            if (currentPlayer == null) continue;
            Player player = SbdsInternalApis.getPlayerById(currentPlayer.getCompetitorId());
            Team team = SbdsInternalApis.getTeamById(currentPlayer.getTeamId());
            Element ranksListElement = createRooElement("list");
            ranksListElement.addContent(createElement("img_player", player.getImageUrl()));
            ranksListElement.addContent(createElement("name_player", player.getName()));
            ranksListElement.addContent(createElement("position", player.getPositionName()));
            ranksListElement.addContent(createElement("team", team.getName()));
            ranksListElement.addContent(createElement("score", currentPlayer.getStats().get("goals") != null ? currentPlayer.getStats().get("goals").toString() : currentPlayer.getStats().get("assists").toString()));
            ranksLit.add(ranksListElement);
        }
        return ranksLit;
    }

    //  team rankings列表
    private List<Element> getTeamGroupRankElements() {
        List<Element> groupRankings = Lists.newArrayList();

        groupRankings.add(createGroupElement("A"));
        groupRankings.add(createGroupElement("B"));
        groupRankings.add(createGroupElement("C"));
        groupRankings.add(createGroupElement("D"));
        return groupRankings;
    }

    private Element createGroupElement(String group) {
        Element groupA = createRooElement("group");
        groupA.addContent(getTh());
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isNotEmpty(topList)) {
            updateElement(groupA, getTds(topList.get(0).getItems()));
        }
        return groupA;

    }

    private List<Element> getTds(List<TopList.TopListItem> items) {
        if (CollectionUtils.isEmpty(items)) return null;
        List<Element> tds = Lists.newArrayList();
        for (TopList.TopListItem item : items) {
            Team currentTeam = SbdsInternalApis.getTeamById(item.getTeamId());
            Element td = createRooElement("td");
            td.addContent(createElement("img_td", currentTeam.getBgLogo()));
            td.addContent(createElement("name_td", currentTeam.getName()));
            td.addContent(createElement("con_td", item.getStats().get("matchNumber").toString()));
            td.addContent(createElement("con_td", item.getStats().get("winMatch").toString()));
            td.addContent(createElement("con_td", item.getStats().get("flatMatch").toString()));
            td.addContent(createElement("con_td", item.getStats().get("lossMatch").toString()));
            td.addContent(createElement("con_td", item.getStats().get("goal").toString()));
            td.addContent(createElement("con_td", item.getStats().get("teamScore").toString()));
            td.addContent(createElement("lift_mark", "0"));
            tds.add(td);
        }
        return tds;
    }

    private Element getTh() {
        Element th = createRooElement("th");
        th.addContent(createElement("name", "球队名称"));
        th.addContent(createElement("name", "场次"));
        th.addContent(createElement("name", "胜"));
        th.addContent(createElement("name", "平"));
        th.addContent(createElement("name", "负"));
        th.addContent(createElement("name", "净胜球"));
        th.addContent(createElement("name", "积分"));
        return th;
    }

    public String getMatchDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "月" + day + "号";
    }

    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
