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
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduTop12SimScheduleService")
public class Baidu_mobileSimplescheduleGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_mobileSimplescheduleGenerator.class);

    public String getFilePath() {
        String filePath = "scheduleandresult_mobile_simple_WordCupTop122018.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        root.addContent(getItemElement());
        return new Document(root);
    }

    private Element getItemElement() {
        Element item = createRooElement("item");//一级目录
        item.addContent(createElement("key", "世预亚12强"));
        item.addContent(getDisplayElement());
        return item;
    }

    private Element getDisplayElement() {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", resourceUrl + baidu_logo));
        displayElement.addContent(createElement("title", "世预亚12强中国队赛程_乐视体育"));
        displayElement.addContent(createElement("highlight", "世界杯"));
        displayElement.addContent(createElement("curtab", "12强赛"));
        displayElement.addContent(createElement("cur_index", getCurIndex()));
        Element gametab = createRooElement("gametab");
        updateElement(gametab, getRecentTabs());
        displayElement.addContent(gametab);
        Element games = createRooElement("games");
        updateElement(games, getTabs());
        displayElement.addContent(games);
//        Element morelinks = createRooElement("morelink");
//        Element list = createRooElement("list");
//        list.addContent(createElement("linktitle", "小组积分榜"));
//        list.addContent(createElement("linkurl", teamRankingUrl + baidu_logo));
//        morelinks.addContent(list);
//        displayElement.addContent(morelinks);
        displayElement.addContent(createElement("provider", "乐视体育"));
        displayElement.addContent(createElement("bottomright_text", "更多赛程"));
        displayElement.addContent(createElement("bottomright_url", matchesUrl + baidu_logo));
        return displayElement;
    }

    private List<Element> getRecentTabs() {
        List<Element> lists = Lists.newArrayList();
        for (String date : getValidDatas()) {
            Element tab0 = createElement("tab", getMatchDate(date));
            lists.add(tab0);
        }
        return lists;
    }


    private List<Element> getTabs() {
        List<Element> lists = Lists.newArrayList();
        for (String date : getValidDatas()) {
            Element tab0 = createRooElement("tab");
            tab0.addContent(getVilistElements(date));
            lists.add(tab0);
        }
        return lists;
    }

    //赛程列表
    private List<Element> getVilistElements(String validDate) {
        List<Element> lists = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("competitors.competitor_id", "is", 1440006));
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.addCriteria(new InternalCriteria("start_date", "eq", validDate));
        q.with(new Sort(Lists.newArrayList(new Sort.Order(Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isNotEmpty(matches)) {
            for (Match currentMatch : matches) {
                Element vsListElement = createRooElement("list");
                vsListElement.addContent(createElement("gameid", currentMatch.getId().toString()));
                vsListElement.addContent(createElement("date", getMatchDate(currentMatch.getStartDate())));
                vsListElement.addContent(createElement("time", getMatchTime(currentMatch.getStartTime())));
                vsListElement.addContent(createElement("state", getmatchStatus(currentMatch.getStatus())));
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
                Map<String, Object> attributesHome = Maps.newHashMap();
                attributesHome.put("name", homeTeam.getName());
                attributesHome.put("logo",  Constants.top12LogoUrlMap.get(homeTeam.getId()));
                attributesHome.put("score", homeScore);
                attributesHome.put("id", homeTeam.getId().toString());
                Element homeTeamElement = getElementWithAttributes("teama", attributesHome);
                Map<String, Object> attributesAway = Maps.newHashMap();
                attributesAway.put("name", awayTeam.getName());
                attributesAway.put("logo",  Constants.top12LogoUrlMap.get(awayTeam.getId()));
                attributesAway.put("score", awayScore);
                attributesAway.put("id", awayTeam.getId().toString());
                Element awayTeamElement = getElementWithAttributes("teamb", attributesAway);
                vsListElement.addContent(homeTeamElement);
                vsListElement.addContent(awayTeamElement);
                vsListElement.addContent(createElement("game_round", getMatchRound(currentMatch.getGroup(), currentMatch.getRound())));
                vsListElement.addContent(createElement("tab_name", getMatchDate(validDate)));
                vsListElement.addContent(createElement("matchurl", matchPageUrl("", currentMatch.getId(), baidu_logo)));
                lists.add(vsListElement);
            }
        }
        return lists;
    }

    public String getMatchDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "月" + day + "日";
    }

    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }

    public String getMatchRound(Long groupId, Long roundId) {
        DictEntry group = SbdsInternalApis.getDictById(groupId);
        DictEntry round = SbdsInternalApis.getDictById(roundId);
        if (group == null && round == null) return "";
        else if (round == null) return group.getName();
        else if (group == null) return round.getName();
        else return group.getName() + " " + round.getName();
    }

    public List<String> getValidDatas() {
        List<String> validDates = Lists.newArrayList();
        List<Long> round = Lists.newArrayList();
        List<String> nums = Lists.newArrayList();
        CompetitionSeason currentComp = SbdsInternalApis.getCompetitionSeasonById(csid);
        Integer roundNum = currentComp.getCurrentRound()==null?1:currentComp.getCurrentRound();
        if (roundNum == 1) {
            nums.add("第" + 1 + "轮");
            nums.add("第" + 2 + "轮");
            nums.add("第" + 3 + "轮");
        } else if (roundNum == currentComp.getTotalRound()) {
            nums.add("第" + (currentComp.getTotalRound() - 2) + "轮");
            nums.add("第" + (currentComp.getTotalRound() - 1) + "轮");
            nums.add("第" + currentComp.getTotalRound() + "轮");
        } else {
            nums.add("第" + (roundNum - 1) + "轮");
            nums.add("第" + roundNum + "轮");
            nums.add("第" + (roundNum + 1) + "轮");
        }
        for (String roundNames : nums) {
            List<DictEntry> currentDict = SbdsInternalApis.getDictEntriesByName(roundNames);
            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("competitors.competitor_id", "is", 1440006));
            q.addCriteria(new InternalCriteria("csid", "eq", csid));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.addCriteria(new InternalCriteria("round", "eq", currentDict.get(0).getId()));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
            validDates.add(matches.get(0).getStartDate());
        }
        return validDates;

    }

    public String getCurIndex() {
        CompetitionSeason currentComp = SbdsInternalApis.getCompetitionSeasonById(csid);
        Integer roundNum = currentComp.getCurrentRound()==null?1:currentComp.getCurrentRound();
        if (roundNum==null||roundNum == 1) {
            return "0";
        } else if (roundNum == currentComp.getTotalRound()) {
            return "2";
        } else {
            return "1";
        }
    }
}
