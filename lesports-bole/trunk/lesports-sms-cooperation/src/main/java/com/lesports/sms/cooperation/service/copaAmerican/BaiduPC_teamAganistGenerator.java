package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TeamSeason;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduPCTeamAganistService")
public class BaiduPC_teamAganistGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduPC_teamAganistGenerator.class);

    public String getFilePath() {
        String filePath = "teamcounterpart_pc_copaAmerican2016.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        root.addContent(getItemElement());
        return new Document(root);
    }

    private List<Element> getItemElement() {
        List<Element> items = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByQuery(q);
        if (CollectionUtils.isEmpty(teamSeasons)) return null;
        for (TeamSeason teamSeason : teamSeasons) {
            Team team = SbdsInternalApis.getTeamById(teamSeason.getTid());
            if (team == null) continue;
            Team awayTeam = null;
            Element item = createRooElement("item");
            item.addContent(createElement("key", team.getName() + "赛程"));
            item.addContent(getDisplayElement(team));
            items.add(item);
        }
        return items;
    }


    private Element getDisplayElement(Team team) {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", teamUrl.replace("?", team.getId().toString())));
        displayElement.addContent(createElement("title", "2016美国美洲杯_" + team.getName() + "赛程"));
        displayElement.addContent(createElement("show_count", "8"));
        updateElement(displayElement, getTableContent(team));
        return displayElement;
    }

    private List<Element> getTableContent(Team team1) {
        List<Element> tableContents = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("competitors.competitor_id", "eq", team1.getId()));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        if (CollectionUtils.isEmpty(matches)) return null;
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
            if (homeTeam == null || awayTeam == null) continue;
            Element item = createRooElement("table_content");
            item.addContent(createElement("status", getmatchStatus(currentMatch.getStatus())));
            item.addContent(createElement("date", getmatchDate((currentMatch.getStartDate()))));
            item.addContent(createElement("time", getMatchTime((currentMatch.getStartTime()))));
            Map team1Map = Maps.newHashMap();
            team1Map.put("text", homeTeam.getName());
            team1Map.put("url", teamUrl.replace("?", homeTeam.getId().toString()));
            team1Map.put("img", logoMaps.get(homeTeam.getId()));
            item.addContent(getElementWithAttributes("country1", team1Map));
            Map team2Map = Maps.newHashMap();
            team2Map.put("text", awayTeam.getName());
            team2Map.put("url", teamUrl.replace("?", awayTeam.getId().toString()));
            team2Map.put("img", logoMaps.get(awayTeam.getId()));
            item.addContent(getElementWithAttributes("country2", team2Map));
            item.addContent(createElement("vs_text", homeScore + "-" + awayScore));
            Map link1Map = Maps.newHashMap();
            link1Map.put("text", getmatchStatus(currentMatch.getStatus()).equals("2") ? "回放" : "直播");//直播
            link1Map.put("showicon", "1");
            link1Map.put("url",getmatchStatus(currentMatch.getStatus()).equals("2") ? matchPageUrl("RECORD",currentMatch.getId(),baidu_logo):matchPageUrl("",currentMatch.getId(),baidu_logo));
            item.addContent(getElementWithAttributes("links", link1Map));
            Map link2Map = Maps.newHashMap();
            link2Map.put("text", getmatchStatus(currentMatch.getStatus()).equals("2") ? "集锦" : "图文");//tuwen
            link2Map.put("url", getmatchStatus(currentMatch.getStatus()).equals("2")? matchPageUrl("HIGHLIGHTS",currentMatch.getId(),baidu_logo):matchPageUrl("",currentMatch.getId(),baidu_logo));
            item.addContent(getElementWithAttributes("links", link2Map));
            Map link3Map = Maps.newHashMap();
            link3Map.put("text", getmatchStatus(currentMatch.getStatus()).equals("0") ? "新闻" : "战报");//新闻
            link3Map.put("url", matchPageUrl("",currentMatch.getId(),baidu_logo));
            item.addContent(getElementWithAttributes("links", link3Map));
            Map link4Map = Maps.newHashMap();
            link4Map.put("text", "图集");
            link4Map.put("url", matchPageUrl("",currentMatch.getId(),baidu_logo));
            item.addContent(getElementWithAttributes("links", link4Map));

            tableContents.add(item);
        }
        return tableContents;
    }


    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "/" + day;
    }


    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
