package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.CommonUtil;
import com.lesports.sms.model.DictEntry;
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
@Service("BaiduSquadService")
public class Baidu_squadsGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_squadsGenerator.class);

    public String getFilePath() {
        String filePath = "squads_mobile_copaAmerican2016.xml";
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
            if (getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore) != null) {
                Element item = createRooElement("item");
                item.addContent(createElement("key", homeTeam.getName() + awayTeam.getName()));
                item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam, homeScore, awayScore));
                items.add(item);
            }
        }
        return items;
    }


    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam, String homeScore, String awayScore) {
        List<Match.Squad> squads = match.getSquads();
        if (CollectionUtils.isNotEmpty(squads)) return null;
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchPageUrl("",match.getId(),baidu_logo)));
        Match.Squad homeSquad = null;
        Match.Squad awaySquad = null;
        for (Match.Squad squad : squads) {
            if (squad.getTid().equals(homeTeam.getId())) homeSquad = squad;
            else awaySquad = squad;
        }
        if (homeSquad == null || awaySquad == null) return null;
        Map team1Map = Maps.newHashMap();
        team1Map.put("name", homeTeam.getName());
        team1Map.put("logo", logoMaps.get(homeTeam.getId()));
        team1Map.put("formation", homeSquad == null || homeSquad.getFormation() == null ? "" : homeSquad.getFormation());
        Map team2Map = Maps.newHashMap();
        team2Map.put("name", awayTeam.getName());
        team2Map.put("logo", logoMaps.get(awayTeam.getId()));
        team2Map.put("formation", awaySquad == null || awaySquad.getFormation() == null ? "" : awaySquad.getFormation());
        displayElement.addContent(getElementWithAttributes("teamA", team1Map));
        displayElement.addContent(getElementWithAttributes("teamB", team2Map));
        displayElement.addContent(getTeamFirstPlayersList("teamA_list", homeSquad));
        displayElement.addContent(getTeamFirstPlayersList("teamB_list", awaySquad));
        displayElement.addContent(getTeamSubPlayersList("teamA_bench", homeSquad));
        displayElement.addContent(getTeamSubPlayersList("teamB_bench", awaySquad));
        return displayElement;
    }


    //添加shoufan
    private Element getTeamFirstPlayersList(String elementname, Match.Squad squad) {
        if (squad == null || CollectionUtils.isEmpty(squad.getPlayers())) return null;
        Element team = createRooElement(elementname);
        List<Element> playerList = Lists.newArrayList();
        for (Match.SimplePlayer simplePlayer : squad.getPlayers()) {
            if (!simplePlayer.getStarting()) continue;
            Map playerMap = Maps.newHashMap();
            playerMap.put("num", simplePlayer.getSquadOrder() == null ? "" : simplePlayer.getSquadOrder());
            playerMap.put("name", SbdsInternalApis.getPlayerById(simplePlayer.getPid()).getName());
            DictEntry dictEntry = SbdsInternalApis.getDictById(simplePlayer.getPosition());
            String position = dictEntry == null ? "候补" : dictEntry.getName();
            playerMap.put("position", position);
            playerList.add(getElementWithAttributes("list", playerMap));
        }
        updateElement(team, playerList);
        return team;
    }

    //添加shoufan
    private Element getTeamSubPlayersList(String elementname, Match.Squad squad) {
        if (squad == null || CollectionUtils.isEmpty(squad.getPlayers())) return null;
        Element team = createRooElement(elementname);
        List<Element> playerList = Lists.newArrayList();
        for (Match.SimplePlayer simplePlayer : squad.getPlayers()) {
            if (simplePlayer.getStarting()) continue;
            Map playerMap = Maps.newHashMap();
            playerMap.put("num", simplePlayer.getSquadOrder() == null ? "" : simplePlayer.getSquadOrder());
            playerMap.put("name", SbdsInternalApis.getPlayerById(simplePlayer.getPid()).getName());
            DictEntry dictEntry = SbdsInternalApis.getDictById(simplePlayer.getPosition());
            String position = dictEntry == null ? "候补" : dictEntry.getName();
            playerMap.put("position", position);
            playerList.add(getElementWithAttributes("list", playerMap));
        }
        updateElement(team, playerList);
        return team;
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
