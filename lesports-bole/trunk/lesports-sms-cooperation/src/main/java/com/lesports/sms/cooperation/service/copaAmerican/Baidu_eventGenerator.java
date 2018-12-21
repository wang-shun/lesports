package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.MatchAction;
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
@Service("BaiduEventService")
public class Baidu_eventGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_eventGenerator.class);

    public String getFilePath() {
        String filePath = "liveEvent_mobile_copaAmerican2016.xml";
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
                    homeScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            if (homeTeam == null || awayTeam == null) continue;
            Element item = createRooElement("item");
            item.addContent(createElement("key", homeTeam.getName() + awayTeam.getName()));
            if (getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore) != null) {
                item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore));
                items.add(item);
            }

        }
        return items;
    }

    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        if (CollectionUtils.isEmpty(getEventlists(match.getId()))) return null;
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchPageUrl("",match.getId(),baidu_logo)));
        Map teamAMap = Maps.newHashMap();
        teamAMap.put("name", homeTeam.getName());
        teamAMap.put("logo", logoMaps.get(homeTeam.getId()));
        teamAMap.put("team_id", homeTeam.getId().toString());
        displayElement.addContent(getElementWithAttributes("teamA", teamAMap));
        Map teamBMap = Maps.newHashMap();
        teamBMap.put("name", awayTeam.getName());
        teamBMap.put("logo", logoMaps.get(awayTeam.getId()));
        teamBMap.put("team_id", awayTeam.getId().toString());
        displayElement.addContent(getElementWithAttributes("teamB", teamBMap));
        updateElement(displayElement, getEventlists(match.getId()));
        return displayElement;
    }

    private List<Element> getEventlists(Long matchId) {
        List<MatchAction> actions = SbdsInternalApis.getMatchActionsByMid(matchId);
        if (CollectionUtils.isEmpty(actions)) return null;
        List<Element> lists = Lists.newArrayList();
        for (MatchAction action : actions) {
            Map actionMap = Maps.newHashMap();
            actionMap.put("minute", new Double(action.getPassedTime() / 60).toString().replace(".0", ""));
            actionMap.put("team_id", action.getTid().toString());
            actionMap.put("type", eventMaps.get(action.getType()));
            actionMap.put("person1", SbdsInternalApis.getPlayerById(action.getPid()).getName());
            lists.add(getElementWithAttributes("time_event", actionMap));
        }
        return lists;
    }

}
