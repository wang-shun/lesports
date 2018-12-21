package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.corpus.util.StringUtils;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduLiveService")
public class Baidu_LiveGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_LiveGenerator.class);

    public String getFilePath() {
        String filePath = "livescore_mobile_copaAmerican2016.xml";
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
            item.addContent(getDisplayElement(currentMatch, homeTeam, awayTeam));
            items.add(item);
        }
        return items;
    }

    private Element getDisplayElement(Match match, Team homeTeam, Team awayTeam) {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", matchPageUrl("",match.getId(),baidu_logo)));
        displayElement.addContent(createElement("tab_name", "直播"));
        displayElement.addContent(getTabs(match.getId()));
        return displayElement;
    }


    //添加tab
    private Element getTabs(Long matchId) {
        Element tabsElement = createRooElement("tabs");
        tabsElement.addContent(createElement("tabstitle", "视频直播"));
        Map vedioMap = Maps.newHashMap();
        vedioMap.put("icon", "http://i1.letvimg.com/lc04_iscms/201605/27/18/14/37b8e393fe894efbad0defabb25cce31.png");
        vedioMap.put("text", "标清免费");
        vedioMap.put("link", matchPageUrl("",matchId,baidu_logo));
        tabsElement.addContent(getElementWithAttributes("video", vedioMap));
        return tabsElement;


    }

}


