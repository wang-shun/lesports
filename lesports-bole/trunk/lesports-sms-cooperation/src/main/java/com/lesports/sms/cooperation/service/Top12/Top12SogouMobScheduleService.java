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
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
 * Created by zhonglin on 2016/8/23.
 */
@Service("top12SogouMobScheduleService")
public class Top12SogouMobScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(Top12SogouMobScheduleService.class);

    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM月dd日");
    public  static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
    private static final String TOP12_CH = "sogou_12";


    //生成给sogou的xml，并且上传到ftp服务器
    public void sogouTop12Stats() {

        String fileName = "sogou2016top12_mobile"+ Constants.fileextraname+".xml";
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

            long cid = Constants.TOP12_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }

            //当前轮次
            long csid = competitionSeason.getId();

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(new CDATA("世预赛"));
            item.addContent(key);

            Element display = new Element("display");

            Element title = new Element("title");
            title.addContent(new CDATA("2018世预赛亚洲区中国赛程"));
            display.addContent(title);

            Element url = new Element("url");
            url.addContent(new CDATA("http://m.lesports.com/12?ch="+TOP12_CH));
            display.addContent(url);



            Element tab1  = createTab1Element(csid);
            display.addContent(tab1);

            Element tab2  = createTab2Element(csid);
            display.addContent(tab2);


            item.addContent(display);
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

    public Element createTab1Element(Long csid) throws Exception{
        Element tab1 = new Element("tab1");
        tab1.setAttribute("current","1");
        tab1.setAttribute("name","比赛");
        List<Match> matches1 = Lists.newArrayList();
        List<Match> matches2 = Lists.newArrayList();

        LOG.info("csid: {} ,competitor_id: {} ",csid,Constants.CHINA_FOOTBALL_TEAM_ID);

        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", csid));
        q.addCriteria(new InternalCriteria("competitors.competitor_id", "eq", Constants.CHINA_FOOTBALL_TEAM_ID));
//        q.addCriteria(new InternalCriteria("competitors.competitor_id", "eq", 3052006L));
        q.addCriteria(new InternalCriteria("deleted", "eq", false));
        q.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.ASC, "start_time"));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);

        for(Match match:matches){
            if(match.getStartDate().startsWith("2016")){
                matches1.add(match);
            }
            else{
                matches2.add(match);
            }
        }


        if(CollectionUtils.isNotEmpty(matches1)){
            Element day = createRoundElement("2016",matches1);
            if(day != null){
                tab1.addContent(day);
            }
        }

        if(CollectionUtils.isNotEmpty(matches2)){
            Element day = createRoundElement("2017",matches2);
            if(day != null){
                tab1.addContent(day);
            }
        }


        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        tab1.addContent(source);

        Element more  = new Element("more");
        more.setAttribute("linkContent","查看更多");
        more .addContent(new CDATA("http://m.lesports.com/12/schedule?ch="+TOP12_CH));
        tab1.addContent(more );

        return tab1;
    }

    //生成每个轮次的Element
    public Element createRoundElement(String  year,List<Match> matches) throws Exception{
        Element day  = new Element("day");
        String past = "0";

        if(LeDateUtils.formatYYYY(new Date()).equals(year)){
            past = "1";
        }

        String startDate = simpleDateFormat.format(LeDateUtils.parseYYYYMMDDHHMMSS(matches.get(0).getStartTime()));
        String endDate = simpleDateFormat.format(LeDateUtils.parseYYYYMMDDHHMMSS(matches.get(matches.size()-1).getStartTime()));
        String date = year + "年("+startDate+"-"+endDate+")";
        day.setAttribute("date",date);
        day.setAttribute("past",past);

        for(Match match:matches){
            Element game = createOneContentElement(match);
            day.addContent(game);
        }

        return day;
    }

    public Element createTab2Element(long csid) throws Exception{
        Element tab2 = new Element("tab2");
        tab2.setAttribute("current","0");
        tab2.setAttribute("name","排行");

        //积分榜
        List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORE_ID);
        if(CollectionUtils.isNotEmpty(scoreTopList)){
            for(TopList topList:scoreTopList){
                if(topList.getGroup() != 100023000L) continue;
                Element subtab = createScoreToplistElement(topList);
                tab2.addContent(subtab);
            }
        }



        //射手榜
        List<TopList> scorerTopList  = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORER_ID);
        if(CollectionUtils.isNotEmpty(scorerTopList)){
            Element subtab = createShotToplistElement(scorerTopList.get(0));
            tab2.addContent(subtab);
        }
        else{
            Element subtab = createShotToplistElement(null);
            tab2.addContent(subtab);
        }
        return tab2;
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
        int size = topListItems.size();
        if(size>10)size = 10;

        topListItems = topListItems.subList(0,size);
        for(TopList.TopListItem topListItem:topListItems){
            Element onecontent = createScoreItemtElement(topListItem);
            subtab.addContent(onecontent);
        }

        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        subtab.addContent(source);
        Element more = new Element("more");
        more.setAttribute("linkContent","查看更多");
        more.addContent(new CDATA("http://m.lesports.com/12/standings?ch="+TOP12_CH));
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

        if(topList != null){
            List<TopList.TopListItem> topListItems = topList.getItems();
            int size = topListItems.size();
            if(size>10)size = 10;
            topListItems = topListItems.subList(0,size);
            for(TopList.TopListItem topListItem:topListItems){
                Element onecontent = createShotItemtElement(topListItem);
                subtab.addContent(onecontent);
            }
        }
        else{
            Map<String,Object> stats = Maps.newHashMap();
            stats.put("goals","-");
            TopList.TopListItem topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106841007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(1);
            topListItem.setShowOrder(1);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            Element onecontent = createShotItemtElement(topListItem);
            subtab.addContent(onecontent);

            stats.put("goals","-");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106775007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(2);
            topListItem.setShowOrder(2);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createShotItemtElement(topListItem);
            subtab.addContent(onecontent);

            stats.put("goals","-");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106822007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(3);
            topListItem.setShowOrder(3);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createShotItemtElement(topListItem);
            subtab.addContent(onecontent);

            stats.put("goals","-");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106719007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(4);
            topListItem.setShowOrder(4);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createShotItemtElement(topListItem);
            subtab.addContent(onecontent);

            stats.put("goals","-");
            topListItem = new TopList.TopListItem();
            topListItem.setCompetitorId(106772007L);
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(5);
            topListItem.setShowOrder(5);
            topListItem.setStats(stats);
            topListItem.setTeamId(1440006L);

            onecontent = createShotItemtElement(topListItem);
            subtab.addContent(onecontent);
        }


        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        subtab.addContent(source);
        Element more = new Element("more");
        more.setAttribute("linkContent","查看更多");
        more.addContent(new CDATA("http://12.lesports.com/scorer?ch="+TOP12_CH));
        subtab.addContent(more);

        return subtab;
    }

    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem) throws Exception{
        Element list = new Element("list");
        Map<String,Object> stats = topListItem.getStats();

        Team team = SbdsInternalApis.getTeamById(topListItem.getCompetitorId());

        String logo = team.getLogo();

        if(StringUtils.isNotBlank(logo)){
            int pos = logo.lastIndexOf(".");
            logo = logo.substring(0,pos) +"/11_112_112"+logo.substring(pos, logo.length());
        }else{
            logo = Constants.top12LogoUrlMap.get(team.getId());
        }


        Element col1 = new Element("col1");
        col1.addContent(new CDATA(logo));
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

        String logo = team.getLogo();
        if(StringUtils.isNotBlank(logo)){
            int pos = logo.lastIndexOf(".");
            logo = logo.substring(0,pos) +"/11_112_112"+logo.substring(pos, logo.length());
        }
        else{
            logo = Constants.top12LogoUrlMap.get(team.getId());
        }


        Element col1 = new Element("col1");
        col1.addContent(new CDATA(logo));
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


    //生成每场比赛的Element
    public Element createOneContentElement(Match match) throws Exception{
        Element game = new Element("game");

        Set<Match.Competitor> competitors = match.getCompetitors();
        Team homeTeam = null,awayTeam = null;
        String homeResult = "0",awayResult = "0";

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                homeResult = competitor.getFinalResult();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                awayResult = competitor.getFinalResult();
            }
        }

        if(homeTeam == null || awayTeam == null) return null;

        Element team1 = new Element("team1");

        Element name = new Element("name");
        name.addContent(new CDATA(homeTeam.getName()));
        team1.addContent(name);

        String logo = homeTeam.getLogo();

        if(StringUtils.isNotBlank(logo)){
            int pos = logo.lastIndexOf(".");
            logo = logo.substring(0,pos) +"/11_112_112"+logo.substring(pos, logo.length());
        }else{
            logo = Constants.top12LogoUrlMap.get(homeTeam.getId());
        }
        Element img = new Element("img");
        img.addContent(new CDATA(logo));
        team1.addContent(img);

        Element link = new Element("link");
        link.addContent(new CDATA("http://m.lesports.com/team/"+homeTeam.getId()+".html?ch="+TOP12_CH));
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
        url.addContent(new CDATA("http://m.sports.le.com/match/"+match.getId()+".html??ch="+TOP12_CH));
        status.addContent(url);

        game.addContent(status);

        Element team2 = new Element("team2");

        name = new Element("name");
        name.addContent(new CDATA(awayTeam.getName()));
        team2.addContent(name);

        logo = awayTeam.getLogo();

        if(StringUtils.isNotBlank(logo)){
            int pos = logo.lastIndexOf(".");
            logo = logo.substring(0,pos) +"/11_112_112"+logo.substring(pos, logo.length());
        }else{
            logo = Constants.top12LogoUrlMap.get(awayTeam.getId());
        }
        img = new Element("img");
        img.addContent(new CDATA(logo));
        team2.addContent(img);

        link = new Element("link");
        link.addContent(new CDATA("http://m.lesports.com/team/"+awayTeam.getId()+".html?ch="+TOP12_CH));
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
}
