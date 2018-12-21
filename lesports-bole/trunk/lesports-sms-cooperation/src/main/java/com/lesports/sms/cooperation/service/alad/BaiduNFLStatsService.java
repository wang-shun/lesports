package com.lesports.sms.cooperation.service.alad;

import ch.qos.logback.core.joran.spi.XMLUtil;
import com.lesports.api.common.CountryCode;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhonglin on 2016/9/6.
 */
@Service("baiduNFLStatsService")
public class BaiduNFLStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduNFLStatsService.class);
    private static final String NFL_CH = "alad_nfl";


    //生成给百度的赛程xml，并且上传到ftp服务器
    public void baiduNFLStats() {

        String fileName = "2016nfl"+Constants.fileextraname+".xml";
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
            url.addContent("http://nfl.lesports.com/");
            display.addContent(url);

            Element title = new Element("title");
            title.addContent("NFL2016-2017赛季_常规赛视频直播_乐视体育");
            display.addContent(title);

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
                current = 2;
                beforeSize = 1;
                nextSize = 2;
            }
            //未开赛或者第一天
            else if(nowDate.compareTo(beginDate) <= 0){
                current = 1;
                nextSize = 3;
            }
            //已结束或者最后一天
            else{
                today = LeDateUtils.parseYYYYMMDD("20170130");
                current = 2;
                nextSize = 2;
            }

            Element showtabindex = new Element("showtabindex");
            showtabindex.addContent(current+"");
            display.addContent(showtabindex);

            Element videoicon = new Element("videoicon");
            videoicon.addContent("0");
            display.addContent(videoicon);

            Element statuslist = new Element("statuslist");
            statuslist.setAttribute("st0","未开始");
            statuslist.setAttribute("st1","比赛中");
            statuslist.setAttribute("st2","已结束");
            display.addContent(statuslist);

            Element morebtn = new Element("morebtn");
            morebtn.setAttribute("showtext","显示全部赛程");
            morebtn.setAttribute("hidetext","收起");
            display.addContent(morebtn);

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
                Element tab = createTab(episodes,date);
                display.addContent(tab);


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
                Element tab = createTab(episodes,date);
                display.addContent(tab);

            }

            Element showurl = new Element("showurl");
            showurl.addContent("nfl.lesports.com");
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


    //生成每天的Element
    public Element createTab(List<Episode> episodes,Date date) throws Exception{
        Element tab = new Element("tab");

        Element title = new Element("title");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");

        String currentDate = simpleDateFormat.format(date);
        String[] times = currentDate.split("-");
        String time = times[0]+"月"+times[1]+"日";
        title.addContent(time);
        tab.addContent(title);


        for(Episode episode:episodes){
            Element tr = createTr(episode);
            if(tr == null) continue;
            tab.addContent(tr);
        }


//        Element bottomlink = new Element("bottomlink");
//        bottomlink.setAttribute("text","球队巡礼");
//        bottomlink.setAttribute("url","http://www.lesports.com/topic/s/mlb30teams/");
//        tab.addContent(bottomlink);
//
//        bottomlink = new Element("bottomlink");
//        bottomlink.setAttribute("text","球队积分");
//        bottomlink.setAttribute("url","http://mlb.lesports.com/");
//        tab.addContent(bottomlink);
//
//        bottomlink = new Element("bottomlink");
//        bottomlink.setAttribute("text","社区讨论");
//        bottomlink.setAttribute("url","http://z.lesports.com/camp/40101");
//        tab.addContent(bottomlink);

        return tab;
    }

    //生成每场比赛的Element
    public Element createTr(Episode episode) throws Exception{
        Match match = SbdsInternalApis.getMatchById(episode.getMid());
        if(match == null) return null;

        Element tr = new Element("tr");
        Element status = new Element("status");
        SimpleDateFormat format = new SimpleDateFormat( "MM/dd HH:mm" );
        tr.setAttribute("time",match.getStartTime().substring(4,6)+"/"+match.getStartTime().substring(6,8)+" "+match.getStartTime().substring(8,10)+":" +match.getStartTime().substring(10,12));

        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        //主客队颠倒一下
        for(Match.Competitor competitor:match.getCompetitors()){
            if (competitor.getGround().equals(GroundType.AWAY)) {
                homeResult = competitor.getFinalResult();
                homeId = competitor.getCompetitorId();
            } else if (competitor.getGround().equals(GroundType.HOME)) {
                awayResult = competitor.getFinalResult();
                awayId = competitor.getCompetitorId();
            }
        }

        Team awayTeam = SbdsInternalApis.getTeamById(awayId);
        if(awayTeam == null) return  null;
        Team homeTeam = SbdsInternalApis.getTeamById(homeId);
        if(homeTeam == null) return  null;


        tr.setAttribute("player1",homeTeam.getNickname());
        String homeLogo = homeTeam.getLogo();
        int pos1 = homeLogo.lastIndexOf(".");
        homeLogo = homeLogo.substring(0,pos1) + "/11_100_100" + homeLogo.substring(pos1);
        tr.setAttribute("player1logo", homeLogo);
        tr.setAttribute("player1url","");

        tr.setAttribute("player2",awayTeam.getNickname());
        String awayLogo = awayTeam.getLogo();
        int pos2 = awayLogo.lastIndexOf(".");
        awayLogo = awayLogo.substring(0,pos2) + "/11_100_100" + awayLogo.substring(pos2);
        tr.setAttribute("player2logo",awayLogo);
        tr.setAttribute("player2url","");

        tr.setAttribute("status",episode.getStatus().getValue()+"");



        Element score = new Element("score");
        if(episode.getStatus().equals(LiveShowStatus.LIVE_END)){
            tr.setAttribute("score",homeResult+"-"+awayResult);
        }
        else{
            tr.setAttribute("score","VS");
        }


        //没有直播的

        if(episode.getHasLive()!=null && episode.getHasLive()){
            tr.setAttribute("link1text","视频直播");
            tr.setAttribute("link1url","http://sports.le.com/match/"+match.getId()+".html?ch="+NFL_CH);



        }
        else{
            tr.setAttribute("link1text","视频录播");
            tr.setAttribute("link1url","");

        }
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
        internalQuery.addCriteria(InternalCriteria.where("mids").is(match.getId()));
        internalQuery.addCriteria(InternalCriteria.where("tag_ids").is(101589008L));
        internalQuery.addCriteria(InternalCriteria.where("allow_country").is("CN"));
        internalQuery.with(PageUtils.createPageable(0, 20));
        List<News> newses = SopsInternalApis.getNewsByQuery(internalQuery);
        tr.setAttribute("link2text","比赛集锦");

        if(CollectionUtils.isNotEmpty(newses)){
            tr.setAttribute("link2url","http://sports.le.com/news/"+newses.get(0).getId()+".html?ch="+NFL_CH);
        }
        else{
            tr.setAttribute("link2url","");
        }


        return tr;
    }


}
