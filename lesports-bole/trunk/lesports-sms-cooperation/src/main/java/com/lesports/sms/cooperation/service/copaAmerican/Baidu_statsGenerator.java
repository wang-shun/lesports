package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduStatsService")
public class Baidu_statsGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_statsGenerator.class);

    public String getFilePath() {
        String filePath = "stats_mobile_copaAmerican2016.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        if(CollectionUtils.isEmpty(getItemElement()))return null;
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
                    homeScore = currentCompetitor.getFinalResult() == null ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = currentCompetitor.getFinalResult() == null ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            if (homeTeam == null || awayTeam == null) continue;
            Element item = createRooElement("item");
            item.addContent(createElement("key", homeTeam.getName() + awayTeam.getName()));
            if (getDisplayElement(currentMatch, homeTeam, awayTeam) != null) {
                item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam));
                items.add(item);
            }
        }
        return items;
    }

    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam) {
        Set<Match.CompetitorStat> stats = match.getCompetitorStats();
        if (CollectionUtils.isEmpty(stats)) return null;
        Match.CompetitorStat homeStat = null;
        Match.CompetitorStat awayStat = null;
        for (Match.CompetitorStat stat : stats) {
            if (stat.getCompetitorId().equals(homeTeam.getId())) homeStat = stat;
            else awayStat = stat;
        }
        if (homeStat == null || awayStat == null) return null;

        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchPageUrl("",match.getId(),baidu_logo)));
        displayElement.addContent(createElement("tab_name", "统计"));
        displayElement.addContent(getList("数据对比", homeTeam.getName(), awayTeam.getName()));
        displayElement.addContent(getList("控球率", homeStat.getStats().get("ballPossession"), awayStat.getStats().get("ballPossession")));
        displayElement.addContent(getList("射门", getShot(homeStat), getShot(awayStat)));
        displayElement.addContent(getList("射正球门", homeStat.getStats().get("shotsOnGoal"), awayStat.getStats().get("shotsOnGoal")));
        displayElement.addContent(getList("扑救", homeStat.getStats().get("goalkeeperSaves"), awayStat.getStats().get("goalkeeperSaves")));
        displayElement.addContent(getList("角球", homeStat.getStats().get("cornerKicks"), awayStat.getStats().get("cornerKicks")));
        displayElement.addContent(getList("犯规", homeStat.getStats().get("fouls"), awayStat.getStats().get("fouls")));
        displayElement.addContent(getList("越位", homeStat.getStats().get("offsides"), awayStat.getStats().get("offsides")));
        displayElement.addContent(getList("黄牌", homeStat.getStats().get("yellow"), awayStat.getStats().get("yellow")));
        displayElement.addContent(getList("红牌", homeStat.getStats().get("red"), awayStat.getStats().get("red")));
        return displayElement;
    }

    private Integer getShot(Match.CompetitorStat stats) {
        return Integer.valueOf(stats.getStats().get("shotsOffGoal").toString()) + Integer.valueOf(stats.getStats().get("shotsOnGoal").toString());
    }

    //添加球员排行榜
    private Element getList(String key1, Object key2, Object key3) {

        Element tabElement3 = createRooElement("list");
        tabElement3.addContent(createElement("text", key1));
        tabElement3.addContent(createElement("text", key2.toString()));
        tabElement3.addContent(createElement("text", key3.toString()));
        return tabElement3;
    }

}


