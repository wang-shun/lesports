package com.lesports.sms.cooperation.service.Top12;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.api.common.Sort;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/8/27.
 */
@Service("top12OneboxPcService")
public class Top12OneboxPcService  {


    private static final Logger LOG = LoggerFactory.getLogger(Top12OneboxPcService.class);


    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
    private static final String TOP12_CH = "360_12";


    //生成给360的赛程xml，并且上传到ftp服务器
    public void oneboxTop12Stats() {
        String fileName = "onebox2016Top12"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成xml文件
    public boolean createXmlFile(String file){
        try{
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.TOP12_COMPETITION_ID);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", Constants.TOP12_COMPETITION_ID);
                return false;
            }

            //当前轮次
            int round = competitionSeason.getCurrentRound();
            long csid = competitionSeason.getId();



            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = createItemElement("世预赛","第"+round+"轮",round,Constants.TOP12_COMPETITION_ID,csid);
            root.addContent(item);

            item = createItemElement("世预赛积分榜","积分榜",round,Constants.TOP12_COMPETITION_ID,csid);
            root.addContent(item);

            item = createItemElement("世预赛射手榜","射手榜",round,Constants.TOP12_COMPETITION_ID,csid);
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

    public Element createItemElement(String keyStr,String current_tab,int round,long cid,long csid) throws Exception{
        Element item = new Element("item");


        Element key = new Element("key");
        key.addContent(new CDATA(keyStr));
        item.addContent(key);

        Element display = new Element("display");
        item.addContent(display);

        Element title = new Element("title");
        title.addContent(new CDATA("2018世界杯亚洲区预选赛_乐视体育"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://12.lesports.com/"));
        display.addContent(url);


        Element currentTab = new Element("current_tab");
        currentTab.addContent(new CDATA(current_tab));
        display.addContent(currentTab);

        Element tabArea = new Element("tab_area");
        //当前轮次
        Element tabMatch = createRoundElement(round,cid,csid);
        tabArea.addContent(tabMatch);

        //下一轮次
        if(round<10){
            Element nextTabMatch = createRoundElement(round+1,cid,csid);
            tabArea.addContent(nextTabMatch);
        }

        //积分榜
        List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORE_ID);
        if(CollectionUtils.isNotEmpty(scoreTopList)){
            for(TopList topList:scoreTopList){
                if(topList.getGroup() != 100023000L) continue;
                Element tabScore = createScoreToplistElement(topList);
                tabArea.addContent(tabScore);
            }
        }



        //射手榜
        List<TopList> scorerTopList  = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORER_ID);
        if(CollectionUtils.isNotEmpty(scorerTopList)){
            Element tabScorer = createScorerToplistElement(scorerTopList.get(0));
            tabArea.addContent(tabScorer);
        }
        else{
            Element tabScorer = createScorerToplistElement(null);
            tabArea.addContent(tabScorer);
        }



        display.addContent(tabArea);
        Element showUrl = new Element("showurl");
        showUrl.addContent(new CDATA("12.lesports.com"));
        display.addContent(showUrl);
        return item;
    }

    //生成每个轮次的Element
    public Element createRoundElement(int round,long cid,long csid) throws Exception{
        Element tabMatch = new Element("tab_match");

        Element name = new Element("name");
        name.addContent(new CDATA("第"+round+"轮"));
        tabMatch.addContent(name);

        Element title = new Element("title");
        Element li = new Element("li");
        li.addContent(new CDATA("状态"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("比赛日期"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("时间"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("对阵"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("赛事观看"));
        title.addContent(li);
        tabMatch.addContent(title);


        DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + round + "轮", 100004000);
        if(dictEntry == null){
            LOG.info("dictEntry name:{}, parentId:{}  is null" ,"第" + round + "轮",100004000);
        }

        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("round", "eq", dictEntry.getId()));
//        q.addCriteria(new InternalCriteria("competitors.competitor_id", "eq", Constants.CHINA_FOOTBALL_TEAM_ID));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.ASC, "start_time"));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);

        List<Match> chinaList = Lists.newArrayList();
        List<Match> otherList = Lists.newArrayList();
        for(Match match:matches){
            boolean chinaFlag = false;
            for(Match.Competitor competitor:match.getCompetitors()){
                if(competitor.getCompetitorId()==1440006L){
                    chinaFlag = true;
                    break;
                }
            }
            if(chinaFlag){
                chinaList.add(match);
            }
            else{
                otherList.add(match);
            }
        }
        chinaList.addAll(otherList);

        for(Match match:chinaList){
            Element oneconten = createOneContentElement(match);
            tabMatch.addContent(oneconten);
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看世预赛全部赛程》"));
        tabMatch.addContent(moretext);
        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://12.lesports.com/schedule?ch="+TOP12_CH));
        tabMatch.addContent(moreurl);

        return tabMatch;
    }

    //生成每场比赛的Element
    public Element createOneContentElement(Match match) throws Exception{
        Element onecontent = new Element("onecontent");
        Element status = new Element("status");
        status.addContent(new CDATA(match.getStatus().getValue()+""));
        onecontent.addContent(status);

        Date startTime = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());
        Element date = new Element("date");

        String weekNick = XmlUtil.getWeekNick(startTime);

        date.addContent(new CDATA(simpleDateFormat.format(startTime)+weekNick));
        onecontent.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA(simpleDateFormat1.format(startTime)));
        onecontent.addContent(time);

        Element teamVs = new Element("team_vs");
        Set<Match.Competitor> competitors = match.getCompetitors();

        String homeResult = "";
        String awayResult = "";
        Team homeTeam = null;
        Team awayTeam = null;

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeResult = competitor.getFinalResult();
                homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayResult = competitor.getFinalResult();
                awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
            }
        }
        if(homeTeam == null || awayTeam == null){
            return null;
        }
        Element team1 = new Element("team1");
        team1.addContent(new CDATA(homeTeam.getName()));
        teamVs.addContent(team1);
        Element team1url = new Element("team1url");
        team1url.addContent(new CDATA(""));
//        team1url.addContent(new CDATA("http://www.lesports.com/team/"+homeTeam.getId()+".html?ch="+TOP12_CH));
        teamVs.addContent(team1url);

        Element team2 = new Element("team2");
        team2.addContent(new CDATA(awayTeam.getName()));
        teamVs.addContent(team2);
        Element team2url = new Element("team2url");
        team2url.addContent(new CDATA(""));
//        team2url.addContent(new CDATA("http://www.lesports.com/team/"+awayTeam.getId()+".html?ch="+TOP12_CH));
        teamVs.addContent(team2url);

        Element score = new Element("score");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            score.addContent(new CDATA(homeResult+"-"+awayResult));
        }
        else{
            score.addContent(new CDATA("VS"));
        }
        teamVs.addContent(score);
        onecontent.addContent(teamVs);
        Element link = new Element("link");
        Element text = new Element("text");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            text.addContent(new CDATA("视频回放"));
        }
        else{
            text.addContent(new CDATA("视频直播"));
        }
        link.addContent(text);
        Element url = new Element("url");
        url.addContent(new CDATA("http://sports.le.com/match/"+match.getId()+".html?ch="+TOP12_CH));
        link.addContent(url);
        onecontent.addContent(link);

        return onecontent;
    }


    //生成积分排行榜的Element
    public Element createScoreToplistElement(TopList topList) throws Exception{
        LOG.info("topList: {} ",topList);
        Element tabScore = new Element("tab_score");

        Element name = new Element("name");
        name.addContent(new CDATA("积分榜"));
        tabScore.addContent(name);

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
        tabScore.addContent(title);

        if(topList != null){
            LOG.info("topList: {}" ,topList.getId());
            List<TopList.TopListItem> topListItems = topList.getItems();
            int size = topListItems.size();
            if(size>10)size = 10;
            LOG.info("topList size: {}" ,size);
            topListItems = topListItems.subList(0,size);
            for(TopList.TopListItem topListItem:topListItems){
                LOG.info("topListItem: {}" ,topListItem);
                Element onecontent = createScoreItemtElement(topListItem);
                tabScore.addContent(onecontent);
            }
        }



        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看完整积分榜》"));
        tabScore.addContent(moretext);
        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://12.lesports.com/standings?ch="+TOP12_CH));
        tabScore.addContent(moreurl);

        return tabScore;
    }

    //生成射手榜的Element
    public Element createScorerToplistElement(TopList topList) throws Exception{
        Element tabScorer = new Element("tab_scorer");

        Element name = new Element("name");
        name.addContent(new CDATA("射手榜"));
        tabScorer.addContent(name);

        Element title = new Element("title");
        Element li = new Element("li");
        li.addContent(new CDATA("排名"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("球员"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("球队"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("进球"));
        title.addContent(li);
        tabScorer.addContent(title);

        if(topList != null){
            List<TopList.TopListItem> topListItems = topList.getItems();
            int size = topListItems.size();
            if(size>10)size = 10;
            topListItems = topListItems.subList(0,size);
            for(TopList.TopListItem topListItem:topListItems){
                Element onecontent = createScorerItemtElement(topListItem);
                tabScorer.addContent(onecontent);
            }
        }
        else{
            Map<String,Object> stats = Maps.newHashMap();
            stats.put("goals","0");
            TopList.TopListItem topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106841007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(1);
            topListItem.setShowOrder(1);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            Element onecontent = createScorerItemtElement(topListItem);
            tabScorer.addContent(onecontent);

            stats.put("goals","0");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106775007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(2);
            topListItem.setShowOrder(2);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createScorerItemtElement(topListItem);
            tabScorer.addContent(onecontent);

            stats.put("goals","0");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106822007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(3);
            topListItem.setShowOrder(3);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createScorerItemtElement(topListItem);
            tabScorer.addContent(onecontent);

            stats.put("goals","0");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106719007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(4);
            topListItem.setShowOrder(4);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createScorerItemtElement(topListItem);
            tabScorer.addContent(onecontent);

            stats.put("goals","0");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106772007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(5);
            topListItem.setShowOrder(5);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createScorerItemtElement(topListItem);
            tabScorer.addContent(onecontent);
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看完整射手榜》"));
        tabScorer.addContent(moretext);
        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://12.lesports.com/scorer?ch="+TOP12_CH));
        tabScorer.addContent(moreurl);

        return tabScorer;
    }

    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem) throws Exception{

        Element onecontent = new Element("onecontent");
        Map<String,Object> stats = topListItem.getStats();
        Element li = new Element("li");
        Element text = new Element("text");
        text.addContent(new CDATA(topListItem.getRank()+""));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        Team team = SbdsInternalApis.getTeamById(topListItem.getCompetitorId());
        text = new Element("text");
        text.addContent(new CDATA(team.getName()));
        li.addContent(text);
        Element url = new Element("url");
        url.addContent(new CDATA(("")));
//        url.addContent(new CDATA(("http://www.lesports.com/team/"+team.getId()+".html?ch="+TOP12_CH)));
        li.addContent(url);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("matchNumber").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("winMatch").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("flatMatch").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("lossMatch").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("goalDiffer").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("teamScore").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        return onecontent;
    }

    //生成射手榜每个球员的Element
    public Element createScorerItemtElement(TopList.TopListItem topListItem) throws Exception{
        Element onecontent = new Element("onecontent");
        Map<String,Object> stats = topListItem.getStats();
        Element li = new Element("li");
        Element text = new Element("text");
        text.addContent(new CDATA(topListItem.getRank()+""));
        li.addContent(text);
        onecontent.addContent(li);
        li = new Element("li");
        Player player = SbdsInternalApis.getPlayerById(topListItem.getCompetitorId());
        text = new Element("text");
        text.addContent(new CDATA(player.getName()));
        li.addContent(text);
        Element url = new Element("url");
        url.addContent(new CDATA(("http://www.lesports.com/player/"+player.getId()+".html?ch="+TOP12_CH)));
        li.addContent(url);
        onecontent.addContent(li);
        Team team = SbdsInternalApis.getTeamById(topListItem.getTeamId());
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(team.getName()));
        li.addContent(text);
        url = new Element("url");

        url.addContent(new CDATA(("")));
//        url.addContent(new CDATA(("http://www.lesports.com/team/"+team.getId()+".html?ch="+TOP12_CH)));
        li.addContent(url);
        onecontent.addContent(li);
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(stats.get("goals").toString()));
        li.addContent(text);
        onecontent.addContent(li);
        return onecontent;
    }

}
