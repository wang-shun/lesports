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
@Service("BaiduPCAganistService")
public class BaiduPC_agamistGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduPC_agamistGenerator.class);

    public String getFilePath() {
        String filePath = "counterpart_pc_copaAmerican2016.xml";
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
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
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
            Element item = createRooElement("item");
            item.addContent(createElement("key", homeTeam.getName() + awayTeam.getName()));
            item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore));
            items.add(item);
        }
        return items;
    }


    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchesUrl.replace("?", match.getId().toString())+baidu_logo));
        displayElement.addContent(createElement("title", "2016美国美洲杯_" + homeTeam.getName() + "VS" + awayTeam.getName()));
        displayElement.addContent(createElement("show_count", "5"));
        displayElement.addContent(getMatch(match, homeTeam, awayTeam, homeScore, awayScore));
        return displayElement;
    }


    //
    private Element getMatch(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        Element tabElement3 = createRooElement("match");
        tabElement3.addContent(createElement("time_text", getmatchDate(match.getStartDate()) + " " + getMatchTime(match.getStartTime()) + "开赛"));//06/07(星期日) 02:45开赛
        Map team1Map = Maps.newHashMap();
        team1Map.put("text", homeTeam.getName());
        team1Map.put("url", teamUrl.replace("?", homeTeam.getId().toString())+baidu_logo);
        team1Map.put("img",logoMaps.get( homeTeam.getId()));
        team1Map.put("team", "");
        team1Map.put("score", getTeamScore(match.getGroup(),homeTeam.getId()));
        team1Map.put("rank", "小组排名"+getTeamrank(match.getGroup(),homeTeam.getId()));
        tabElement3.addContent(getElementWithAttributes("country1", team1Map));
        Map team2Map = Maps.newHashMap();
        team2Map.put("text", awayTeam.getName());
        team2Map.put("url", teamUrl.replace("?", awayTeam.getId().toString())+baidu_logo);
        team2Map.put("img", logoMaps.get( awayTeam.getId()));
        team2Map.put("team", "");
        team2Map.put("score", getTeamScore(match.getGroup(),awayTeam.getId()));
        team2Map.put("rank", "小组排名"+getTeamrank(match.getGroup(),awayTeam.getId()));
        tabElement3.addContent(getElementWithAttributes("country2", team2Map));
        tabElement3.addContent(createElement("vs_text", homeScore + "-" + awayScore));
        tabElement3.addContent(createElement("status", getmatchStatus(match.getStatus())));
        Map link1Map = Maps.newHashMap();
        link1Map.put("text", "视频直播");
        link1Map.put("showicon", "1");
        link1Map.put("url", matchPageUrl("",match.getId(),baidu_logo));
        tabElement3.addContent(getElementWithAttributes("links", link1Map));
        Map link2Map = Maps.newHashMap();
        link2Map.put("text", "视频集锦");
        link2Map.put("url", matchPageUrl("HIGHLIGHTS",match.getId(),baidu_logo));
        tabElement3.addContent(getElementWithAttributes("links", link2Map));
        Map link3Map = Maps.newHashMap();
        link3Map.put("text", "技术统计");
        link3Map.put("url", matchPageUrl("",match.getId(),baidu_logo));
        tabElement3.addContent(getElementWithAttributes("links", link3Map));
        Map link4Map = Maps.newHashMap();
        link4Map.put("text", "滚动新闻");
        link4Map.put("url", matchPageUrl("",match.getId(),baidu_logo));
        tabElement3.addContent(getElementWithAttributes("links", link4Map));
        return tabElement3;
    }


    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String weekDay = CommonUtil.getWeekday(date);
        return month + "/" + day + "(" + weekDay + ")";
    }

    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
