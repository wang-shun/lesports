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
@Service("BaiduTeamAganistService")
public class Baidu_team_aganistGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_team_aganistGenerator.class);

    public String getFilePath() {
        String filePath = "teamcounterpart_mobile_copaAmerican2016.xml";
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
            item.addContent(createElement("key", team.getName()));
            item.addContent(getDisplayElement(team));
            items.add(item);
        }
        return items;
    }


    private Element getDisplayElement(Team team) {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", teamUrl.replace("?", team.getId().toString())+baidu_logo));
        displayElement.addContent(createElement("tab_name", "赛事信息"));
        displayElement.addContent(createElement("name", "近期战绩"));
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
            Element item = createRooElement("list");
            item.addContent(createElement("date", getmatchDate((currentMatch.getStartDate()))));
            item.addContent(createElement("match", "美洲杯"));
            Map vsMap = Maps.newHashMap();
            vsMap.put("team1", homeTeam.getName());
            vsMap.put("team2", awayTeam.getName());
            vsMap.put("text", homeScore + "-" + awayScore);
            vsMap.put("style", "0");
           item.addContent(getElementWithAttributes("vs",vsMap));
            tableContents.add(item);
        }
        return tableContents;
    }


    private String getmatchDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "/" + day;
    }


}
