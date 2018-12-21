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
@Service("BaiduAganistService")
public class Baidu_aganistGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_aganistGenerator.class);

    public String getFilePath() {
        String filePath = "counterpart_mobile_copaAmerican2016.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
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
            Map<String, Object> attributes = Maps.newHashMap();
            Element item = createRooElement("item");
            item.addContent(createElement("key", homeTeam.getName() + awayTeam.getName()));
            item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore));
            items.add(item);
        }
        return items;
    }


    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchPageUrl("",match.getId(),baidu_logo)));
        //   displayElement.addContent(createElement("title", "2016美国美洲杯_" + homeTeam.getName() + "VS" + awayTeam.getName()));
        displayElement.addContent(createElement("state", getmatchStatus(match.getStatus())));
        displayElement.addContent(createElement("date", getmatchDate(match.getStartDate())));
        displayElement.addContent(createElement("time", getMatchTime(match.getStartTime())));
        displayElement.addContent(getRanks(match, homeTeam, awayTeam, homeScore, awayScore));
        return displayElement;
    }


    //添加球员排行榜
    private Element getRanks(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        Element tabElement3 = createRooElement("ranks");
        tabElement3.addContent(createElement("score", homeScore + "-" + awayScore));//06/07(星期日) 02:45开赛
        Map team1Map = Maps.newHashMap();
        team1Map.put("icon", logoMaps.get(homeTeam.getId()));
        team1Map.put("name", homeTeam.getName());
        tabElement3.addContent(getElementWithAttributes("rank", team1Map));
        Map team2Map = Maps.newHashMap();
        team2Map.put("icon", logoMaps.get(awayTeam.getId()));
        team2Map.put("name", awayTeam.getName());
        tabElement3.addContent(getElementWithAttributes("rank", team2Map));
        return tabElement3;
    }


    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String weekDay = CommonUtil.getWeekday(date);
        return month + "-" + day + "/周" + weekDay.substring(2, 3);
    }


    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
