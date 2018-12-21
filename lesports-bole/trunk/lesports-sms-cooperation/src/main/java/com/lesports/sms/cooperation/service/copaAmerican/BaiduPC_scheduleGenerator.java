package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TopList;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduPCScheduleService")
public class BaiduPC_scheduleGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduPC_scheduleGenerator.class);

    public String getFilePath() {
        String filePath = "scheduleandresult_pc_copaAmerican2016.xml";
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
        displayElement.addContent(createElement("url", resourceUrl + baidu_logo));
        displayElement.addContent(createElement("title", "2016美国美洲杯_乐视体育"));
        displayElement.addContent(createElement("show_index", "1"));
        if (createGroupSchedule("1/4决赛", "1") != null) {
            displayElement.addContent(getTabPlayOffGroupElement());
        } else {
            displayElement.addContent(getTabRoundElement());
        }
        displayElement.addContent(getTabScheduleGroupElement());
        if (CollectionUtils.isNotEmpty(getPlayerRanksListElements())) {
            displayElement.addContent(getTabPlayerRankingElement());
        }
        displayElement.addContent(getTabRankingElement());
        return displayElement;
    }


    private Element getTabScheduleGroupElement() {
        //添加小组赛赛程
        Element tabElement4 = createRooElement("tab");
        tabElement4.addContent(createElement("tab_name", "小组赛赛程"));
        tabElement4.addContent(createElement("show_style", "match"));
        tabElement4.addContent(createGroupElement("A", "1"));
        tabElement4.addContent(createGroupElement("B", "2"));
        tabElement4.addContent(createGroupElement("C", "3"));
        tabElement4.addContent(createGroupElement("D", "4"));
        tabElement4.addContent(createElement("show_group_index", "1"));
        updateElement(tabElement4, createGroupSchedule("A", "1"));
        updateElement(tabElement4, createGroupSchedule("B", "2"));
        updateElement(tabElement4, createGroupSchedule("C", "3"));
        updateElement(tabElement4, createGroupSchedule("D", "4"));
        return tabElement4;
    }

    private Element getTabPlayOffGroupElement() {
        Element tabElement4 = createRooElement("tab");
        tabElement4.addContent(createElement("tab_name", "淘汰赛赛程"));
        tabElement4.addContent(createElement("show_style", "match"));
        tabElement4.addContent(createGroupElement("1/4决赛", "1"));
        tabElement4.addContent(createGroupElement("半决赛", "2"));
        tabElement4.addContent(createGroupElement("三四名决赛", "3"));
        tabElement4.addContent(createGroupElement("决赛", "4"));
        tabElement4.addContent(createElement("show_group_index", "4"));
        updateElement(tabElement4, createGroupSchedule("1/4决赛", "1"));
        updateElement(tabElement4, createGroupSchedule("半决赛", "2"));
        updateElement(tabElement4, createGroupSchedule("三四名决赛", "3"));
        updateElement(tabElement4, createGroupSchedule("决赛", "4"));
        return tabElement4;
    }

    private Element getTabRoundElement() {
        Element tabElement4 = createRooElement("tab");
        tabElement4.addContent(createElement("tab_name", "第3轮"));
        tabElement4.addContent(createElement("show_style", "match"));
        updateElement(tabElement4, createGroupSchedule("第3轮", "1"));
        return tabElement4;
    }

    private Element getTabRankingElement() {
        //添加球队积分榜
        Element tabElement4 = createRooElement("tab");
        tabElement4.addContent(createElement("tab_name", "积分榜"));
        tabElement4.addContent(createElement("show_style", "teamscore"));
        tabElement4.addContent(createGroupElement("A", "1"));
        tabElement4.addContent(createGroupElement("B", "2"));
        tabElement4.addContent(createGroupElement("C", "3"));
        tabElement4.addContent(createGroupElement("D", "4"));
        tabElement4.addContent(createElement("show_group_index", "1"));
        Map map1 = Maps.newHashMap();
        map1.put("name", "排名");
        tabElement4.addContent(getElementWithAttributes("table_head", map1));
        Map map3 = Maps.newHashMap();
        map3.put("name", "国家");
        tabElement4.addContent(getElementWithAttributes("table_head", map3));
        Map map4 = Maps.newHashMap();
        map4.put("name", "胜/平/负");
        tabElement4.addContent(getElementWithAttributes("table_head", map4));
        Map map5 = Maps.newHashMap();
        map5.put("name", "净胜球");
        tabElement4.addContent(getElementWithAttributes("table_head", map5));
        Map map2 = Maps.newHashMap();
        map2.put("name", "积分");
        tabElement4.addContent(getElementWithAttributes("table_head", map2));
        updateElement(tabElement4, createGroupRanking("A", "1"));
        updateElement(tabElement4, createGroupRanking("B", "2"));
        updateElement(tabElement4, createGroupRanking("C", "3"));
        updateElement(tabElement4, createGroupRanking("D", "4"));
        return tabElement4;
    }

    private Element getTabPlayerRankingElement() {
        //添加球员榜
        Element tabElement3 = createRooElement("tab");
        tabElement3.addContent(createElement("tab_name", "射手榜"));
        tabElement3.addContent(createElement("show_style", "player"));
        Map map1 = Maps.newHashMap();
        map1.put("name", "排名");
        tabElement3.addContent(getElementWithAttributes("table_head", map1));
        Map map2 = Maps.newHashMap();
        map2.put("name", "球员");
        tabElement3.addContent(getElementWithAttributes("table_head", map2));
        Map map3 = Maps.newHashMap();
        map3.put("name", "球队");
        tabElement3.addContent(getElementWithAttributes("table_head", map3));
        Map map5 = Maps.newHashMap();
        map5.put("name", "进球");
        tabElement3.addContent(getElementWithAttributes("table_head", map5));
        updateElement(tabElement3, getPlayerRanksListElements());
        return tabElement3;
    }


    //赛程分组列表
    private List<Element> createGroupSchedule(String group, String key) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        if (idRoundMaps.get(group) != null) {
            q.addCriteria(new InternalCriteria("round", "eq", idRoundMaps.get(group)));
        } else if (idMaps.get(group) != null) {
            q.addCriteria(new InternalCriteria("group", "eq", idMaps.get(group)));
        } else if (idStageMaps.get(group) != null) {
            q.addCriteria(new InternalCriteria("stage", "eq", idStageMaps.get(group)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new Sort(Lists.newArrayList(new Sort.Order(Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        List<Element> matchElements = Lists.newArrayList();
        for (Match match : matches) {
            Team homeTeam = null;
            Team awayTeam = null;
            String homeScore = "0";
            String awayScore = "0";
            Set<Match.Competitor> teams = match.getCompetitors();
            for (Match.Competitor currentCompetitor : teams) {
                Team team = SbdsInternalApis.getTeamById(currentCompetitor.getCompetitorId());
                if (currentCompetitor.getGround().equals(GroundType.HOME)) {
                    homeTeam = team;
                    homeScore = StringUtils.isBlank(currentCompetitor.getFinalResult()) ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = StringUtils.isBlank(currentCompetitor.getFinalResult()) ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            Element tab_row = createRooElement("table_row");
            tab_row.addContent(createElement("key", key));
            Map matchData = Maps.newHashMap();
            matchData.put("status", getmatchStatus(match.getStatus()));
            matchData.put("time", getmatchDate(match.getStartDate()) + " " + getMatchTime(match.getStartTime()));
            matchData.put("country1", homeTeam.getName());
            matchData.put("country1img", logoMaps.get(homeTeam.getId()) == null ? "" : logoMaps.get(homeTeam.getId()));
            matchData.put("country1url", teamUrl.replace("?", homeTeam.getId().toString()) + baidu_logo);
            matchData.put("country2", awayTeam.getName());
            matchData.put("country2img", logoMaps.get(awayTeam.getId()) == null ? "" : logoMaps.get(awayTeam.getId()));
            matchData.put("country2url", teamUrl.replace("?", awayTeam.getId().toString()) + baidu_logo);
            matchData.put("vs_text", getScore(match.getStatus(), homeScore, awayScore));
            matchData.put("team", "小组赛");
            Map vedioData1 = Maps.newHashMap();
            if (match.getStatus().equals(MatchStatus.MATCH_END)) {
                vedioData1.put("text", "视频回放");
                vedioData1.put("url", matchPageUrl("RECORD", match.getId(), baidu_logo));
            } else {
                vedioData1.put("text", "视频直播");
                vedioData1.put("url", matchPageUrl("", match.getId(), baidu_logo));
            }
            Map vedioData2 = Maps.newHashMap();
            vedioData2.put("text", "视频集锦");
            vedioData2.put("url", matchPageUrl("HIGHLIGHTS", match.getId(), baidu_logo));
            tab_row.addContent(getElementWithAttributes("match_data", matchData));
            tab_row.addContent(getElementWithAttributes("match_links", vedioData1));
            tab_row.addContent(getElementWithAttributes("match_links", vedioData2));
            matchElements.add(tab_row);
        }
        return matchElements;
    }

    //players列表
    private List<Element> getPlayerRanksListElements() {
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndType(csid, 100160000L);
        if (CollectionUtils.isEmpty(topList) || CollectionUtils.isEmpty(topList.get(0).getItems())) return null;
        List<TopList.TopListItem> items = getOrderedTopListItems(topList.get(0));
        List<Element> ranksLit = Lists.newArrayList();
        List<TopList.TopListItem> existList = Lists.newArrayList();
        int count = 1;
        for (TopList.TopListItem currentPlayer : items) {
            if (currentPlayer == null) continue;
            if (existList.contains(currentPlayer)) continue;
            existList.add(currentPlayer);
            if (count > 5) break;
            Element table_row = createRooElement("table_row");
            Player player = SbdsInternalApis.getPlayerById(currentPlayer.getCompetitorId());
            Team team = SbdsInternalApis.getTeamById(currentPlayer.getTeamId());
            Map tableData1 = Maps.newHashMap();
            tableData1.put("text", currentPlayer.getRank());
            table_row.addContent(getElementWithAttributes("table_data", tableData1));
            Map tableData2 = Maps.newHashMap();
            tableData2.put("text", player.getName());
            table_row.addContent(getElementWithAttributes("table_data", tableData2));
            Map tableData3 = Maps.newHashMap();
            tableData3.put("text", team.getName());
            tableData3.put("url", teamUrl.replace("?", team.getId().toString()));
            tableData3.put("img", logoMaps.get(team.getId()));
            table_row.addContent(getElementWithAttributes("table_data", tableData3));
            Map tableData5 = Maps.newHashMap();
            String goal = currentPlayer.getStats() == null || currentPlayer.getStats().get("goals") == null ? "0" : currentPlayer.getStats().get("goals").toString();
            tableData5.put("text", goal);
            table_row.addContent(getElementWithAttributes("table_data", tableData5));
            ranksLit.add(table_row);
            count++;
        }
        return ranksLit;
    }

    //  team rankings列表

    private Element createGroupElement(String group, String key) {
        Map attributes = Maps.newHashMap();
        attributes.put("name", group + "组");
        attributes.put("key", key);
        Element groupA = getElementWithAttributes("group", attributes);
        return groupA;
    }

    private List<Element> createGroupRanking(String group, String key) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("group", "eq", idMaps.get(group)));
        List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isEmpty(topLists) || CollectionUtils.isEmpty(topLists.get(0).getItems())) return null;
        List<Element> items = Lists.newArrayList();
        for (TopList.TopListItem item : topLists.get(0).getItems()) {
            Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());

            Element table_row = createRooElement("table_row");
            table_row.addContent(createElement("key", key));
            Map data1 = Maps.newHashMap();
            data1.put("text", item.getRank());
            table_row.addContent(getElementWithAttributes("table_data", data1));
            Map data2 = Maps.newHashMap();
            data2.put("text", currentTeam.getName());
            data2.put("img", logoMaps.get(currentTeam.getId()));
            table_row.addContent(getElementWithAttributes("table_data", data2));
            Map data3 = Maps.newHashMap();
            String content = item.getStats().get("winMatch").toString() + "/" + item.getStats().get("flatMatch").toString() + "/" + item.getStats().get("lossMatch").toString();
            data3.put("text", content);
            table_row.addContent(getElementWithAttributes("table_data", data3));
            Map data4 = Maps.newHashMap();
            Integer goalDif = Integer.valueOf(item.getStats().get("goal").toString()) - Integer.valueOf(item.getStats().get("fumble").toString());
            data4.put("text", goalDif.toString());
            table_row.addContent(getElementWithAttributes("table_data", data4));
            Map data6 = Maps.newHashMap();
            data6.put("text", item.getStats().get("teamScore").toString());
            table_row.addContent(getElementWithAttributes("table_data", data6));
            items.add(table_row);
        }
        return items;
    }

    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "/" + day;
    }

    public String getmatchStatus(MatchStatus status) {
        if (status.toString().equals("MATCH_END")) return "2";
        else if (status.toString().equals("MATCHING")) return "1";
        else return "0";
    }

    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
