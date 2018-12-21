package com.lesports.sms.cooperation.service.olympic;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/6/29.
 */
@Service("sogouOlyScheduleStatsService")
public class SogouOlyScheduleStatsService {
    private static final Logger LOG = LoggerFactory.getLogger(SogouOlyScheduleStatsService.class);
    //每个文件最多条数
    public static final String TEST = "_test";
    //        public static final String TEST = "";
    //每次读取数据条数
//    public static final String PATH = "E:\\sogou\\";
    public static final String PATH = "/letv/data/hd/alad/";
    //正式或者测试  上传标志
    public static final boolean UPLOAD_FLAG = true;
//    public static final boolean UPLOAD_FLAG = false;

    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM月dd日");
    public  static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:ss");


    //生成给sogou的xml，并且上传到ftp服务器
    public void sogouCslStats() {

        String fileName = "sogou2016csl_mobile"+TEST+".xml";
        String path = PATH;
        Boolean flag = createXmlFile(path+fileName);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, path, "//hd//");
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }



    //生成xml文件
    public boolean createXmlFile(String file){
        try{

            long cid = Constants.CSL_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }

            //当前轮次
            int currentRound = competitionSeason.getCurrentRound();
            long csid = competitionSeason.getId();

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(new CDATA("中超"));
            item.addContent(key);

            Element display = new Element("display");

            Element title = new Element("title");
            title.addContent(new CDATA("中超 2016赛季精彩呈现_乐视体育"));
            display.addContent(title);

            Element url = new Element("url");
            url.addContent(new CDATA("http://m.sports.le.com/csl/schedule.html"));
            display.addContent(url);



            Element tab1  = createTab1Element(currentRound, cid, csid);
            display.addContent(tab1);

            Element tab2  = createTab2Element(currentRound, cid, csid);
            display.addContent(tab2);


            item.addContent(display);
            root.addContent(item);
            File path = new File(file);
            if(!path.exists()){
                XmlUtil.createPath(file);
            }

            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }

    public Element createTab1Element(int currentRound,long cid,long csid) throws Exception{
        Element tab1 = new Element("tab1");
        tab1.setAttribute("current","1");
        tab1.setAttribute("name","比赛");

        for(int i=0;i<3;i++){
            Element day = createRoundElement(currentRound+i,cid,csid);
            tab1.addContent(day);
        }

        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        tab1.addContent(source);

        Element more  = new Element("more");
        more.setAttribute("linkContent","查看所有比赛");
        more .addContent(new CDATA("http://m.sports.le.com/csl/schedule.html"));
        tab1.addContent(more );

        return tab1;
    }

    public Element createTab2Element(int currentRound,long cid,long csid) throws Exception{
        Element tab2 = new Element("tab2");

        //积分榜
        List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORE_ID);
        Element subtab = createScoreToplistElement(scoreTopList.get(0));
        tab2.addContent(subtab);

        //射手榜
        List<TopList> scorerTopList  = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORER_ID);
        subtab = createShotToplistElement(scorerTopList.get(0));
        tab2.addContent(subtab);

        return tab2;
    }

    //生成每个轮次的Element
    public Element createRoundElement(int round,long cid,long csid) throws Exception{
        Element day  = new Element("day");

        String date = "",startDate = "",endDate = "";
        TComboEpisode firstEpisode = null,lastEpisode = null;
        String past = "1";

        PageParam pageParam = new PageParam();
        pageParam.setCount(20);
        pageParam.setPage(0);
        Sort sort = new Sort();
        sort.setOrders(Lists.newArrayList(new Order("start_time", Direction.ASC)));
        pageParam.setSort(sort);

        CallerParam callerParam = new CallerParam();
        callerParam.setCallerId(LeConstants.LESPORTS_PC_CALLER_CODE);
        callerParam.setLanguage(LanguageCode.ZH_CN);
        callerParam.setCountry(CountryCode.CN);

        DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + round + "轮", 100004000);
        if(dictEntry == null){
            LOG.info("dictEntry name:{}, parentId:{}  is null" ,"第" + round + "轮",100004000);
        }
        GetEpisodesOfSeasonByMetaEntryIdParam p = new GetEpisodesOfSeasonByMetaEntryIdParam();
        p.setCid(cid);
        p.setCsid(csid);
        p.setEntryId(dictEntry.getId());
        List<TComboEpisode> episodes = SopsApis.getEpisodesOfSeasonByMetaEntryId(p, pageParam, callerParam);

        firstEpisode = episodes.get(0);
        lastEpisode = episodes.get(episodes.size() -1);

        if(firstEpisode.getMatchStatus().equals(MatchStatus.MATCH_NOT_START)){
            past = "2" ;
        }
        else if(lastEpisode.getMatchStatus().equals(MatchStatus.MATCH_END)){
            past = "0" ;
        }
        startDate = simpleDateFormat.format(LeDateUtils.parseYYYYMMDDHHMMSS(firstEpisode.getStartTime()));
        endDate = simpleDateFormat.format(LeDateUtils.parseYYYYMMDDHHMMSS(lastEpisode.getStartTime()));

        date = "第" + round + "轮("+startDate + "-" + endDate +  ")";
        day.setAttribute("date",date);
        day.setAttribute("past",past);

        for(TComboEpisode episode:episodes){
            LOG.info("tComboEpisode: {} , time: {}", episode.getName(), episode.getStartTime());
            Match match = SbdsInternalApis.getMatchById(episode.getMid());
            Element game = createOneContentElement(match,episode);
            day.addContent(game);
        }

        return day;
    }

    //生成每场比赛的Element
    public Element createOneContentElement(Match match,TComboEpisode episode) throws Exception{
        Element game = new Element("game");

        List<TCompetitor> competitors = episode.getCompetitors();
        Team homeTeam = null,awayTeam = null;
        String homeResult = "0",awayResult = "0";

        for(TCompetitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeTeam = SbdsInternalApis.getTeamById(competitor.getId());
                homeResult = competitor.getFinalResult();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayTeam = SbdsInternalApis.getTeamById(competitor.getId());
                awayResult = competitor.getFinalResult();
            }
        }

        if(homeTeam == null || awayTeam == null) return null;

        Element team1 = new Element("team1");

        Element name = new Element("name");
        name.addContent(new CDATA(homeTeam.getName()));
        team1.addContent(name);

        Element img = new Element("img");
        img.addContent(new CDATA(homeTeam.getLogo()));
        team1.addContent(img);

        Element link = new Element("link");
        link.addContent(new CDATA("http://www.lesports.com/team/"+homeTeam.getId()+".html"));
        team1.addContent(link);

        game.addContent(team1);




        Element status = new Element("status");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            status.setAttribute("statu","0");
        }
        else if(match.getStatus().equals(MatchStatus.MATCHING)){
            status.setAttribute("statu","1");
        }
        else{
            status.setAttribute("statu","2");
        }

        Date startTime = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());
        Element date = new Element("date");
        date.addContent(new CDATA(simpleDateFormat1.format(startTime)));
        status.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA((simpleDateFormat2.format(startTime))));
        status.addContent(time);

        Element score = new Element("score");
        score.addContent(new CDATA(homeResult +"-"+awayResult));
        status.addContent(score);

        Element url = new Element("url");
        url.addContent(new CDATA("http://m.sports.le.com/match/"+match.getId()+".html?ch=baidu_mobile"));
        status.addContent(url);

        game.addContent(status);

        Element team2 = new Element("team2");

        name = new Element("name");
        name.addContent(new CDATA(awayTeam.getName()));
        team2.addContent(name);

        img = new Element("img");
        img.addContent(new CDATA(awayTeam.getLogo()));
        team2.addContent(img);

        link = new Element("link");
        link.addContent(new CDATA("http://www.lesports.com/team/"+awayTeam.getId()+".html"));
        team2.addContent(link);

        game.addContent(team2);

        return game;
    }

    public Format FormatXML(){
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }

    //生成积分排行榜的Element
    public Element createScoreToplistElement(TopList topList) throws Exception{
        Element subtab = new Element("subtab");

        subtab.setAttribute("current","1");
        subtab.setAttribute("name","积分榜");

        Element th = new Element("th");
        th.setAttribute("col1","排名");
        th.setAttribute("col2","球队");
        th.setAttribute("col3","胜/平/负");
        th.setAttribute("col4","积分");
        subtab.addContent(th);


        List<TopList.TopListItem> topListItems = topList.getItems();
        topListItems = topListItems.subList(0,10);
        for(TopList.TopListItem topListItem:topListItems){
            Element onecontent = createScoreItemtElement(topListItem);
            subtab.addContent(onecontent);
        }

        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        subtab.addContent(source);
        Element more = new Element("more");
        more.setAttribute("linkContent","查看更多");
        more.addContent(new CDATA("http://m.sports.le.com/csl/toplist.html"));
        subtab.addContent(more);

        return subtab;
    }

    //生成射手榜的Element
    public Element createShotToplistElement(TopList topList) throws Exception{
        Element subtab = new Element("subtab");

        subtab.setAttribute("current","0");
        subtab.setAttribute("name","射手榜");

        Element th = new Element("th");
        th.setAttribute("col1","排名");
        th.setAttribute("col2","球员");
        th.setAttribute("col3","球队");
        th.setAttribute("col4","进球");
        subtab.addContent(th);


        List<TopList.TopListItem> topListItems = topList.getItems();
        topListItems = topListItems.subList(0,10);
        for(TopList.TopListItem topListItem:topListItems){
            Element onecontent = createShotItemtElement(topListItem);
            subtab.addContent(onecontent);
        }

        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        subtab.addContent(source);
        Element more = new Element("more");
        more.setAttribute("linkContent","查看更多");
        more.addContent(new CDATA("http://m.sports.le.com/csl/toplist.html"));
        subtab.addContent(more);

        return subtab;
    }

    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem) throws Exception{
        Element list = new Element("list");
        Map<String,Object> stats = topListItem.getStats();

        Team team = SbdsInternalApis.getTeamById(topListItem.getCompetitorId());
        Element col1 = new Element("col1");
//        col1.addContent(stats.get("matchNumber").toString());
        col1.addContent(new CDATA(team.getLogo()));
        list.addContent(col1);

        Element col2 = new Element("col2");
        col2.addContent(new CDATA(team.getName()));
        list.addContent(col2);



        String win = stats.get("winMatch").toString();
        String flat = stats.get("flatMatch").toString();
        String loss = stats.get("lossMatch").toString();
        Element col3 = new Element("col3");
        col3.addContent(new CDATA(win+"/"+flat+"/"+loss));
        list.addContent(col3);

        Element col4 = new Element("col4");
        col4.addContent(new CDATA(stats.get("teamScore").toString()));
        list.addContent(col4);

        return list;
    }

    //生成射手榜每个球员的Element
    public Element createShotItemtElement(TopList.TopListItem topListItem) throws Exception{
        Element list = new Element("list");
        Map<String,Object> stats = topListItem.getStats();
        Player player = SbdsInternalApis.getPlayerById(topListItem.getCompetitorId());
        Team team = SbdsInternalApis.getTeamById(topListItem.getTeamId());

        Element col1 = new Element("col1");
//        col1.addContent(stats.get("matchNumber").toString());
        col1.addContent(new CDATA(team.getLogo()));
        list.addContent(col1);

        Element col2 = new Element("col2");
        col2.addContent(new CDATA(player.getName()));
        list.addContent(col2);

        Element col3 = new Element("col3");
        col3.addContent(new CDATA(team.getName()));
        list.addContent(col3);

        Element col4 = new Element("col4");
        col4.addContent(new CDATA(stats.get("goals").toString()));
        list.addContent(col4);

        return list;
    }
}