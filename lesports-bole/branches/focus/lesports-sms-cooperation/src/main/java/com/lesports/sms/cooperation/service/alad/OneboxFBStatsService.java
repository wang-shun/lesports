package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.vo.*;
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
import java.util.*;

/**
 * Created by zhonglin on 2016/3/7.
 */
@Service("oneboxFBStatsService")
public class OneboxFBStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(OneboxFBStatsService.class);


    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");



    //生成给360的赛程xml，并且上传到ftp服务器
    public void oneboxFbStats(Long cid, int roundSize) {
        LOG.info("begin cid: {} " ,cid);
        String fileName = Constants.oneboxParamNameMap.get("file_name_"+cid) + Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName,cid,roundSize);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成xml文件
    public boolean createXmlFile(String file,Long cid,int roundSize){
        try{
            LOG.info("create xml file cid : {} , competition_name: {} ",cid,Constants.oneboxParamNameMap.get("competition_name_"+cid));
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{} ", cid);
                return false;
            }

            //当前轮次
            int round = competitionSeason.getCurrentRound();
            int totalRound = competitionSeason.getTotalRound();
            long csid = competitionSeason.getId();

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = createItemElement(Constants.oneboxParamNameMap.get("competition_name_"+cid),"第"+round+"轮",round,totalRound,roundSize,cid,csid);
            root.addContent(item);

            item = createItemElement(Constants.oneboxParamNameMap.get("competition_name_"+cid)+"积分榜","积分榜",round,totalRound,roundSize,cid,csid);
            root.addContent(item);

            item = createItemElement(Constants.oneboxParamNameMap.get("competition_name_"+cid)+"射手榜","射手榜",round,totalRound,roundSize,cid,csid);
            root.addContent(item);
            File path = new File(file);


            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }

    public Element createItemElement(String keyStr,String current_tab,int round,int totalRound,int roundSize,long cid,long csid) throws Exception{
        Element item = new Element("item");
        LOG.info("keyStr: {}",keyStr);

        Element key = new Element("key");
        key.addContent(new CDATA(keyStr));
        item.addContent(key);

        Element display = new Element("display");
        item.addContent(display);

        Element title = new Element("title");
        title.addContent(new CDATA("2016赛季"+Constants.oneboxParamNameMap.get("competition_name_"+cid)+"联赛_乐视体育_乐视网"));
        LOG.info("cid:{} , copetition_name: {} ",cid,Constants.oneboxParamNameMap.get("competition_name_"+cid));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA(Constants.oneboxParamNameMap.get("domain_"+cid)));
        display.addContent(url);


        Element currentTab = new Element("current_tab");
        currentTab.addContent(new CDATA(current_tab));
        display.addContent(currentTab);

        Element tabArea = new Element("tab_area");

        int beginRound = round;
        if(roundSize == 4){
            beginRound = round -1;
            if(beginRound == 0){
                beginRound = 1;
            }
        }

        if(beginRound + roundSize <=(totalRound)){
            for(int i=0;i<roundSize;i++){
                Element tabMatch = createRoundElement(beginRound+i,cid,csid);
                tabArea.addContent(tabMatch);
            }
        }
        else{
            for(int i=(roundSize-1);i>=0;i--){
                Element tabMatch = createRoundElement(totalRound-i,cid,csid);
                tabArea.addContent(tabMatch);
            }
        }

        //积分榜
        List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORE_ID);
        Element tabScore = createScoreToplistElement(scoreTopList.get(0));
        tabArea.addContent(tabScore);

        //射手榜
        List<TopList> scorerTopList  = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORER_ID);
        Element tabScorer = createScorerToplistElement(scorerTopList.get(0));
        tabArea.addContent(tabScorer);

        display.addContent(tabArea);
        Element showUrl = new Element("showurl");
        showUrl.addContent(new CDATA("csl.lesports.com"));
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


        for(TComboEpisode episode:episodes){
//            LOG.info("tComboEpisode: {} , time: {}", episode.getName(), episode.getStartTime());
            Match match = SbdsInternalApis.getMatchById(episode.getMid());
            Element oneconten = createOneContentElement(match,episode);
            tabMatch.addContent(oneconten);
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看"+Constants.oneboxParamNameMap.get("competition_name_"+cid)+"全部赛程》"));
        tabMatch.addContent(moretext);
        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA(Constants.oneboxParamNameMap.get("fixture_"+cid)+"?ch="+Constants.oneboxParamNameMap.get("ch_"+cid)));
        tabMatch.addContent(moreurl);

        return tabMatch;
    }

    //生成每场比赛的Element
    public Element createOneContentElement(Match match,TComboEpisode episode) throws Exception{
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
        List<TCompetitor> competitors = episode.getCompetitors();

        String homeName = "";
        String awayName = "";
        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        for(TCompetitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeResult = competitor.getFinalResult();
                homeId = competitor.getId();
                homeName = competitor.getName();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayResult = competitor.getFinalResult();
                awayId = competitor.getId();
                awayName = competitor.getName();

            }
        }
        Element team1 = new Element("team1");
        team1.addContent(new CDATA(homeName));
        teamVs.addContent(team1);
        Element team1url = new Element("team1url");
        team1url.addContent(new CDATA("http://www.lesports.com/team/"+homeId+".html?ch="+Constants.oneboxParamNameMap.get("ch_"+match.getCid())));
        teamVs.addContent(team1url);

        Element team2 = new Element("team2");
        team2.addContent(new CDATA(awayName));
        teamVs.addContent(team2);
        Element team2url = new Element("team2url");
        team2url.addContent(new CDATA("http://www.lesports.com/team/"+awayId+".html?ch="+Constants.oneboxParamNameMap.get("ch_"+match.getCid())));
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
        if(LiveShowStatus.LIVE_NOT_START.equals(episode.getStatus()) || LiveShowStatus.LIVING.equals(episode.getStatus())){
            text.addContent(new CDATA("视频直播"));
        }
        else{
            text.addContent(new CDATA("视频回放"));
        }
        link.addContent(text);
        Element url = new Element("url");
        url.addContent(new CDATA("http://sports.le.com/match/"+match.getId()+".html?ch="+Constants.oneboxParamNameMap.get("ch_"+match.getCid())));
        link.addContent(url);
        onecontent.addContent(link);

        return onecontent;
    }

    public Format FormatXML(){
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }

    //生成积分排行榜的Element
    public Element createScoreToplistElement(TopList topList) throws Exception{
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

        List<TopList.TopListItem> topListItems = topList.getItems();
        topListItems = topListItems.subList(0,10);
        for(TopList.TopListItem topListItem:topListItems){
            Element onecontent = createScoreItemtElement(topListItem, topList.getCid());
            tabScore.addContent(onecontent);
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看完整积分榜》"));
        tabScore.addContent(moretext);
        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA(Constants.oneboxParamNameMap.get("score_"+topList.getCid())+"?ch="+Constants.oneboxParamNameMap.get("ch_"+topList.getCid())));
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

        List<TopList.TopListItem> topListItems = topList.getItems();
        topListItems = topListItems.subList(0,10);
        for(TopList.TopListItem topListItem:topListItems){
            Element onecontent = createScorerItemtElement(topListItem,topList.getCid());
            tabScorer.addContent(onecontent);
        }

        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看完整射手榜》"));
        tabScorer.addContent(moretext);
        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA(Constants.oneboxParamNameMap.get("scorer_"+topList.getCid())+"?ch="+Constants.oneboxParamNameMap.get("ch_"+topList.getCid())));
        tabScorer.addContent(moreurl);

        return tabScorer;
    }

    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem,Long cid) throws Exception{
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
        url.addContent(new CDATA(("http://www.lesports.com/team/"+team.getId()+".html?ch="+Constants.oneboxParamNameMap.get("ch_"+cid))));
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
    public Element createScorerItemtElement(TopList.TopListItem topListItem,Long cid) throws Exception{
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
        url.addContent(new CDATA(("http://www.lesports.com/player/"+player.getId()+".html?ch="+Constants.oneboxParamNameMap.get("ch_"+cid))));
        li.addContent(url);
        onecontent.addContent(li);
        Team team = SbdsInternalApis.getTeamById(topListItem.getTeamId());
        li = new Element("li");
        text = new Element("text");
        text.addContent(new CDATA(team.getName()));
        li.addContent(text);
        url = new Element("url");
        url.addContent(new CDATA(("http://www.lesports.com/team/"+team.getId()+".html?ch="+Constants.oneboxParamNameMap.get("ch_"+cid))));
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
