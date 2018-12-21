package com.lesports.sms.cooperation.service.Top12;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.vo.TComboEpisode;
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
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/8/27.
 */
@Service("top12OneboxMobService")
public class Top12OneboxMobService {


    private static final Logger LOG = LoggerFactory.getLogger(Top12OneboxMobService.class);


    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
    public  static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd");
    private static final String TOP12_CH = "360_12";


    //生成给360的赛程xml，并且上传到ftp服务器
    public void oneboxTop12Stats() {
        String fileName = "onebox2016Top12_mob"+ Constants.fileextraname+".xml";
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
            int currentRound = competitionSeason.getCurrentRound();
            LOG.info("currentRound: {} " ,currentRound);
            long csid = competitionSeason.getId();


            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            List<Integer> rounds = Lists.newArrayList();
            if(currentRound>1 && currentRound<10){
                rounds = Lists.newArrayList(currentRound-1,currentRound,currentRound+1);
            }
            else if(currentRound<2){
                rounds = Lists.newArrayList(1,2,3);
            }
            else{
                rounds = Lists.newArrayList(8,9,10);
            }


            Element item = createItemElement("世预赛","1",rounds,currentRound,Constants.TOP12_COMPETITION_ID,csid);
            root.addContent(item);

            item = createItemElement("世预赛积分榜","2",rounds,currentRound,Constants.TOP12_COMPETITION_ID,csid);
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

    public Element createItemElement(String keyStr,String type,List<Integer> rounds,int currentRound,long cid,long csid) throws Exception{
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
        url.addContent(new CDATA("http://m.lesports.com/12?ch="+TOP12_CH));
        display.addContent(url);
        List<Element> matchsList = Lists.newArrayList();
        String currentMatchs = "";

        for(int round:rounds){
            DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + round + "轮", 100004000);
            if(dictEntry == null){
                LOG.info("dictEntry name:{}, parentId:{}  is null" ,"第" + round + "轮",100004000);
                return null;
            }

            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("csid", "eq", csid));
            q.addCriteria(new InternalCriteria("round", "eq", dictEntry.getId()));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.ASC, "start_time"));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);

            String startDate = simpleDateFormat2.format(LeDateUtils.parseYYYYMMDDHHMMSS(matches.get(0).getStartTime()));
            String endDate = simpleDateFormat2.format(LeDateUtils.parseYYYYMMDDHHMMSS(matches.get(matches.size()-1).getStartTime()));
            String desc = "第" + round + "轮("+startDate + "-" + endDate +  ")";

            if(round == currentRound){
                currentMatchs = desc;
            }

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

            Element matchsElement = createRoundElement(chinaList,desc,currentMatchs);
            matchsList.add(matchsElement);

        }

        String currentTab = "赛程";
        if("2".equals(type)){
            currentTab = "积分榜";
        }
        Element currentTabElement = new Element("currenttab");
        currentTabElement.addContent(new CDATA(currentTab));
        display.addContent(currentTabElement);

        Element tabMatch = new Element("tab_match");

        Element titleElement = new Element("title");
        titleElement.addContent(new CDATA("赛程"));
        tabMatch.addContent(titleElement);

        Element currentmatchs = new Element("currentmatchs");
        currentmatchs.addContent(currentMatchs);
        tabMatch.addContent(currentmatchs);

        if(CollectionUtils.isNotEmpty(matchsList)){
            for(Element matchs:matchsList){
                tabMatch.addContent(matchs);
            }
        }
        display.addContent(tabMatch);

        //积分榜
        List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORE_ID);

        for(TopList topList:scoreTopList){
            if(topList.getGroup() != 100023000L) continue;
            Element tabScore = createScoreToplistElement(topList);
            display.addContent(tabScore);
        }




        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        display.addContent(source);

        Element more = new Element("more");
        more.addContent(new CDATA("进入世预赛专题"));
        display.addContent(more);

        Element moreUrl = new Element("more_url");
        moreUrl.addContent(new CDATA("http://m.lesports.com/12?ch="+TOP12_CH));
        display.addContent(moreUrl);
        return item;
    }

    //生成每个轮次的Element
    public Element createRoundElement(List<Match> matches,String desc,String currentMatchs) throws Exception{
        Element matchsElement = new Element("matchs");

        Element descElement = new Element("desc");
        descElement.addContent(new CDATA(desc));
        matchsElement.addContent(descElement);

        for(Match match:matches){
            Element teamVs =  createOneContentElement(match);
            matchsElement.addContent(teamVs);
        }

        return matchsElement;
    }

    //生成每场比赛的Element
    public Element createOneContentElement(Match match) throws Exception{
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

        Element team1Icon = new Element("team1_icon");
        String logo1 = homeTeam.getLogo();
        if(StringUtils.isNotBlank(logo1)){
            int pos1 = logo1.lastIndexOf(".");
            logo1 = logo1.substring(0,pos1) +"/11_112_112"+logo1.substring(pos1, logo1.length());
        }else{
            logo1 = Constants.top12LogoUrlMap.get(homeTeam.getId());
        }
        team1Icon.addContent(new CDATA(logo1));
        teamVs.addContent(team1Icon);

        Element team2 = new Element("team2");
        team2.addContent(new CDATA(awayTeam.getName()));
        teamVs.addContent(team2);

        Element team2Icon = new Element("team2_icon");
        String logo2 = awayTeam.getLogo();
        if(StringUtils.isNotBlank(logo1)){
            int pos2 = logo2.lastIndexOf(".");
            logo2 = logo2.substring(0,pos2) +"/11_112_112"+logo2.substring(pos2, logo2.length());
        }else{
            logo2 = Constants.top12LogoUrlMap.get(awayTeam.getId());
        }
        team2Icon.addContent(new CDATA(logo2));
        teamVs.addContent(team2Icon);

        Element score = new Element("score");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            score.addContent(new CDATA(homeResult+"-"+awayResult));
        }
        else{
            score.addContent(new CDATA("VS"));
        }
        teamVs.addContent(score);



        Date startTime = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());

        Element date = new Element("date");
        date.addContent(new CDATA(simpleDateFormat.format(startTime)));
        teamVs.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA(simpleDateFormat1.format(startTime)));
        teamVs.addContent(time);


        Element state = new Element("state");
        state.addContent(new CDATA(match.getStatus().getValue()+""));
        teamVs.addContent(state);

        Element url = new Element("url");
        url.addContent(new CDATA(""));
        teamVs.addContent(url);

        return teamVs;
    }


    //生成积分排行榜的Element
    public Element createScoreToplistElement(TopList topList) throws Exception{
        Element tabScore = new Element("tab_score");

        Element title = new Element("title");
        title.addContent(new CDATA("积分榜"));
        tabScore.addContent(title);


        Element li = new Element("li");
        li.addContent(new CDATA("球队"));
        tabScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("胜"));
        tabScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("平"));
        tabScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("负"));
        tabScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("净胜球"));
        tabScore.addContent(li);

        li = new Element("li");
        li.addContent(new CDATA("积分"));
        tabScore.addContent(li);



        if(topList != null){
            LOG.info("topList: {}" ,topList.getId());
            List<TopList.TopListItem> topListItems = topList.getItems();
            int size = topListItems.size();
            if(size>10)size = 10;
            LOG.info("topList size: {}" ,size);
            topListItems = topListItems.subList(0,size);
            for(TopList.TopListItem topListItem:topListItems){
                LOG.info("topListItem: {}" ,topListItem);
                Element list = createScoreItemtElement(topListItem);
                tabScore.addContent(list);
            }
        }

//        Element source = new Element("source");
//        source.addContent(new CDATA("乐视体育》"));
//        tabScore.addContent(source);
//
//        Element moretext = new Element("moretext");
//        moretext.addContent(new CDATA("进入世预赛专题》"));
//        tabScore.addContent(moretext);
//        Element moreurl = new Element("moreurl");
//        moreurl.addContent(new CDATA("http://m.lesports.com/12?ch="+TOP12_CH));
//        tabScore.addContent(moreurl);

        return tabScore;
    }



    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem) throws Exception{

        Element list = new Element("list");
        Map<String,Object> stats = topListItem.getStats();


        Element rank = new Element("rank");
        rank.addContent(new CDATA(topListItem.getRank()+""));
        list.addContent(rank);

        Team team = SbdsInternalApis.getTeamById(topListItem.getCompetitorId());
        Element teamInfo = new Element("team_info");
        teamInfo.addContent(new CDATA(team.getName()));
        list.addContent(teamInfo);


//        teamInfo = new Element("team_info");
//        teamInfo.addContent(new CDATA(stats.get("matchNumber").toString()));
//        list.addContent(teamInfo);


        teamInfo = new Element("team_info");
        teamInfo.addContent(new CDATA(stats.get("winMatch").toString()));
        list.addContent(teamInfo);


        teamInfo = new Element("team_info");
        teamInfo.addContent(new CDATA(stats.get("flatMatch").toString()));
        list.addContent(teamInfo);

        teamInfo = new Element("team_info");
        teamInfo.addContent(new CDATA(stats.get("lossMatch").toString()));
        list.addContent(teamInfo);


        teamInfo = new Element("team_info");
        teamInfo.addContent(new CDATA(stats.get("goalDiffer").toString()));
        list.addContent(teamInfo);

        teamInfo = new Element("team_info");
        teamInfo.addContent(new CDATA(stats.get("teamScore").toString()));
        list.addContent(teamInfo);
        
        return list;
    }


}
