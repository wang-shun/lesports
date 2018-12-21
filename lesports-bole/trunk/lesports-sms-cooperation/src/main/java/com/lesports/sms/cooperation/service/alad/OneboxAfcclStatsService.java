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
import com.lesports.sms.cooperation.service.copaAmerican.AbstractService;
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
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2017/2/9.
 */
@Service("oneboxAfcclStatsService")
public class OneboxAfcclStatsService{
    private static final Logger LOG = LoggerFactory.getLogger(OneboxAfcclStatsService.class);
    private static final Map<String,Long> roundIdMap = Constants.roundIdMap;
    private static final Map<String,Long> stageIdMap = Constants.stageIdMap;
    private static final Map<Long,String> stageNameMap = Constants.stageNameMap;
    private static final Map<String,Long> groupIdMap = Constants.groupIdMap;
    private static final Map<Long,String> groupNameMap = Constants.groupNameMap;
    private static final String CH = "onebox_yaguan";


    //生成给360pc的赛程xml，并且上传到ftp服务器
    public void oneboxAfcclStats(Long cid,String fileHead) {
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



            Element item = getItem(competition, competitionSeason,"","");
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



        Element tabArea = getTabArea(competition,competitionSeason);

        Element currenttab  = new Element("current_tab");
        if(org.apache.commons.lang3.StringUtils.isBlank(currentTabStr)){
            String tabMatchName = tabArea.getChild("tab_match").getChildTextTrim("name");
            currenttab.addContent(new CDATA(tabMatchName));
        }
        else{
            currenttab.addContent(new CDATA(currentTabStr));
        }
        display.addContent(currenttab);
        display.addContent(tabArea);

        Element showurl = new Element("showurl");
        showurl.addContent("http://www.lesports.com");
        display.addContent(showurl);

        item.addContent(display);
        return item;
    }


    private Element getTabArea(Competition competition,CompetitionSeason competitionSeason){
        Element tabArea = new Element("tab_area");
        Date today = new Date();
        Element currentTabMatch = getCurrentTabMatch(today, competition.getId(), competitionSeason.getId());
        if(currentTabMatch != null){
            tabArea.addContent(currentTabMatch);
        }

        Element nextTabMatch = getNextTabMatch(today, competition.getId(), competitionSeason.getId());
        if(nextTabMatch != null){
            tabArea.addContent(nextTabMatch);
        }
        tabArea.addContent(getTabGroupMatch(competitionSeason.getId()));
        Element stageTabMatch = getTabStageMatch(competitionSeason.getId());
        if(stageTabMatch != null){
            tabArea.addContent(stageTabMatch);
        }
        tabArea.addContent(getTabGroupScore(competitionSeason.getId()));
        return  tabArea;
    }

    //添加当前的 tabMatch
    private Element getCurrentTabMatch(Date today,Long cid,Long csid) {

        CallerParam caller = CallerUtils.getDefaultCaller();
        List<Match> todayMatchs = Lists.newArrayList();
        List<Match> preMatchs = Lists.newArrayList();
        Sort sort = new Sort();
        sort.addToOrders(new Order("start_time", Direction.ASC));
        PageParam page = PageUtils.createPageParam(0,20);

        //当天的比赛
//        GetSomeDayEpisodesParam p = new GetSomeDayEpisodesParam();
//        p.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
//        p.setCids(Lists.newArrayList(cid));
//        p.setDate(LeDateUtils.formatYYYYMMDD(today));
//        todayEpisodes = SopsApis.getSomeDayEpisodes(p, page, caller);


        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("startDate", "eq", LeDateUtils.formatYYYYMMDD(today)));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        todayMatchs = SbdsInternalApis.getMatchsByQuery(q);


        //最近的已结束的赛程
//        GetCurrentEpisodesParam endParam = new GetCurrentEpisodesParam();
//        endParam.setCsid(csid);
//        endParam.setCids(Lists.newArrayList(cid));
//        endParam.setStartDate(LeDateUtils.formatYYYYMMDD(today));
//        endParam.setLiveShowStatusParam(LiveShowStatusParam.LIVE_END);
//        page.setSort(sort);
//        preEpisodes = SopsApis.getTimelineEpisodesByCids(endParam, page, caller);

        InternalQuery q1 = new InternalQuery();
        q1.addCriteria(new InternalCriteria("startDate", "lt", LeDateUtils.formatYYYYMMDD(today)));
        q1.addCriteria(new InternalCriteria("deleted", "eq", false));
        q1.addCriteria(new InternalCriteria("csid", "eq", csid));
        q1.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        preMatchs = SbdsInternalApis.getMatchsByQuery(q1);

        Element tabMatch = new Element("tab_match");

        //今天有比赛
        if(CollectionUtils.isNotEmpty(todayMatchs)){
            String currentDate = todayMatchs.get(0).getStartTime();
            Element name = new Element("name");
            name.addContent(new CDATA(currentDate.substring(4,6)+"月" + currentDate.substring(6,8)+"日"));
            tabMatch.addContent(name);

            tabMatch.addContent(getTitle());
            for(Match match:todayMatchs){
                String groupName = "小组赛";
                if(match.getGroup() != null && groupNameMap.get(match.getGroup())!=null){
                    groupName = groupNameMap.get(match.getGroup())+"组";
                }
                else if(match.getStage() != null && stageNameMap.get(match.getStage())!= null){
                    groupName = stageNameMap.get(match.getStage());
                }
                tabMatch.addContent(getoneContents("0",match,groupName));
            }
        }
        else if(CollectionUtils.isNotEmpty(preMatchs)){

            String preDate = preMatchs.get(preMatchs.size()-1).getStartTime().substring(0,8);
            Element name = new Element("name");
            name.addContent(new CDATA(preDate.substring(4,6)+"月" + preDate.substring(6,8)+"日"));
            tabMatch.addContent(name);

            tabMatch.addContent(getTitle());
            for(Match match:preMatchs){
                if(!preDate.equals(match.getStartTime().substring(0,8)))continue;
                String groupName = "小组赛";
                if(match.getGroup() != null && groupNameMap.get(match.getGroup())!=null){
                    groupName = groupNameMap.get(match.getGroup())+"组";
                }
                else if(match.getStage() != null && stageNameMap.get(match.getStage())!= null){
                    groupName = stageNameMap.get(match.getStage());
                }
                tabMatch.addContent(getoneContents("0",match,groupName));
            }
        }
        else{
            return  null;
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看亚冠全部赛程>>"));
        tabMatch.addContent(moretext);

        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://afccl.lesports.com/schedule/"));
        tabMatch.addContent(moreurl);

        return tabMatch;
    }



    //添加赛程 之后 tabMatch
    private Element getNextTabMatch(Date today,Long cid,Long csid) {
        CallerParam caller = CallerUtils.getDefaultCaller();
        List<Match> nextMatches = Lists.newArrayList();
        Sort sort = new Sort();
        PageParam page = PageUtils.createPageParam(0, 20);


        //最近的一场未开始的赛程
//        GetCurrentEpisodesParam nexParam = new GetCurrentEpisodesParam();
//        nexParam.setCsid(csid);
//        nexParam.setCids(Lists.newArrayList(cid));
//        nexParam.setStartDate(LeDateUtils.formatYYYYMMDD(today));
//        nexParam.setLiveShowStatusParam(LiveShowStatusParam.LIVE_NOT_START);
//
//        sort.addToOrders(new Order("start_time", Direction.ASC));
//        page.setSort(sort);
//        nextEpisodes =  SopsApis.getTimelineEpisodesByCids(nexParam,page , caller);

        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("startDate", "gt", LeDateUtils.formatYYYYMMDD(today)));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        nextMatches = SbdsInternalApis.getMatchsByQuery(q);

        //今天有比赛
        if(CollectionUtils.isNotEmpty(nextMatches)){

            Element tabMatch = new Element("tab_match");
            String  nextDate = nextMatches.get(0).getStartTime().substring(0,8);
            Element name = new Element("name");
            name.addContent(new CDATA(nextDate.substring(4,6)+"月" + nextDate.substring(6,8)+"日"));
            tabMatch.addContent(name);

            tabMatch.addContent(getTitle());
            for(Match match:nextMatches){
                if(!nextDate.equals(match.getStartTime().substring(0,8)))break;
                String groupName = "小组赛";
                if(match.getGroup() != null && groupNameMap.get(match.getGroup())!=null){
                    groupName = groupNameMap.get(match.getGroup())+"组";
                }
                else if(match.getStage() != null && stageNameMap.get(match.getStage())!= null){
                    groupName = stageNameMap.get(match.getStage());
                }
                tabMatch.addContent(getoneContents("0",match,groupName));
            }

            Element moretext = new Element("moretext");
            moretext.addContent(new CDATA("查看亚冠全部赛程>>"));
            tabMatch.addContent(moretext);

            Element moreurl = new Element("moreurl");
            moreurl.addContent(new CDATA("http://afccl.lesports.com/schedule/"));
            tabMatch.addContent(moreurl);

            return tabMatch;
        }

        return null;
    }

    //添加分组赛程
    private Element getTabGroupMatch(Long csid) {
        //添加赛程tab
        Element tabGroupMatchElement = new Element("tab_group_match");

        Element name = new Element("name");
        name.addContent(new CDATA("小组赛程"));
        tabGroupMatchElement.addContent(name);

        Element currentGoup = new Element("current_group");
        currentGoup.addContent(new CDATA("A"));
        tabGroupMatchElement.addContent(currentGoup);

        tabGroupMatchElement.addContent(getGroupElement("A",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("B",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("C",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("D",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("E",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("F",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("G",csid,"0"));
        tabGroupMatchElement.addContent(getGroupElement("H",csid,"0"));

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看亚冠全部赛程>>"));
        tabGroupMatchElement.addContent(moretext);

        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://afccl.lesports.com/schedule/"));
        tabGroupMatchElement.addContent(moreurl);

        return tabGroupMatchElement;
    }

    //添加淘汰赛赛程
    private Element getTabStageMatch(Long csid) {
        //添加赛程tab
        Element tabGroupMatchElement = new Element("tab_group_match");

        Element name = new Element("name");
        name.addContent(new CDATA("淘汰赛赛程"));
        tabGroupMatchElement.addContent(name);

        String currentGroupTab = "";
        Element currentGroup = new Element("current_group");
        Element stage1 = null;
        Element stage2 = null;
        Element stage3 = null;
        Element stage4 = null;
        Element stage5 = null;

        stage1 = getGroupElement("1/8决赛",csid,"1");

        if(stage1 == null){
            return null;
        }


        stage2 = getGroupElement("1/4决赛",csid,"1");
        stage3 = getGroupElement("半决赛",csid,"1");
        stage4 = getGroupElement("三四名决赛",csid,"1");
        stage5 = getGroupElement("决赛",csid,"1");

        if(stage5 != null){
            currentGroupTab = "决赛";
        }
        else if(stage4 != null){
            currentGroupTab = "三四名决赛";
        }
        else if(stage4 != null){
            currentGroupTab = "半决赛";
        }
        else if(stage4 != null){
            currentGroupTab = "1/4决赛";
        }
        else{
            currentGroupTab = "1/8决赛";
        }

        currentGroup.addContent(new CDATA(currentGroupTab));
        tabGroupMatchElement.addContent(currentGroup);

        if(stage1 != null){
            tabGroupMatchElement.addContent(stage1);
        }
        if(stage2 != null){
            tabGroupMatchElement.addContent(stage2);
        }
        if(stage3 != null){
            tabGroupMatchElement.addContent(stage3);
        }
        if(stage4 != null){
            tabGroupMatchElement.addContent(stage4);
        }
        if(stage5 != null){
            tabGroupMatchElement.addContent(stage5);
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看亚冠全部赛程>>"));
        tabGroupMatchElement.addContent(moretext);

        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://afccl.lesports.com/schedule/"));
        tabGroupMatchElement.addContent(moreurl);

        return tabGroupMatchElement;
    }

    //小组赛赛程
    private Element getGroupElement(String groupName,Long csid,String flag) {
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        if (groupIdMap.get(groupName) != null) {
            q.addCriteria(new InternalCriteria("group", "eq", groupIdMap.get(groupName)));
        } else {
            q.addCriteria(new InternalCriteria("stage", "eq", stageIdMap.get(groupName)));
        }
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isEmpty(matches)) return null;
        Element tabGroup = new Element("tab_group");

        Element title = new Element("title");
        title.addContent(new CDATA(groupName));
        tabGroup.addContent(title);


        for(Match match:matches){
            Element oneContents;
            oneContents  = getoneContents(flag,match,groupName);
            tabGroup.addContent(oneContents);
        }
        return tabGroup;
    }

    public Element getTitle(){
        Element title = new Element("title");

        Element li = new Element("li");
        li.addContent("日期");
        title.addContent(li);

        li = new Element("li");
        li.addContent("时间");
        title.addContent(li);

        li = new Element("li");
        li.addContent("对阵");
        title.addContent(li);

        li = new Element("li");
        li.addContent("组别");
        title.addContent(li);

        li = new Element("li");
        li.addContent("赛事观看");
        title.addContent(li);
        return  title;
    }

    //节目生成比赛element
    private Element getoneContents(String flag,TComboEpisode episode) {
        Match match = SbdsInternalApis.getMatchById(episode.getMid());
        Element oneContent = null;
        if(episode.getGroup() != null){
            oneContent = getoneContents("0",match,episode.getGroup());
        }
        else{
            oneContent = getoneContents("1",match,episode.getStage());
        }

        return oneContent;
    }

    //比赛生成element
    private Element getoneContents(String flag, Match match,String groupName) {
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
        Element oneContent = new Element("onecontent");

        Element status = new Element("status");
        status.addContent(new CDATA(match.getStatus().getValue()+""));
        oneContent.addContent(status);

        Element date = new Element("date");
        date.addContent(new CDATA(XmlUtil.getMatchDate(match.getStartTime())));
        oneContent.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA(XmlUtil.getMatchTime(match.getStartTime())));
        oneContent.addContent(time);

        Element teamVs = new Element("team_vs");

        Element team1 = new Element("team1");
        team1.addContent(new CDATA(homeTeam.getName()));
        teamVs.addContent(team1);

        Element team1Url = new Element("team1url");
        if(Constants.afcclMap.get(homeTeam)!=null){
            team1Url.addContent(new CDATA(String.format(Constants.TEAM_URL,homeTeam.getId())+"?ch="+CH));
        }
        else{
            team1Url.addContent(new CDATA(""));
        }

        teamVs.addContent(team1Url);

        Element team1icon = new Element("team1icon");
        team1icon.addContent(new CDATA(XmlUtil.getLogo(homeTeam.getLogo())));
        teamVs.addContent(team1icon);

        Element team2 = new Element("team2");
        team2.addContent(new CDATA(awayTeam.getName()));
        teamVs.addContent(team2);

        Element team2Url = new Element("team2url");
        if(Constants.afcclMap.get(homeTeam)!=null){
            team2Url.addContent(new CDATA(String.format(Constants.TEAM_URL,awayTeam.getId())+"?ch="+CH));
        }
        else{
            team2Url.addContent(new CDATA(""));
        }
        teamVs.addContent(team2Url);

        Element team2icon = new Element("team2icon");
        team2icon.addContent(new CDATA(XmlUtil.getLogo(awayTeam.getLogo())));
        teamVs.addContent(team2icon);

        Element score = new Element("score");
        score.addContent(XmlUtil.getScore(match.getStatus(), homeScore, awayScore));
        teamVs.addContent(score);

        oneContent.addContent(teamVs);

        Element group = new Element("group");
        if (flag.equals("1")) {
            group.addContent(new CDATA("淘汰赛"));
        } else {
            group.addContent(new CDATA(groupName));
        }
        oneContent.addContent(group);

        Element link = new Element("link");
        Element text = new Element("text");
        text.addContent(new CDATA(match.getStatus().toString().equals("MATCH_END") ? "视频集锦" : "视频直播"));
        link.addContent(text);
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
        link.addContent(url);
        oneContent.addContent(link);

        return oneContent;
    }

    //添加小组积分榜
    private Element getTabGroupScore(Long csid) {
        Element tabGroupScore = new Element("tab_group_score");

        Element name = new Element("name");
        name.addContent(new CDATA("小组积分榜"));
        tabGroupScore.addContent(name);

        Element title = new Element("title");

        Element li = new Element("li");
        li.addContent(new CDATA("排名"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("球队"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("场次"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("胜"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("平"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("负"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("净胜球"));
        title.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("积分"));
        title.addContent(li);

        tabGroupScore.addContent(title);

        tabGroupScore.addContent(getGroudRank("A",csid));
        tabGroupScore.addContent(getGroudRank("B",csid));
        tabGroupScore.addContent(getGroudRank("C",csid));
        tabGroupScore.addContent(getGroudRank("D",csid));
        tabGroupScore.addContent(getGroudRank("E",csid));
        tabGroupScore.addContent(getGroudRank("F",csid));
        tabGroupScore.addContent(getGroudRank("G",csid));
        tabGroupScore.addContent(getGroudRank("H",csid));

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看亚冠全部积分榜>>"));
        tabGroupScore.addContent(moretext);

        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://afccl.lesports.com/table"));
        tabGroupScore.addContent(moreurl);

        return tabGroupScore;
    }

    private Element getGroudRank(String groupName,Long csid) {
        Element groupScore = new Element("group_score");
        Element title = new Element("title");
        title.addContent(new CDATA(groupName));
        groupScore.addContent(title);

        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, Constants.TOPLIST_SCORE_ID, groupIdMap.get(groupName));
        if (CollectionUtils.isNotEmpty(topList)) {
            List<TopList.TopListItem> items = topList.get(0).getItems();
            if (CollectionUtils.isEmpty(items)) return null;
            int rank = 1;
            for (TopList.TopListItem item : items) {
                Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());
                Element onecontent = new Element("onecontent");

                Element li = new Element("li");
                Element text = new Element("text");
                text.addContent(new CDATA(rank+""));
                li.addContent(text);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                text.addContent(new CDATA(currentTeam.getName()));
                li.addContent(text);
                Element url = new Element("url");
                if(Constants.afcclMap.get(currentTeam.getId())!=null){
                    url.addContent(new CDATA(String.format(Constants.TEAM_URL,currentTeam.getId())+"?ch="+CH));
                }
                else{
                    url.addContent(new CDATA(""));
                }
                li.addContent(url);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                text.addContent(new CDATA(item.getStats().get("matchNumber").toString()));
                li.addContent(text);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                text.addContent(new CDATA(item.getStats().get("winMatch").toString()));
                li.addContent(text);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                text.addContent(new CDATA(item.getStats().get("flatMatch").toString()));
                li.addContent(text);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                text.addContent(new CDATA(item.getStats().get("lossMatch").toString()));
                li.addContent(text);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                Integer goalDif = Integer.valueOf(item.getStats().get("goal").toString()) - Integer.valueOf(item.getStats().get("fumble").toString());
                text.addContent(new CDATA(goalDif+""));
                li.addContent(text);
                onecontent.addContent(li);

                li = new Element("li");
                text = new Element("text");
                text.addContent(new CDATA(item.getStats().get("teamScore").toString()));
                li.addContent(text);
                onecontent.addContent(li);

                groupScore.addContent(onecontent);
                rank ++;
            }
        }
        return groupScore;
    }
}
