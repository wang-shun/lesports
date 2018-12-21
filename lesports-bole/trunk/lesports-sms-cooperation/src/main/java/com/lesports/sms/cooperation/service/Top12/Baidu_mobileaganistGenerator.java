package com.lesports.sms.cooperation.service.Top12;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.service.copaAmerican.AbstractService;
import com.lesports.sms.cooperation.util.CommonUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
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
@Service("BaiduMobileAganistService")
public class Baidu_mobileaganistGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_mobileaganistGenerator.class);

    public String getFilePath() {
        String filePath = "counterpart_mobile_WordCupTop122018.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        updateElement(root, getItemElement());
        return new Document(root);
    }

    private List<Element> getItemElement() {
        List<Element> items = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("competitors.competitor_id", "is", 1440006));
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new Sort(Lists.newArrayList(new Sort.Order(Sort.Direction.ASC, "start_time"))));
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
            Map<String, Object> attributes = Maps.newHashMap();
            Element item = createRooElement("item");
            item.addContent(createElement("key", homeTeam.getName() + "vs" + awayTeam.getName()));
            item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore));
            items.add(item);
        }
        return items;
    }


    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchPageUrl("", match.getId(), baidu_logo)));
        displayElement.addContent(createElement("title", homeTeam.getName() + "vs" + awayTeam.getName() + "_乐视体育"));
        displayElement.addContent(createElement("highlight", homeTeam.getName() + "vs" + awayTeam.getName()));
        displayElement.addContent(createElement("gameid", match.getId().toString()));
        displayElement.addContent(createElement("game_time", getMatchTime(match.getStartTime())));
        displayElement.addContent(createElement("game_date", getmatchDate(match.getStartDate())));
        DictEntry dictEntry = SbdsInternalApis.getDictById(match.getRound());
        if(dictEntry!=null){
            displayElement.addContent(createElement("game_round", dictEntry.getName()));
        }

        displayElement.addContent(createElement("state", getmatchStatus(match.getStatus())));
        displayElement.addContent(createRooElement("game_progresstime"));
        displayElement.addContent(getTeamElement("home", homeTeam, homeScore));
        displayElement.addContent(getTeamElement("away", awayTeam, awayScore));
        displayElement.addContent(createRooElement("regular_time_goal"));
        displayElement.addContent(createRooElement("extra_time_goal"));
        displayElement.addContent(createRooElement("penalties_time_goal"));
        displayElement.addContent(createElement("provider", "乐视体育"));
        displayElement.addContent(createElement("bottomright_text", "查看比赛详情"));
        displayElement.addContent(createElement("bottomright_url", matchPageUrl("", match.getId(), baidu_logo)));

        return displayElement;
    }


    //添加球员排行榜
    private Element getTeamElement(String type, Team team, String teamSore) {
        Map team1Map = Maps.newHashMap();
        team1Map.put("name", team.getName());
        team1Map.put("score", teamSore);
        team1Map.put("logo", Constants.top12LogoUrlMap.get(team.getId()));
        team1Map.put("team_id", team.getId().toString());
        if (type.equalsIgnoreCase("home")) {
            Element tab = getElementWithAttributes("teamA", team1Map);
            return tab;
        } else {
            Element tab = getElementWithAttributes("teamB", team1Map);
            return tab;
        }

    }


    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String weekDay = CommonUtil.getWeekday(date);
        return month + "月" + day + "日";
    }


    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
