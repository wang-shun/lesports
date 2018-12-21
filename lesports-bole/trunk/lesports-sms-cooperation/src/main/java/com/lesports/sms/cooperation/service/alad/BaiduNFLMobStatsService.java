package com.lesports.sms.cooperation.service.alad;

import com.google.common.collect.Maps;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/9/8.
 */
@Service("baiduNFLMobStatsService")
public class BaiduNFLMobStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduNFLStatsService.class);
    private static final String NFL_CH = "alad_nfl";

    //生成给百度的赛程xml，并且上传到ftp服务器
    public void baiduNFLStats() {

        String fileName = "2016nfl_mob"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadBaiduXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成xml文件
    public boolean createXmlFile(String file){
        try{
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.NFL_COMPETITION_ID);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", Constants.NFL_COMPETITION_ID);
                return false;
            }

            //当前轮次
            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent("NFL");
            item.addContent(key);

            Element display = new Element("display");

            Element url = new Element("url");
            url.addContent("http://m.lesports.com/topic/h/kickoff2016/");
            display.addContent(url);

            Element title = new Element("title");
            title.addContent("NFL视频_NFL直播_NFL美国国家橄榄球联盟_乐视体育");
            display.addContent(title);

            Element highlight = new Element("highlight");
            highlight.addContent("NFL");
            display.addContent(highlight);

            Element curtab = new Element("curtab");
            curtab.addContent("常规赛");
            display.addContent(curtab);



            int current = 0;
            int days = 0;
            int size = 0;
            int beforeSize = 0;
            int nextSize = 0;
            String nowDate = LeDateUtils.formatYYYYMMDD(new Date());
            String beginDate = "20160909";
            String endDate = competitionSeason.getEndTime().substring(0,8);
            Date today = new Date();

            //赛程中
            if(nowDate.compareTo(beginDate)> 0 && nowDate.compareTo(endDate) <0){
                current = 1;
                beforeSize = 1;
                nextSize = 2;
            }
            //未开赛或者第一天
            else if(nowDate.compareTo(beginDate) <= 0){
                current = 0;
                nextSize = 3;
            }
            //已结束或者最后一天
            else{
                today = LeDateUtils.parseYYYYMMDD("20170130");
                current = 2;
                nextSize = 2;
            }

            Element curIndex = new Element("cur_index");
            curIndex.addContent(current+"");
            display.addContent(curIndex);

            Map<String,List<Episode>> episodesMap = Maps.newHashMap();
            int cycle = 0;
            //计算前一天的
            while (size<beforeSize){
                cycle ++;
                if(cycle>20)break;
                days --;
                Date date = LeDateUtils.addDay(today, days);
                String startDate = LeDateUtils.formatYYYYMMDD(date);


                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("csid").is(competitionSeason.getId()));
                internalQuery.addCriteria(InternalCriteria.where("allow_country").is("CN"));
                internalQuery.addCriteria(InternalCriteria.where("start_date").is(startDate));
                internalQuery.with(PageUtils.createPageable(0, 20));
                internalQuery.setSort(new Sort(Sort.Direction.DESC, "start_date"));
                List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);



                LOG.info("startDate: {} " ,startDate + " size: " + episodes.size());
                if(CollectionUtils.isEmpty(episodes)){
                    continue;
                }

                size ++;
                episodesMap.put(startDate,episodes);
            }


            size = 0;
            days = 0;
            cycle = 0;

            //后面两天
            while(size<nextSize){
                cycle ++;
                if(cycle>20)break;
                Date date = LeDateUtils.addDay(today,days);
                String startDate = LeDateUtils.formatYYYYMMDD(date);
                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("csid").is(competitionSeason.getId()));
                internalQuery.addCriteria(InternalCriteria.where("allow_country").is("CN"));
                internalQuery.addCriteria(InternalCriteria.where("start_date").is(startDate));
                internalQuery.with(PageUtils.createPageable(0, 20));
                internalQuery.setSort(new Sort(Sort.Direction.DESC, "start_date"));
                List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);
                LOG.info("startDate: {} " ,startDate + " size: " + episodes.size());
                days ++;
                if(CollectionUtils.isEmpty(episodes)){
                    continue;
                }

                size ++;
                episodesMap.put(startDate, episodes);
            }
            Object[] keys = episodesMap.keySet().toArray();
            Arrays.sort(keys);
            Element gametab = new Element("gametab");
            for(Object keyObj:keys){
                String date = (String)keyObj;
                Element tab = new Element("tab");
                tab.addContent(date.substring(4,6)+"月"+date.substring(6,8)+"日");
                gametab.addContent(tab);
            }
            display.addContent(gametab);

            Element games = new Element("games");
            for(Object keyObj:keys){
                String date = (String)keyObj;
                Element tab = createTab(episodesMap.get(date), date);
                if(tab == null) continue;
                games.addContent(tab);
            }
            display.addContent(games);

            Element provider = new Element("provider");
            provider.addContent("乐视体育");
            display.addContent(provider);

            Element bottomrightText = new Element("bottomright_text");
            bottomrightText.addContent("更多赛程");
            display.addContent(bottomrightText);

            Element bottomrightUrl = new Element("bottomright_url");
            bottomrightUrl.addContent("http://m.lesports.com/topic/h/kickoff2016?ch="+NFL_CH);
            display.addContent(bottomrightUrl);

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


    //生成每天的Element
    public Element createTab(List<Episode> episodes,String date) throws Exception{
        Element tab = new Element("tab");

        for(Episode episode:episodes){
            Element list = createList(episode);
            if(list == null) continue;
            tab.addContent(list);
        }

        return tab;
    }

    //生成每场比赛的Element
    public Element createList(Episode episode) throws Exception{
        Match match = SbdsInternalApis.getMatchById(episode.getMid());
        if(match == null) return null;

        Element list = new Element("list");

        Element gameid = new Element("gameid");
        gameid.addContent(match.getId()+"");
        list.addContent(gameid);

        Element date = new Element("date");
        date.addContent(match.getStartDate().substring(4,6)+"月"+match.getStartDate().substring(6,8)+"日");
        list.addContent(date);

        Element time = new Element("time");
        time.addContent(match.getStartTime().substring(8,10)+":"+match.getStartTime().substring(10,12));
        list.addContent(time);

        Element state = new Element("state");
        state.addContent(match.getStatus().getValue()+"");
        list.addContent(state);

        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        //主客队颠倒一下
        for(Match.Competitor competitor:match.getCompetitors()){
            if (competitor.getGround().equals(GroundType.AWAY)) {
                homeResult = competitor.getFinalResult();
                if(StringUtils.isBlank(homeResult))homeResult="0";
                homeId = competitor.getCompetitorId();
            } else if (competitor.getGround().equals(GroundType.HOME)) {
                awayResult = competitor.getFinalResult();
                if(StringUtils.isBlank(awayResult))awayResult="0";
                awayId = competitor.getCompetitorId();
            }
        }

        Team awayTeam = SbdsInternalApis.getTeamById(awayId);
        if(awayTeam == null) return  null;
        Team homeTeam = SbdsInternalApis.getTeamById(homeId);
        if(homeTeam == null) return  null;

        Element teama = new Element("teama");
        teama.setAttribute("id",homeTeam.getId()+"");
        String homeLogo = homeTeam.getLogo();
        int pos1 = homeLogo.lastIndexOf(".");
        homeLogo = homeLogo.substring(0,pos1) + "/11_100_100" + homeLogo.substring(pos1);
        teama.setAttribute("logo",homeLogo);
        teama.setAttribute("name",homeTeam.getName());
        teama.setAttribute("score",homeResult);
        list.addContent(teama);

        Element teamb = new Element("teamb");
        teamb.setAttribute("id",awayTeam.getId()+"");
        String awayLogo = awayTeam.getLogo();
        int pos2 = awayLogo.lastIndexOf(".");
        awayLogo = awayLogo.substring(0,pos2) + "/11_100_100" + awayLogo.substring(pos2);
        teamb.setAttribute("logo",awayLogo);
        teamb.setAttribute("name",awayTeam.getName());
        teamb.setAttribute("score",awayResult);
        list.addContent(teamb);

        DictEntry dictEntry = SbdsInternalApis.getDictById(match.getRound());

        Element gameRound = new Element("game_round");
        gameRound.addContent("常规赛");
        list.addContent(gameRound);

        Element tabName = new Element("tab_name");
        tabName.addContent(match.getStartDate().substring(4,6)+"月"+match.getStartDate().substring(6,8)+"日");
        list.addContent(tabName);


        //没有直播的
        Element matchurl = new Element("matchurl");

        if(episode.getHasLive()!=null && episode.getHasLive()){
            matchurl.addContent("http://m.lesports.com/match/" + match.getId() + ".html?ch="+NFL_CH);
        }
        else{
            matchurl.addContent("http://m.lesports.com/topic/h/kickoff2016?ch="+NFL_CH);
        }

        list.addContent(matchurl);
        return list;
    }
}
