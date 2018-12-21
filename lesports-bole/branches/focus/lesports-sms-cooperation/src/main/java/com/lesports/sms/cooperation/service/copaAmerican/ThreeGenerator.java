package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("360Service")
public class ThreeGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(ThreeGenerator.class);


    public String getFilePath() {
        String filePath = "scheduleandresult_copaAmerican2016.xml";
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
        displayElement.addContent(createElement("title", "美国美洲杯2016赛季_赛程赛果_乐视体育"));
        displayElement.addContent(createElement("url", resourceUrl + one_box_logo));
        displayElement.addContent(createRooElement("current_tab"));
        Element sonElement = createRooElement("tab_area");
        updateElement(sonElement, getVilistElements());
        sonElement.addContent(getGroupElements());
        if (getTabGroup("1/4决赛") != null) {
            sonElement.addContent(getPlayoffElements());
        }
        sonElement.addContent(getTab_group_score());
        displayElement.addContent(sonElement);
        displayElement.addContent(createElement("showurl", "www.lesports.com"));
        return displayElement;
    }

    //赛程列表日期
    private List<Element> getVilistElements() {
        List<Element> scheduleLists = Lists.newArrayList();
        Date validDate = new Date().before(CommonUtil.getDataYYMMDD(copa_start_date)) ? CommonUtil.getDataYYMMDD(copa_start_date) : new Date();
        int i = 0;
        scheduleLists.add(getCurrentSchedule(validDate));
        if(getNextSchedule(validDate)!=null) {
            scheduleLists.add(getNextSchedule(validDate));
        }
        return scheduleLists;
    }

    private Element getNextSchedule(Date validDate) {
        Date currentDate = CommonUtil.getNextDate(validDate);
        List<Match> matches = Lists.newArrayList();
        while (currentDate.after(validDate)&&currentDate.before(CommonUtil.getDataYYMMDD("20160630"))) {
            InternalQuery q1 = new InternalQuery();
            q1.addCriteria(new InternalCriteria("csid", "eq", csid));
            q1.addCriteria(new InternalCriteria("start_date", "eq", CommonUtil.getYYYYMMDDDate(currentDate)));
            q1.addCriteria(new InternalCriteria("deleted", "eq", false));
            matches = SbdsInternalApis.getMatchsByQuery(q1);
            if (CollectionUtils.isEmpty(matches)) {
                currentDate = CommonUtil.getNextDate(currentDate);
                continue;
            } else break;
        }
        if(CommonUtil.getYYYYMMDDDate(currentDate).equals("20160630"))return null;
        Element tabElement = createRooElement("tab_match");
        tabElement.addContent(createElement("name", getDate(CommonUtil.getYYYYMMDDDate(currentDate))));
        Element title = createRooElement("title");
        title.addContent(createElement("li", "日期"));
        title.addContent(createElement("li", "时间"));
        title.addContent(createElement("li", "对阵"));
        title.addContent(createElement("li", "轮次"));
        title.addContent(createElement("li", "赛事观看"));
        tabElement.addContent(title);
        updateElement(tabElement, getoneContents("0", matches));
        tabElement.addContent(createElement("moretext", "查看美洲杯全部赛程>>"));
        tabElement.addContent(createElement("moreurl", matchesUrl + one_box_logo));
        return tabElement;
    }


    private Element getCurrentSchedule(Date validDate) {
        Date currentDate = validDate;
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("start_date", "eq", CommonUtil.getYYYYMMDDDate(validDate)));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) {
            currentDate = CommonUtil.getBeforeDate(validDate);
            while (currentDate.before(validDate)) {
                InternalQuery q1 = new InternalQuery();
                q1.addCriteria(new InternalCriteria("csid", "eq", csid));
                q1.addCriteria(new InternalCriteria("start_date", "eq", CommonUtil.getYYYYMMDDDate(currentDate)));
                q1.addCriteria(new InternalCriteria("deleted", "eq", false));
                matches = SbdsInternalApis.getMatchsByQuery(q1);
                if (CollectionUtils.isEmpty(matches)) {
                    currentDate = CommonUtil.getBeforeDate(currentDate);
                    continue;
                } else break;
            }
        }
        Element tabElement = createRooElement("tab_match");
        tabElement.addContent(createElement("name", getDate(CommonUtil.getYYYYMMDDDate(currentDate))));
        Element title = createRooElement("title");
        title.addContent(createElement("li", "日期"));
        title.addContent(createElement("li", "时间"));
        title.addContent(createElement("li", "对阵"));
        title.addContent(createElement("li", "组别"));
        title.addContent(createElement("li", "赛事观看"));
        tabElement.addContent(title);
        updateElement(tabElement, getoneContents("0", matches));
        tabElement.addContent(createElement("moretext", "查看美洲杯全部赛程>>"));
        tabElement.addContent(createElement("moreurl", matchesUrl + one_box_logo));

        return tabElement;
    }


    //赛程列表小组
    private Element getGroupElements() {
        Element tab_group_match = createRooElement("tab_group_match");
        tab_group_match.addContent(createElement("name", "小组赛程"));
        tab_group_match.addContent(createElement("current_group", "A"));
        tab_group_match.addContent(getTabGroup("A"));
        tab_group_match.addContent(getTabGroup("B"));
        tab_group_match.addContent(getTabGroup("C"));
        tab_group_match.addContent(getTabGroup("D"));
        tab_group_match.addContent(createElement("moretext", "查看美洲杯全部赛程>>"));
        tab_group_match.addContent(createElement("moreurl", matchesUrl + one_box_logo));
        return tab_group_match;
    }

    private Element getTabGroup(String group) {
        Element tab_group = createRooElement("tab_group");
        tab_group.addContent(createElement("title", group));
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "is", csid));
        if (idMaps.get(group) != null) {
            q.addCriteria(new InternalCriteria("group", "is", idMaps.get(group)));
        } else if (idStageMaps.get(group) != null) {
            q.addCriteria(new InternalCriteria("stage", "is", idStageMaps.get(group)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        updateElement(tab_group, getoneContents("1", matches));
        return tab_group;

    }

    //playoff
    private Element getPlayoffElements() {
        Element tab_group_match = createRooElement("tab_group_match");
        tab_group_match.addContent(createElement("name", "淘汰赛赛程"));
        tab_group_match.addContent(createElement("current_group", "决赛"));
        tab_group_match.addContent(getTabGroup("1/4决赛"));
        if (getTabGroup("半决赛") != null) tab_group_match.addContent(getTabGroup("半决赛"));
        if (getTabGroup("三四名决赛") != null) tab_group_match.addContent(getTabGroup("三四名决赛"));
        if (getTabGroup("决赛") != null) tab_group_match.addContent(getTabGroup("决赛"));
        tab_group_match.addContent(createElement("moretext", "查看美洲杯全部赛程>>"));
        tab_group_match.addContent(createElement("moreurl", matchesUrl + one_box_logo));
        return tab_group_match;
    }

    private List<Element> getoneContents(String flag, List<Match> matchList) {
        List<Element> matches = Lists.newArrayList();
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
                    homeScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            Element oneContent = createRooElement("onecontent");
            oneContent.addContent(createElement("status", getmatchStatus(currentMatch.getStatus())));
            oneContent.addContent(createElement("date", getMatchDate(currentMatch.getStartDate())));
            oneContent.addContent(createElement("time", getMatchTime(currentMatch.getStartTime())));
            Element team_vs = createRooElement("team_vs");
            team_vs.addContent(createElement("team1", homeTeam.getName()));
            team_vs.addContent(createElement("team1url", teamUrl.replace("?", homeTeam.getId().toString()) + one_box_logo));
            team_vs.addContent(createElement("team1icon", getValidLogo15(homeTeam.getLogo())));
            team_vs.addContent(createElement("team2", awayTeam.getName()));
            team_vs.addContent(createElement("team2url", teamUrl.replace("?", awayTeam.getId().toString()) + one_box_logo));
            team_vs.addContent(createElement("team2icon", getValidLogo15(awayTeam.getLogo())));
            team_vs.addContent(createElement("score", getScore(currentMatch.getStatus(), homeScore, awayScore)));
            oneContent.addContent(team_vs);
            if (flag.equals("0") && SbdsInternalApis.getDictById(currentMatch.getGroup()) != null) {
                oneContent.addContent(createElement("group", SbdsInternalApis.getDictById(currentMatch.getGroup()).getName()));
            } else {
                oneContent.addContent(createElement("group", "淘汰赛"));
            }
            updateElement(oneContent, getLinks(currentMatch));
            matches.add(oneContent);
        }
        return matches;
    }

    private List<Element> getLinks(Match match) {
        List<Element> links = Lists.newArrayList();
        Element link = createRooElement("link");
        link.addContent(createElement("text", match.getStatus().toString().equals("MATCH_END") ? "视频集锦" : "视频直播"));
        link.addContent(createElement("url", match.getStatus().toString().equals("MATCH_END") ? matchPageUrl("HIGHLIGHTS", match.getId(), one_box_logo) : matchPageUrl("", match.getId(), one_box_logo)));
        links.add(link);
        return links;
    }

    //jifenbang列表
    private Element getTab_group_score() {
        Element tab_group_score = createRooElement("tab_group_score");
        tab_group_score.addContent(createElement("name", "小组积分榜"));
        Element title = createRooElement("title");
        title.addContent(createElement("li", "排名"));
        title.addContent(createElement("li", "球队"));
        title.addContent(createElement("li", "场次"));
        title.addContent(createElement("li", "胜"));
        title.addContent(createElement("li", "平"));
        title.addContent(createElement("li", "负"));
        title.addContent(createElement("li", "净胜球"));
        title.addContent(createElement("li", "积分"));
        tab_group_score.addContent(title);
        tab_group_score.addContent(getTabGroupScore("A"));
        tab_group_score.addContent(getTabGroupScore("B"));
        tab_group_score.addContent(getTabGroupScore("C"));
        tab_group_score.addContent(getTabGroupScore("D"));
        tab_group_score.addContent(createElement("moretext", "查看完整积分榜>>"));
        tab_group_score.addContent(createElement("moreurl", teamRankingUrl + one_box_logo));
        return tab_group_score;
    }


    private Element getTabGroupScore(String group) {
        Element group_score = createRooElement("group_score");
        group_score.addContent(createElement("title", group));
        Long groupId = idMaps.get(group);
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, groupId);
        if (CollectionUtils.isNotEmpty(topList))
            updateElement(group_score, getoneContentsScoreList(topList.get(0).getItems()));
        return group_score;
    }


    private List<Element> getoneContentsScoreList(List<TopList.TopListItem> items) {
        if (CollectionUtils.isEmpty(items)) return null;
        List<Element> tds = Lists.newArrayList();
        for (TopList.TopListItem item : items) {
            Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());
            Element td = createRooElement("onecontent");

            td.addContent(createParentElement("li", "text", item.getRank().toString()));
            Element li = createRooElement("li");
            li.addContent(createElement("text", currentTeam.getName()));
            li.addContent(createElement("url", teamUrl.replace("?", currentTeam.getId().toString()) + one_box_logo));
            td.addContent(li);
            td.addContent(createParentElement("li", "text", item.getStats().get("matchNumber").toString()));
            td.addContent(createParentElement("li", "text", item.getStats().get("winMatch").toString()));
            td.addContent(createParentElement("li", "text", item.getStats().get("flatMatch").toString()));
            td.addContent(createParentElement("li", "text", item.getStats().get("lossMatch").toString()));
            Integer goalDif = Integer.valueOf(item.getStats().get("goal").toString()) - Integer.valueOf(item.getStats().get("fumble").toString());
            td.addContent(createParentElement("li", "text", goalDif.toString()));
            td.addContent(createParentElement("li", "text", item.getStats().get("teamScore").toString()));
            tds.add(td);
        }
        return tds;
    }

    private String getDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String weekDay = CommonUtil.getWeekday(date);
        return month + "月" + day + "日";
    }

    public String getMatchDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String weekDay = CommonUtil.getWeekday(date);
        if (weekDay.contains("星期")) {
            return month + "-" + day + weekDay.replace("星期", "周");
        } else {
            LOG.info("current date,{},current weekDay,{}", date, weekDay);
            return month + "-" + day + weekMaps.get(weekDay);
        }
    }

    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
