package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.api.common.Sort;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.*;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2017/2/9.
 */
@Service("oneboxAfcclMobStatsService")
public class OneboxAfcclMobStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(OneboxAfcclStatsService.class);
    private static final Map<String,Long> roundIdMap = Constants.roundIdMap;
    private static final Map<String,Long> stageIdMap = Constants.stageIdMap;
    private static final Map<String,Long> groupIdMap = Constants.groupIdMap;
    private static final String CH = "onebox_yaguan";

    //生成给360pc的赛程xml，并且上传到ftp服务器
    public void oneboxAfcclMobStats(Long cid,String fileHead) {
        LOG.info("begin cid: {} " ,cid);
        String fileName = fileHead + Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName,cid);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成xml文件
    public boolean createXmlFile(String file,Long cid){
        try{
            Competition competition = SbdsInternalApis.getCompetitionById(cid);
            if(competition == null){
                LOG.error("competition is  null cid:{} ", cid);
                return false;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{} ", cid);
                return false;
            }

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);


            Element item = getItem(competition, competitionSeason,"","赛程");
            root.addContent(item);

            item = getItem(competition, competitionSeason, "小组赛程","小组赛程");
            root.addContent(item);

            item = getItem(competition, competitionSeason, "积分榜","小组积分榜");
            root.addContent(item);


            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }

    private Element getItem(Competition competition,CompetitionSeason competitionSeason ,String keyStr,String currentTabStr){
        Element item = new Element("item");

        Element key = new Element("key");
        key.addContent(new CDATA(competition.getAbbreviation()+keyStr));
        item.addContent(key);

        Element display = new Element("display");

        Element title = new Element("title");
        title.addContent(new CDATA(competition.getAbbreviation()+competitionSeason.getSeason()+"赛季("+competition.getName()+")赛程赛果_乐视体育"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://afccl.lesports.com/"));
        display.addContent(url);

        Element currenttab = new Element("currenttab");
        currenttab.addContent(currentTabStr);
        display.addContent(currenttab);

        Element tabMatch = getTabMatch(competition.getId(),competitionSeason.getId());
        if(tabMatch != null){
            display.addContent(tabMatch);
        }

        Element tabGroupMatch = getTabGroupMatch(competitionSeason.getId());
        if(tabGroupMatch != null){
            display.addContent(tabGroupMatch);
        }

        Element tabGroupScore = getTabGroupScore(competitionSeason.getId());
        if(tabGroupScore != null){
            display.addContent(tabGroupScore);
        }

        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        display.addContent(source);

        Element more = new Element("more");
        more.addContent(new CDATA("进入"+competition.getAbbreviation()+"专题"));
        display.addContent(more);

        //todo 专题链接
        Element moreUrl = new Element("more_url");
        moreUrl.addContent(new CDATA("http://afccl.lesports.com?ch="+CH));
        display.addContent(moreUrl);

        item.addContent(display);
        return item;
    }

    //添加赛程tabMatch
    private Element getTabMatch(Long cid,Long csid) {

        Element tabMatch = new Element("tab_match");

        Element title = new Element("title");
        title.addContent(new CDATA("赛程"));
        tabMatch.addContent(title);

        Element currentMatches = new Element("currentmatchs");
        currentMatches.addContent(new CDATA(getCurrentMatchesTab(cid,csid)));
        tabMatch.addContent(currentMatches);

        for(int i=1;i<=6;i++){
            Element element1 = getRoundElements("第" + i + "轮", csid);
            if(element1 != null){
                tabMatch.addContent(element1);
            }
        }

        Element element2 = getRoundElements("1/8决赛", csid);
        if (element2 != null){
            tabMatch.addContent(element2);
        }
        Element element3 = getRoundElements("1/4决赛", csid);
        if (element3 != null){
            tabMatch.addContent(element3);
        }
        Element element4 = getRoundElements("半决赛", csid);
        if (element4 != null){
            tabMatch.addContent(element4);
        }
        Element element5 = getRoundElements("三四名决赛", csid);
        if (element5 != null){
            tabMatch.addContent(element5);
        }
        Element element6 = getRoundElements("决赛", csid);
        if (element6 != null){
            tabMatch.addContent(element6);
        }
        return tabMatch;
    }

    private String getCurrentMatchesTab(Long cid,Long csid){
        String currentMatches = "";
        Date today = new Date();
        CallerParam caller = CallerUtils.getDefaultCaller();
        List<TComboEpisode> todayEpisodes = Lists.newArrayList();
        List<TComboEpisode> preEpisodes = Lists.newArrayList();
        List<TComboEpisode> nextEpisodes = Lists.newArrayList();
        Sort sort = new Sort();
        PageParam page = PageUtils.createPageParam(0, 1);

        //当天的比赛
        GetSomeDayEpisodesParam p = new GetSomeDayEpisodesParam();
        p.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
        p.setCids(Lists.newArrayList(cid));
        p.setDate(LeDateUtils.formatYYYYMMDD(today));
        todayEpisodes = SopsApis.getSomeDayEpisodes(p, PageUtils.createPageParam(0, 20), caller);

        //最近的一场未开始的赛程
        GetCurrentEpisodesParam nexParam = new GetCurrentEpisodesParam();
        nexParam.setCsid(csid);
        nexParam.setCids(Lists.newArrayList(cid));
        nexParam.setStartDate(LeDateUtils.formatYYYYMMDD(today));
        nexParam.setLiveShowStatusParam(LiveShowStatusParam.LIVE_NOT_START);

        sort.addToOrders(new Order("start_time", Direction.ASC));
        page.setSort(sort);
        nextEpisodes =  SopsApis.getTimelineEpisodesByCids(nexParam,page , caller);


        //近的一场已结束的赛程
        GetCurrentEpisodesParam endParam = new GetCurrentEpisodesParam();
        endParam.setCsid(csid);
        endParam.setCids(Lists.newArrayList(cid));
        endParam.setStartDate(LeDateUtils.formatYYYYMMDD(today));
        endParam.setLiveShowStatusParam(LiveShowStatusParam.LIVE_END);

        sort = new Sort();
        sort.addToOrders(new Order("start_time", Direction.DESC));
        page = PageUtils.createPageParam(0, 1);
        page.setSort(sort);

        preEpisodes = SopsApis.getTimelineEpisodesByCids(endParam, page, caller);

        Long roundId = 0L;
        TComboEpisode currentEpisode = null;

        //当天有比赛
        if(CollectionUtils.isNotEmpty(todayEpisodes)){
            currentEpisode = todayEpisodes.get(0);
        }
        //当天没有比赛
        else{
            //没有比赛了,最后一天和倒数第二天的比赛
            if(CollectionUtils.isEmpty(nextEpisodes)){
                currentEpisode = preEpisodes.get(0);
            }
            //还没有开打，第一天和第二天的比赛
            else if(CollectionUtils.isEmpty(preEpisodes)){
                currentEpisode = nextEpisodes.get(0);
            }
            else{
                TComboEpisode preEpisode = preEpisodes.get(0);
                TComboEpisode nextEpisode = nextEpisodes.get(0);

                int days1 = XmlUtil.daysBetween(preEpisode.getStartTime().substring(0,8),LeDateUtils.formatYYYYMMDD(today));
                int days2 = XmlUtil.daysBetween(LeDateUtils.formatYYYYMMDD(today),nextEpisode.getStartTime().substring(0,8));

                //距离未开赛的时间更近
                if(days2 <= days1){
                    currentEpisode = nextEpisodes.get(0);
                }
                //距离已结束时间更近
                else{
                    currentEpisode = preEpisodes.get(0);
                }
            }
        }
        if(currentEpisode != null){
            String roundName = "";
            if(roundIdMap.get(currentEpisode.getRound()) != null || stageIdMap.get(currentEpisode.getStage()) != null){
                InternalQuery q = new InternalQuery();
                q.addCriteria(new InternalCriteria("csid", "eq", csid));
                if (roundIdMap.get(currentEpisode.getRound()) != null) {
                    q.addCriteria(new InternalCriteria("round", "eq", roundIdMap.get(currentEpisode.getRound())));
                    roundName = currentEpisode.getRound();
                } else {
                    q.addCriteria(new InternalCriteria("stage", "eq", stageIdMap.get(currentEpisode.getStage())));
                    roundName = currentEpisode.getStage();
                }
                q.addCriteria(new InternalCriteria("deleted", "eq", false));
                q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
                List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
                currentMatches = XmlUtil.getRoundDate(matches,roundName);
            }
        }
        return  currentMatches;
    }

    private Element getTabGroupMatch(Long csid) {
        //添加赛程tab
        Element tabGroupMatchElement = new Element("tab_group_match");

        Element title = new Element("title");
        title.addContent(new CDATA("小组赛程"));
        tabGroupMatchElement.addContent(title);

        tabGroupMatchElement.addContent(getGroupElement("A",csid));
        tabGroupMatchElement.addContent(getGroupElement("B",csid));
        tabGroupMatchElement.addContent(getGroupElement("C",csid));
        tabGroupMatchElement.addContent(getGroupElement("D",csid));
        tabGroupMatchElement.addContent(getGroupElement("E",csid));
        tabGroupMatchElement.addContent(getGroupElement("F",csid));
        tabGroupMatchElement.addContent(getGroupElement("G",csid));
        tabGroupMatchElement.addContent(getGroupElement("H",csid));

        return tabGroupMatchElement;
    }

    //小组赛赛程
    private Element getGroupElement(String groupName,Long csid) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        if (groupIdMap.get(groupName) != null) {
            q.addCriteria(new InternalCriteria("group", "eq", groupIdMap.get(groupName)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        Element tabGroup  = new Element("tab_group");

        Element group = new Element("group");
        group.addContent(new CDATA(groupName));
        tabGroup.addContent(group);

        for(Match match:matches){
            Element teamVs = getTeamVs(match);
            tabGroup.addContent(teamVs);
        }
        return tabGroup;
    }

    //添加小组积分榜
    private Element getTabGroupScore(Long csid) {
        Element tabGroupScore = new Element("tab_group_score");

        Element title = new Element("title");
        title.addContent(new CDATA("小组积分榜"));
        tabGroupScore.addContent(title);

        Element li = new Element("li");
        li.addContent(new CDATA("球队"));
        tabGroupScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("胜"));
        tabGroupScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("平"));
        tabGroupScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("负"));
        tabGroupScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("净胜球"));
        tabGroupScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("积分"));
        tabGroupScore.addContent(li);

        tabGroupScore.addContent(getGroudRank("A",csid));
        tabGroupScore.addContent(getGroudRank("B",csid));
        tabGroupScore.addContent(getGroudRank("C",csid));
        tabGroupScore.addContent(getGroudRank("D",csid));
        tabGroupScore.addContent(getGroudRank("E",csid));
        tabGroupScore.addContent(getGroudRank("F",csid));
        tabGroupScore.addContent(getGroudRank("G",csid));
        tabGroupScore.addContent(getGroudRank("H",csid));


        return tabGroupScore;
    }

    private Element getGroudRank(String groupName,Long csid) {
        Element tabGroup = new Element("tab_group");
        Element group = new Element("group");
        group.addContent(new CDATA(groupName));
        tabGroup.addContent(group);

        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, Constants.TOPLIST_SCORE_ID, groupIdMap.get(groupName));
        if (CollectionUtils.isNotEmpty(topList)) {
            List<TopList.TopListItem> items = topList.get(0).getItems();
            if (CollectionUtils.isEmpty(items)) return null;
            List<Element> tds = Lists.newArrayList();
            int rankNum = 1;
            for (TopList.TopListItem item : items) {
                Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());
                Element onecontent = new Element("list");

                Element rank = new Element("rank");
                rank.addContent(new CDATA(rankNum + ""));
                onecontent.addContent(rank);


                Element teamInfo = new Element("team_info");
                teamInfo.addContent(new CDATA(currentTeam.getName()));
                onecontent.addContent(teamInfo);

                teamInfo = new Element("team_info");
                teamInfo.addContent(new CDATA(item.getStats().get("winMatch").toString()));
                onecontent.addContent(teamInfo);

                teamInfo = new Element("team_info");
                teamInfo.addContent(new CDATA(item.getStats().get("flatMatch").toString()));
                onecontent.addContent(teamInfo);

                teamInfo = new Element("team_info");
                teamInfo.addContent(new CDATA(item.getStats().get("lossMatch").toString()));
                onecontent.addContent(teamInfo);

                Integer goalDif = Integer.valueOf(item.getStats().get("goal").toString()) - Integer.valueOf(item.getStats().get("fumble").toString());

                teamInfo = new Element("team_info");
                teamInfo.addContent(new CDATA(goalDif.toString()));
                onecontent.addContent(teamInfo);

                teamInfo = new Element("team_info");
                teamInfo.addContent(new CDATA(item.getStats().get("teamScore").toString()));
                onecontent.addContent(teamInfo);

                tabGroup.addContent(onecontent);
                rankNum ++;
            }
        }
        return tabGroup;
    }

    //轮次的数据
    private Element getRoundElements(String round,Long csid) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        if (stageIdMap.get(round) != null) {
            q.addCriteria(new InternalCriteria("stage", "eq", stageIdMap.get(round)));
        } else {
            q.addCriteria(new InternalCriteria("round", "eq",roundIdMap .get(round)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        Element matchs = new Element("matchs");
        Element desc = new Element("desc");
        String roundDate = XmlUtil.getRoundDate(matches, round);
        desc.addContent(roundDate);
        matchs.addContent(desc);
        for(Match match:matches){
            Element matchElement = getTeamVs(match);
            if(matchElement != null){
                matchs.addContent(matchElement);
            }
        }
        return matchs;
    }

    //单场比赛的element
    private Element getTeamVs(Match match) {
        Element teamVs = new Element("team_vs");
        Team homeTeam = null;
        Team awayTeam = null;
        String homeScore = "0";
        String awayScore = "0";
        Set<Match.Competitor> teams = match.getCompetitors();
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
        Element team1 = new Element("team1");
        team1.addContent(new CDATA(homeTeam.getName()));
        teamVs.addContent(team1);

        Element team1Icon = new Element("team1_icon");
        team1Icon.addContent(new CDATA(XmlUtil.getLogo(homeTeam.getLogo())));
        teamVs.addContent(team1Icon);

        Element team2 = new Element("team2");
        team2.addContent(new CDATA(awayTeam.getName()));
        teamVs.addContent(team2);

        Element team2Icon = new Element("team2_icon");
        team2Icon.addContent(new CDATA(XmlUtil.getLogo(awayTeam.getLogo())));
        teamVs.addContent(team2Icon);

        Element score = new Element("score");
        score.addContent(new CDATA(XmlUtil.getScore(match.getStatus(),homeScore,awayScore)));
        teamVs.addContent(score);

        Element date = new Element("date");
        date.addContent(new CDATA(XmlUtil.getMatchDate(match.getStartTime())));
        teamVs.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA(XmlUtil.getMatchTime(match.getStartTime())));
        teamVs.addContent(time);

        Element state = new Element("state");
        state.addContent(new CDATA(match.getStatus().getValue()+""));
        teamVs.addContent(state);

        Element url = new Element("url");
        String matchUrl = "";
        if(match.getId() == 1028087003){
            matchUrl = "http://sports.letv.com/match/1028087003.html?ch="+CH+"#live/1020170216191337";
        }
        else if(match.getId() == 1028426003){
            matchUrl = "http://sports.letv.com/match/1028426003.html?ch="+CH+"#live/1020170216185708";
        }
        else{
            matchUrl = String.format(Constants.MATCH_URL,match.getId())+"?ch="+CH;
        }
        url.addContent(new CDATA(matchUrl));
        teamVs.addContent(url);

        return teamVs;
    }


}
