package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.api.common.*;
import com.lesports.api.common.Sort;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.EpisodeType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.service.*;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.api.vo.TTeam;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.StatsConstants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LesportsEnv;
import com.lesports.utils.PageUtils;
import freemarker.template.utility.StringUtil;
import jxl.demo.XML;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
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
 * Created by zhonglin on 2017/2/6.
 */
@Service("baiduCupStatsService")
public class BaiduCupStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduEplStatsService.class);
    private static List<String> groupNames = Lists.newArrayList("A","B","C","D","E","F","G","H");
    private static Map<String,Long> roundIdMap = Constants.roundIdMap;
    private static final String CH = "alad_yaguan";

    //生成给百度pc的杯赛xml，并且上传到ftp服务器
    public void baiduCupStats(Long cid,String fileHead) {
        String fileName = fileHead+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName,cid);
        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadBaiduXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
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
                LOG.error("Competition is  null cid:{}", cid);
                return false;
            }

            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(competition.getAbbreviation());
            item.addContent(key);

            Element display = new Element("display");

            Element title = new Element("title");
            title.addContent(competitionSeason.getSeason()+competition.getAbbreviation()+"("+competition.getName()+")赛程赛果_乐视体育");
            display.addContent(title);

            Element url = new Element("url");
            url.addContent("http://www.lesports.com");
            display.addContent(url);

            //todo 判断当前进行的阶段
//            List<TComboEpisode> todayEpisodes = Lists.newArrayList();
//            List<TComboEpisode> preEpisodes = Lists.newArrayList();
//            List<TComboEpisode> nextEpisodes = Lists.newArrayList();

            List<Match> todayMatches = Lists.newArrayList();
            List<Match> preMatches = Lists.newArrayList();
            List<Match> nextMatches = Lists.newArrayList();

            List<TComboEpisode> tab1Episodes = Lists.newArrayList();
            List<TComboEpisode> tab4Episodes = Lists.newArrayList();
            String currentTab = "1";
            Date today = new Date();
            CallerParam caller = CallerUtils.getDefaultCaller();
            Date tab1Date = today ,tab2Date = today;
            Sort sort = new Sort();
            PageParam page = PageUtils.createPageParam(0, 1);

            //当天的比赛
//            GetSomeDayEpisodesParam p = new GetSomeDayEpisodesParam();
//            p.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
//            p.setCids(Lists.newArrayList(100234001L));
//            p.setDate(LeDateUtils.formatYYYYMMDD(today));
//            todayEpisodes = SopsApis.getSomeDayEpisodes(p, PageUtils.createPageParam(0,20), caller);

            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("startDate", "eq", LeDateUtils.formatYYYYMMDD(today)));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.addCriteria(new InternalCriteria("csid", "eq", competitionSeason.getId()));
            q.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
            todayMatches = SbdsInternalApis.getMatchsByQuery(q);

            //最近的一场未开始的赛程
//            GetCurrentEpisodesParam nexParam = new GetCurrentEpisodesParam();
//            nexParam.setCsid(competitionSeason.getId());
//            nexParam.setCids(Lists.newArrayList(100234001L));
//            nexParam.setStartDate(LeDateUtils.formatYYYYMMDD(today));
//            nexParam.setLiveShowStatusParam(LiveShowStatusParam.LIVE_NOT_START);

//            sort.addToOrders(new Order("start_time", Direction.ASC));
//            page.setSort(sort);
//            nextEpisodes =  SopsApis.getTimelineEpisodesByCids(nexParam,page , caller);

            InternalQuery q1 = new InternalQuery();
            q1.addCriteria(new InternalCriteria("startDate", "gt", LeDateUtils.formatYYYYMMDD(today)));
            q1.addCriteria(new InternalCriteria("deleted", "eq", false));
            q1.addCriteria(new InternalCriteria("csid", "eq", competitionSeason.getId()));
            q1.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
            q1.with(PageUtils.createPageable(0,20));
            nextMatches = SbdsInternalApis.getMatchsByQuery(q1);


            //近的一场已结束的赛程
//            GetCurrentEpisodesParam endParam = new GetCurrentEpisodesParam();
//            endParam.setCsid(competitionSeason.getId());
//            endParam.setCids(Lists.newArrayList(100234001L));
//            endParam.setStartDate(LeDateUtils.formatYYYYMMDD(today));
//            endParam.setLiveShowStatusParam(LiveShowStatusParam.LIVE_END);
//
//
//
//            sort = new Sort();
//            sort.addToOrders(new Order("start_time", Direction.DESC));
//            page = PageUtils.createPageParam(0, 1);
//            page.setSort(sort);

//            Sort sort1 = new Sort();
//            sort1.addToOrders(new Order("start_time", Direction.ASC));
//            PageParam page1 = PageUtils.createPageParam(0, 20);
//            page1.setSort(sort1);
//
//            preEpisodes = SopsApis.getTimelineEpisodesByCids(endParam, page, caller);

            InternalQuery q2 = new InternalQuery();
            q2.addCriteria(new InternalCriteria("startDate", "lt", LeDateUtils.formatYYYYMMDD(today)));
            q2.addCriteria(new InternalCriteria("deleted", "eq", false));
            q2.addCriteria(new InternalCriteria("csid", "eq", competitionSeason.getId()));
            q2.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.DESC, "start_time"))));
            q2.with(PageUtils.createPageable(0,20));
            preMatches = SbdsInternalApis.getMatchsByQuery(q2);

            Sort sort1 = new Sort();
            sort1.addToOrders(new Order("start_time", Direction.ASC));
            PageParam page1 = PageUtils.createPageParam(0, 20);
            page1.setSort(sort1);


            //当天有比赛
            if(CollectionUtils.isNotEmpty(todayMatches)){
                LOG.info("当天有比赛,最后一天");
                //其他赛事已打完，显示当天和之前的，定位到当天
                if(CollectionUtils.isEmpty(todayMatches)){
                    tab1Date = LeDateUtils.parseYYYYMMDDHHMMSS(todayMatches.get(0).getStartTime());
                    tab2Date = today;
                    currentTab = "4";
                }
                //赛事刚开打，显示当天和后一天的，定位到当天
                else if(CollectionUtils.isEmpty(preMatches)){
                    LOG.info("当天有比赛,第一天");
                    tab2Date = LeDateUtils.parseYYYYMMDDHHMMSS(preMatches.get(0).getStartTime());
                    tab1Date = today;
                    currentTab = "1";
                }
                //赛事进行中，显示当天和距离最近的一场，定位到当天
                else{
                    Match preMatch = preMatches.get(0);
                    Match nextMatch = nextMatches.get(0);

                    int days1 = XmlUtil.daysBetween(preMatch.getStartTime().substring(0,8),LeDateUtils.formatYYYYMMDD(today));
                    int days2 = XmlUtil.daysBetween(LeDateUtils.formatYYYYMMDD(today),nextMatch.getStartTime().substring(0,8));

                    //距离未开赛更近
                    if(days2 <= days1){
                        LOG.info("当天有比赛,接近下一场，tab1Date:{},tab2Date: {}",LeDateUtils.formatYYYY_MM_DD(tab1Date),LeDateUtils.formatYYYY_MM_DD(tab2Date));
                        tab2Date = LeDateUtils.parseYYYYMMDDHHMMSS(nextMatches.get(0).getStartTime());
                        tab1Date = today;
                        currentTab = "1";
                    }
                    //距离已结束更近
                    else{
                        LOG.info("当天有比赛,接近上一场，tab1Date:{},tab2Date: {}",LeDateUtils.formatYYYY_MM_DD(tab1Date),LeDateUtils.formatYYYY_MM_DD(tab2Date));
                        tab1Date = LeDateUtils.parseYYYYMMDDHHMMSS(preMatches.get(0).getStartTime());
                        tab2Date = today;
                        currentTab = "4";
                    }
                }



                //tab1的数据
                GetSomeDayEpisodesParam p1 = new GetSomeDayEpisodesParam();
                p1.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                p1.setDate(LeDateUtils.formatYYYYMMDD(tab1Date));
                p1.setCids(Lists.newArrayList(100234001L));
                tab1Episodes = SopsApis.getSomeDayEpisodes(p1, page1, caller);

                //tab4的数据
                GetSomeDayEpisodesParam p2 = new GetSomeDayEpisodesParam();
                p2.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                p2.setDate(LeDateUtils.formatYYYYMMDD(tab2Date));
                p2.setCids(Lists.newArrayList(100234001L));
                tab4Episodes = SopsApis.getSomeDayEpisodes(p2, page1, caller);

            }
            //当天没有比赛
            else{
                //没有比赛了,最后一天和倒数第二天的比赛
                if(CollectionUtils.isEmpty(nextMatches)){
                    tab2Date = LeDateUtils.parseYYYYMMDDHHMMSS(preMatches.get(0).getStartTime());
                    GetSomeDayEpisodesParam p2 = new GetSomeDayEpisodesParam();
                    p2.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                    p2.setDate(LeDateUtils.formatYYYYMMDD(tab2Date));
                    p2.setCids(Lists.newArrayList(100234001L));
                    tab4Episodes = SopsApis.getSomeDayEpisodes(p2, page1, caller);
                    int i=1;
                    while(true && i<=10){
                        GetSomeDayEpisodesParam p1 = new GetSomeDayEpisodesParam();
                        p1.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                        tab1Date = LeDateUtils.addDay(tab2Date,-i);
                        p1.setDate(LeDateUtils.formatYYYYMMDD(tab1Date));
                        p1.setCids(Lists.newArrayList(100234001L));
                        tab1Episodes = SopsApis.getSomeDayEpisodes(p2, page1, caller);
                        if(CollectionUtils.isNotEmpty(tab1Episodes)){
                            break;
                        }
                        i++;
                    }
                    LOG.info("当天没有比赛,上两场，tab1Date:{},tab2Date: {}",LeDateUtils.formatYYYY_MM_DD(tab1Date),LeDateUtils.formatYYYY_MM_DD(tab2Date));
                    currentTab = "4";
                }
                //还没有开打，第一天和第二天的比赛
                else if(CollectionUtils.isEmpty(preMatches)){
                    tab1Date = LeDateUtils.parseYYYYMMDDHHMMSS(nextMatches.get(0).getStartTime());
                    LOG.info("tab1Date:{} ",LeDateUtils.formatYYYY_MM_DD(tab1Date));
                    GetSomeDayEpisodesParam p1 = new GetSomeDayEpisodesParam();
                    p1.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                    p1.setDate(LeDateUtils.formatYYYYMMDD(tab1Date));
                    p1.setCids(Lists.newArrayList(100234001L));
                    tab1Episodes = SopsApis.getSomeDayEpisodes(p1, PageUtils.createPageParam(0,20), caller);
                    int i=1;
                    while(true && i<=10){
                        GetSomeDayEpisodesParam p2 = new GetSomeDayEpisodesParam();
                        p2.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                        tab2Date = LeDateUtils.addDay(tab1Date,i);
                        p2.setDate(LeDateUtils.formatYYYYMMDD(tab2Date));
                        p2.setCids(Lists.newArrayList(100234001L));
                        tab4Episodes = SopsApis.getSomeDayEpisodes(p2, page1, caller);
                        if(CollectionUtils.isNotEmpty(tab4Episodes)){
                            LOG.info("tab2Date:{} ",LeDateUtils.formatYYYY_MM_DD(tab2Date));
                            break;
                        }
                        i++;
                    }
                    LOG.info("当天没有比赛,下两场，tab1Date:{},tab2Date: {}",LeDateUtils.formatYYYY_MM_DD(tab1Date),LeDateUtils.formatYYYY_MM_DD(tab2Date));
                    currentTab = "1";
                }
                else{
                    Match preMatch = preMatches.get(0);
                    Match nextMatch = nextMatches.get(0);

                    int days1 = XmlUtil.daysBetween(preMatch.getStartTime().substring(0,8),LeDateUtils.formatYYYYMMDD(today));
                    int days2 = XmlUtil.daysBetween(LeDateUtils.formatYYYYMMDD(today),nextMatch.getStartTime().substring(0,8));

                    LOG.info("time1:{},time2:{} ,days1:{} ,days2:{} ",preMatch.getStartTime(),nextMatch.getStartTime(),days1,days2);

                    //距离未开赛的时间更近
                    if(days2 <= days1){
                        tab1Date = LeDateUtils.parseYYYYMMDDHHMMSS(nextMatches.get(0).getStartTime());
                        GetSomeDayEpisodesParam p1 = new GetSomeDayEpisodesParam();
                        p1.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                        p1.setDate(LeDateUtils.formatYYYYMMDD(tab1Date));
                        p1.setCids(Lists.newArrayList(100234001L));
                        tab1Episodes = SopsApis.getSomeDayEpisodes(p1, page1, caller);

                        int i=1;
                        while(true && i<=10){
                            GetSomeDayEpisodesParam p2 = new GetSomeDayEpisodesParam();
                            p2.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                            tab2Date = LeDateUtils.addDay(tab1Date,i);
                            p2.setDate(LeDateUtils.formatYYYYMMDD(tab2Date));
                            p2.setCids(Lists.newArrayList(100234001L));

                            tab4Episodes = SopsApis.getSomeDayEpisodes(p2,page1, caller);
                            if(CollectionUtils.isNotEmpty(tab4Episodes)){
                                LOG.info("tab2Date:{} ",LeDateUtils.formatYYYY_MM_DD(tab2Date));
                                break;
                            }
                            i++;
                        }
                        LOG.info("当天没有比赛,接近下两场，tab1Date:{},tab2Date: {}",LeDateUtils.formatYYYY_MM_DD(tab1Date),LeDateUtils.formatYYYY_MM_DD(tab2Date));
                        currentTab = "1";
                    }
                    //距离已结束时间更近
                    else{
                        tab2Date = LeDateUtils.parseYYYYMMDDHHMMSS(preMatches.get(0).getStartTime());
                        GetSomeDayEpisodesParam p2 = new GetSomeDayEpisodesParam();
                        p2.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                        p2.setDate(LeDateUtils.formatYYYYMMDD(tab2Date));
                        p2.setCids(Lists.newArrayList(100234001L));
                        tab4Episodes = SopsApis.getSomeDayEpisodes(p2, page1, caller);
                        int i=1;
                        while(true && i<=10){
                            GetSomeDayEpisodesParam p1 = new GetSomeDayEpisodesParam();
                            p1.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                            tab1Date = LeDateUtils.addDay(tab2Date,-i);
                            p1.setDate(LeDateUtils.formatYYYYMMDD(tab1Date));
                            p1.setCids(Lists.newArrayList(100234001L));
                            tab1Episodes = SopsApis.getSomeDayEpisodes(p1, page1, caller);
                            if(CollectionUtils.isNotEmpty(tab1Episodes)){
                                break;
                            }
                            i++;
                        }
                        LOG.info("当天没有比赛,接近上两场，tab1Date:{},tab2Date: {}",LeDateUtils.formatYYYY_MM_DD(tab1Date),LeDateUtils.formatYYYY_MM_DD(tab2Date));
                        currentTab = "4";
                    }
                }
            }

            Element currenttab = new Element("currenttab");
            currenttab.addContent(currentTab);
            display.addContent(currenttab);

            //todo tab1

            if(CollectionUtils.isNotEmpty(tab1Episodes)){
                Element tab1 = new Element("tab1");
                String tab1Time = LeDateUtils.formatYYYYMMDD(tab1Date);
                tab1.addContent(tab1Time.substring(4,6)+"月"+tab1Time.substring(6,8)+"日赛程");
                display.addContent(tab1);

                Element th1 = new Element("th1");
                th1.setAttribute("col1","日期");
                th1.setAttribute("col2","时间");
                th1.setAttribute("col3","对阵");
                th1.setAttribute("col4","赛别");
                th1.setAttribute("col5","电视直播");
                th1.setAttribute("col6","赛事观看");
                display.addContent(th1);
                for(TComboEpisode episode:tab1Episodes){
                    if(!episode.getType().equals(EpisodeType.MATCH))continue;
                    Element tr = createTabOneContentElement(episode, 1);
                    display.addContent(tr);
                }

                Element morelink1 = new Element("morelink1");
                morelink1.setAttribute("show","1");
                morelink1.setAttribute("linkurl","http://afccl.lesports.com/schedule/");
                morelink1.setAttribute("linkcontent","完整赛程>>");
                morelink1.setAttribute("link1url","");
                morelink1.setAttribute("link1content","");
                morelink1.setAttribute("link2url","");
                morelink1.setAttribute("link2content","");
                morelink1.setAttribute("link3url","http://afccl.lesports.com/table");
                morelink1.setAttribute("link3content","积分榜");
                display.addContent(morelink1);

            }


            int beginRound = 1;
            int endRound = 3;
            if(CollectionUtils.isNotEmpty(tab1Episodes)){
                TComboEpisode currentEpisode = tab1Episodes.get(0);
                if(currentEpisode.getGroup()!= null && currentEpisode.getRound() != null){
                    int currentRound = XmlUtil.getRoundNum(currentEpisode.getRound());
                    if(currentRound >2 && currentRound <6){
                        beginRound = currentRound -1;
                        endRound = currentRound+1;
                    }
                    else if(currentRound == 6){
                        beginRound = 4;
                        endRound = 6;
                    }
                }
                else{
                    beginRound = 4;
                    endRound = 6;
                }
            }

            //小组赛赛程
            Element tab2 = new Element("tab2");
            tab2.addContent("小组赛程");
            display.addContent(tab2);

            Element groups = new Element("groups");
            Map<String,Long> groupMap = Constants.groupIdMap;
            int i = 0;
            for(String groupName:groupNames){
                i ++ ;
                groups.setAttribute("g"+i,groupName+"组");
            }
            display.addContent(groups);

            Element th2 = new Element("th2");
            th2.setAttribute("col1","日期");
            th2.setAttribute("col2","时间");
            th2.setAttribute("col3","对阵");
            th2.setAttribute("col4","电视直播");
            th2.setAttribute("col5","赛事观看");
            display.addContent(th2);



            for(String groupName:groupNames){
                GetEpisodesOfSeasonByMetaEntryIdParam p2 = new GetEpisodesOfSeasonByMetaEntryIdParam();
                p2.setCid(cid);
                p2.setCsid(competitionSeason.getId());
                p2.setEntryId(groupMap.get(groupName));
                p2.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
                p2.setLiveShowStatusParam(LiveShowStatusParam.findByValue(-1));
                List<TComboEpisode> episodes = SopsApis.getEpisodesOfSeasonByMetaEntryId(p2, PageUtils.createPageParam(0,100), CallerUtils.getDefaultCaller());
                if(CollectionUtils.isNotEmpty(episodes)){
                    for(TComboEpisode episode:episodes){
                        int currentRound = XmlUtil.getRoundNum(episode.getRound());
                        if(currentRound<beginRound || currentRound>endRound) continue;
                        Element tr = createGroupOneContentElement(episode,2, groupName);
                        display.addContent(tr);
                    }
                }
            }

            Element morelink2 = new Element("morelink2");
            morelink2.setAttribute("show","1");
            morelink2.setAttribute("linkurl","http://afccl.lesports.com/schedule/");
            morelink2.setAttribute("linkcontent","完整赛程>>");
            morelink2.setAttribute("link1url","");
            morelink2.setAttribute("link1content","");
            morelink2.setAttribute("link2url","");
            morelink2.setAttribute("link2content","");
            morelink2.setAttribute("link3url","http://afccl.lesports.com/table");
            morelink2.setAttribute("link3content","积分榜");
            display.addContent(morelink2);

            //todo tab4
            if(CollectionUtils.isNotEmpty(tab4Episodes)){
                Element tab4 = new Element("tab4");
                String tab2Time = LeDateUtils.formatYYYYMMDD(tab2Date);
                tab4.addContent(tab2Time.substring(4,6)+"月"+tab2Time.substring(6,8)+"日赛程");
                display.addContent(tab4);

                Element th1 = new Element("th4");
                th1.setAttribute("col1","日期");
                th1.setAttribute("col2","时间");
                th1.setAttribute("col3","对阵");
                th1.setAttribute("col4","赛别");
                th1.setAttribute("col5","电视直播");
                th1.setAttribute("col6","赛事观看");
                display.addContent(th1);
                for(TComboEpisode episode:tab4Episodes){
                    Element tr = createTabOneContentElement(episode, 4);
                    display.addContent(tr);
                }

                Element morelink4 = new Element("morelink4");
                morelink4.setAttribute("show","1");
                morelink4.setAttribute("linkurl","http://afccl.lesports.com/schedule/");
                morelink4.setAttribute("linkcontent","完整赛程>>");
                morelink4.setAttribute("link1url","");
                morelink4.setAttribute("link1content","");
                morelink4.setAttribute("link2url","");
                morelink4.setAttribute("link2content","");
                morelink4.setAttribute("link3url","http://afccl.lesports.com/table");
                morelink4.setAttribute("link3content","积分榜");
                display.addContent(morelink4);

            }
            Element provider = new Element("provider");
            provider.addContent("以上亚冠信息由乐视体育提供，赛程时间均为北京时间");
            display.addContent(provider);
            Element showurl = new Element("showurl");
            showurl.addContent("www.lesports.com");
            display.addContent(showurl);


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

    //生成小组赛每场比赛的Element
    public Element createGroupOneContentElement(TComboEpisode episode,int num,String groupName) throws Exception{
        Element onecontent = new Element("tr"+num);
        String date = episode.getStartTime().substring(4,6)+"月"+episode.getStartTime().substring(6,8)+"日";
        String time = episode.getStartTime().substring(8,10)+":"+episode.getStartTime().substring(10,12);
        onecontent.setAttribute("t",groupName);
        onecontent.setAttribute("col1",date);
        onecontent.setAttribute("col2",time);

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

        onecontent.setAttribute("col31",homeName);
//        if(Constants.afcclMap.get(homeId)!=null){
//            onecontent.setAttribute("col31link",String.format(TEAM_URL,homeId)+"?ch="+CH);
//        }
//        else{
//            onecontent.setAttribute("col31link","");
//        }
        onecontent.setAttribute("col31link","");
        String vs = "VS";
        if(!episode.getMatchStatus().equals(MatchStatus.MATCH_NOT_START) && StringUtils.isNotBlank(homeResult) && StringUtils.isNotBlank(awayResult)){
            vs = homeResult + ":" + awayResult;
        }
        onecontent.setAttribute("col32",vs);
        onecontent.setAttribute("col33",awayName);
//        if(Constants.afcclMap.get(awayId)!=null){
//            onecontent.setAttribute("col33link",String.format(TEAM_URL,awayId)+"?ch="+CH);
//        }
//        else{
//            onecontent.setAttribute("col33link","");
//        }
        onecontent.setAttribute("col33link","");
        if(episode.getStatus().equals(LiveShowStatus.LIVE_NOT_START)){
            onecontent.setAttribute("col4","未开始");
            onecontent.setAttribute("col5","乐视直播");
        }
        else if(episode.getStatus().equals(LiveShowStatus.LIVING)){
            onecontent.setAttribute("col4","直播中");
            onecontent.setAttribute("col5","乐视直播");
        }
        else{
            onecontent.setAttribute("col4","已结束");
            onecontent.setAttribute("col5","视频集锦");
        }


        String matchUrl = "";
        if(episode.getMid() == 1028087003){
            matchUrl = "http://sports.letv.com/match/1028087003.html?ch="+CH+"#live/1020170216191337";
        }
        else if(episode.getMid() == 1028426003){
            matchUrl = "http://sports.letv.com/match/1028426003.html?ch="+CH+"#live/1020170216185708";
        }
        else{
            matchUrl = String.format(Constants.MATCH_URL,episode.getMid())+"?ch="+CH;
        }
        onecontent.setAttribute("col5link",matchUrl);

        return onecontent;
    }

    //生成tab1和tab4每场比赛的Element
    public Element createTabOneContentElement(TComboEpisode episode,int num) throws Exception{
        Element onecontent = new Element("tr"+num);
        String date = episode.getStartTime().substring(4,6)+"月"+episode.getStartTime().substring(6,8)+"日";
        String time = episode.getStartTime().substring(8,10)+":"+episode.getStartTime().substring(10,12);
        onecontent.setAttribute("col1",date);
        onecontent.setAttribute("col2",time);

        List<TCompetitor> competitors = episode.getCompetitors();
        String homeName = "";
        String awayName = "";
        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        if(competitors == null){
            LOG.info("competitors is null: {} ",episode.getName());
            return null;
        }

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
        onecontent.setAttribute("col31",homeName);
//        if(Constants.afcclMap.get(homeId)!=null){
//            onecontent.setAttribute("col31link",String.format(TEAM_URL,homeId));
//        }
//        else{
//            onecontent.setAttribute("col31link","");
//        }
        onecontent.setAttribute("col31link","");
        String vs = "VS";
        if(!episode.getMatchStatus().equals(MatchStatus.MATCH_NOT_START) && StringUtils.isNotBlank(homeResult) && StringUtils.isNotBlank(awayResult)){
            vs = homeResult + ":" + awayResult;
        }
        onecontent.setAttribute("col32",vs);
        onecontent.setAttribute("col33",awayName);
//        if(Constants.afcclMap.get(awayId)!=null){
//            onecontent.setAttribute("col33link",String.format(TEAM_URL,awayId));
//        }
//        else{
//            onecontent.setAttribute("col33link","");
//        }
        onecontent.setAttribute("col33link","");
        if(episode.getStage() != null){
            onecontent.setAttribute("col4",episode.getStage());
        }
        else{
            onecontent.setAttribute("col4",episode.getGroup());
        }


        if(episode.getStatus().equals(LiveShowStatus.LIVE_NOT_START)){
            onecontent.setAttribute("col5","未开始");
            onecontent.setAttribute("col6","乐视直播");
        }
        else if(episode.getStatus().equals(LiveShowStatus.LIVING)){
            onecontent.setAttribute("col5","直播中");
            onecontent.setAttribute("col6","乐视直播");
        }
        else{
            onecontent.setAttribute("col5","已结束");
            onecontent.setAttribute("col6","视频集锦");
        }


        String matchUrl = "";
        if(episode.getMid() == 1028087003){
            matchUrl = "http://sports.letv.com/match/1028087003.html?ch="+CH+"#live/1020170216191337";
        }
        else if(episode.getMid() == 1028426003){
            matchUrl = "http://sports.letv.com/match/1028426003.html?ch="+CH+"#live/1020170216185708";
        }
        else{
            matchUrl = String.format(Constants.MATCH_URL,episode.getMid())+"?ch="+CH;
        }
        onecontent.setAttribute("col6link",matchUrl);
//        onecontent.setAttribute("col6link",String.format(MATCH_URL,episode.getMid())+"?ch="+CH);

        return onecontent;
    }

}
