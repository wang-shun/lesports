package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.service.LiveShowStatusParam;
import com.lesports.sms.api.service.LiveTypeParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.api.vo.TVideo;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import jdk.nashorn.internal.codegen.CompilerConstants;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by zhonglin on 2017/2/6.
 */
@Service("baiduWiseStatsService")
public class BaiduWiseStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduWiseStatsService.class);

    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//    private static String CH = "N_alad_yaguan";

    /**************************************   直播开始   **********************************************/

    //生成给百度wise的直播xml，并且上传到ftp服务器
    public void baiduWiseLiveStats(Long cid, String fileHead,String CH) {
        String fileName = fileHead +"_live" + Constants.fileextraname+".xml";
        Boolean flag = createLiveXmlFile(Constants.filelocalpath + fileName, cid, CH);
        //生成文件成功上传文件
        if(flag){
            LOG.info("localpath: {} , uploadpath: {} " , Constants.filelocalpath, Constants.fileuploadpath);
            XmlUtil.uploadBaiduXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }

    //生成直播xml文件
    public boolean createLiveXmlFile(String file,Long cid,String CH){
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
            root.setAttribute("content_method","full");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(competition.getAbbreviation());
            item.addContent(key);

            Element display = new Element("display");

            Element url = new Element("url");
            url.addContent("http://www.lesports.com");
            display.addContent(url);

            Element type = new Element("type");
            type.addContent("Match");
            display.addContent(type);

            Element data = new Element("data");

            GetEpisodesOfSeasonByMetaEntryIdParam p = new GetEpisodesOfSeasonByMetaEntryIdParam();
            p.setCid(cid);
            p.setCsid(competitionSeason.getId());
            p.setEntryId(0);
//            p.setLiveTypeParam(LiveTypeParam.ONLY_LIVE);
            p.setLiveShowStatusParam(LiveShowStatusParam.findByValue(-1));


            int page = 1;
            while (true){
                List<TComboEpisode> episodes = SopsApis.getEpisodesOfSeasonByMetaEntryId(p, PageUtils.createPageParam(page,100), CallerUtils.getDefaultCaller());
                if(CollectionUtils.isEmpty(episodes)){
                    break;
                }
                for(TComboEpisode episode:episodes){
                    Element oneContent = createLiveItemElement(episode,competition,CH);
                    data.addContent(oneContent);
                }
                page ++ ;
            }

            display.addContent(data);
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

    //生成每场比赛的Element
    public Element createLiveItemElement(TComboEpisode episode,Competition competition,String CH) throws Exception{

        Element onecontent = new Element("item");

        Element status = new Element("status");
        if(episode.getStatus().equals(LiveShowStatus.LIVE_NOT_START)){
            status.addContent("未开始");
        }
        else if(episode.getStatus().equals(LiveShowStatus.LIVING)){
            status.addContent("直播中");
        }
        else{
            status.addContent("已结束");
        }
        onecontent.addContent(status);

        Element startTime = new Element("startTime");

        Date date = LeDateUtils.parseYYYYMMDDHHMMSS(episode.getStartTime());

        Element day = new Element("day");
        day.addContent(LeDateUtils.formatYYYY_MM_DD(date));
        startTime.addContent(day);

        Element time = new Element("time");
        time.addContent(simpleDateFormat.format(date));
        startTime.addContent(time);

        onecontent.addContent(startTime);

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
        Element leftName = new Element("leftName");
        leftName.addContent(homeName);
        onecontent.addContent(leftName);

        Element startTimeStamp = new Element("startTimeStamp");
        startTimeStamp.addContent(date.getTime()/1000+".0");
        onecontent.addContent(startTimeStamp);

        Element rightGoal = new Element("rightGoal");
        if(StringUtils.isNotBlank(homeResult)){
            rightGoal.addContent(homeResult);
        }
        else{
            rightGoal.addContent("0");
        }
        onecontent.addContent(rightGoal);

        Element leftGoal = new Element("leftGoal");
        if(StringUtils.isNotBlank(awayResult)){
            leftGoal.addContent(awayResult);
        }
        else{
            leftGoal.addContent("0");
        }
        onecontent.addContent(leftGoal);

        Element num = new Element("num");
        num.addContent(episode.getLivePlatformsSize()+"");
        onecontent.addContent(num);

        Element key = new Element("key");
        String keyStr = competition.getAbbreviation()+"#"+LeDateUtils.formatYYYY_MM_DD(date) +"#"+homeName+"VS"+awayName;
        key.addContent(keyStr);
        onecontent.addContent(key);

        Element leagueName = new Element("leagueName");
        leagueName.addContent(competition.getAbbreviation());
        onecontent.addContent(leagueName);

        Element rightName = new Element("rightName");
        rightName.addContent(awayName);
        onecontent.addContent(rightName);

        Element onlineURL = new Element("onlineURL");
        Element item = new Element("item");

        String matchUrl = "";
        matchUrl = String.format(Constants.MATCH_URL,episode.getMid())+"?ch="+CH;
        Element url = new Element("url");
        url.addContent(matchUrl);
        item.addContent(url);

        Element wiseurl = new Element("wiseurl");
        wiseurl.addContent(matchUrl.replace("sports.","m.sports."));
        item.addContent(wiseurl);

        Element source = new Element("source");
        source.addContent("乐视体育");
        item.addContent(source);

        Element liveType = new Element("liveType");
        liveType.addContent("视频直播");
        item.addContent(liveType);

        onlineURL.addContent(item);
        onecontent.addContent(onlineURL);

        Element desc = new Element("desc");
        String descStr = "";
        if(StringUtils.isNotBlank(episode.getStage())){
            descStr = competition.getAbbreviation()+episode.getStage();
        }
        else{
            descStr = competition.getAbbreviation()+episode.getRound();
        }

        if(descStr.indexOf("资格赛1")>0){
            descStr = descStr.replace("资格赛1","资格赛第1轮");
        }
        else if(descStr.equals("资格赛2")){
            descStr = descStr.replace("资格赛2","资格赛第2轮");
        }
        else if(descStr.equals("资格赛3")){
            descStr = descStr.replace("资格赛3","资格赛第3轮");

        }
        desc.addContent(descStr);
        onecontent.addContent(desc);

        return onecontent;
    }

    /**************************************   直播结束   **********************************************/


    /**************************************   集锦开始   **********************************************/

    //生成给百度wise的集锦点播xml，并且上传到ftp服务器
    public void baiduWiseHighlightsStats(Long cid, String fileHead,String CH) {
        String fileName = fileHead +"_highlights" + Constants.fileextraname+".xml";
        Boolean flag = createHighlightsXmlFile(Constants.filelocalpath+fileName,cid,CH);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadBaiduXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }
    //生成集锦xml文件
    public boolean createHighlightsXmlFile(String file,Long cid,String CH){
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
            root.setAttribute("content_method","full");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(competition.getAbbreviation());
            item.addContent(key);

            Element display = new Element("display");

            Element url = new Element("url");
            url.addContent("http://www.lesports.com");
            display.addContent(url);

            Element type = new Element("type");
            type.addContent("Matchvideo");
            display.addContent(type);

            Element data = new Element("data");

            GetEpisodesOfSeasonByMetaEntryIdParam p = new GetEpisodesOfSeasonByMetaEntryIdParam();
            p.setCid(cid);
            p.setCsid(competitionSeason.getId());
            p.setEntryId(0);
            p.setLiveTypeParam(LiveTypeParam.NOT_ONLY_LIVE);
            p.setLiveShowStatusParam(LiveShowStatusParam.findByValue(-1));


            int page = 1;
            while (true){
                List<TComboEpisode> episodes = SopsApis.getEpisodesOfSeasonByMetaEntryId(p, PageUtils.createPageParam(page,100), CallerUtils.getDefaultCaller());
                if(CollectionUtils.isEmpty(episodes)){
                    break;
                }
                for(TComboEpisode episode:episodes){
//                    LOG.info("page: {}, episodes: {} ",page,episode.getStartTime());
                    Element oneContent = createHighlightsItemElement(episode, competition,CH);
                    if(oneContent != null){
                        data.addContent(oneContent);
                    }
                }
                page ++ ;
            }

            display.addContent(data);
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

    //生成每场比赛的Element
    public Element createHighlightsItemElement(TComboEpisode episode,Competition competition,String CH) throws Exception{

        Element onecontent = new Element("item");

        Date date = LeDateUtils.parseYYYYMMDDHHMMSS(episode.getStartTime());

        Element startTimeStamp = new Element("startTimeStamp");
        startTimeStamp.addContent(date.getTime()/1000+".0");
        onecontent.addContent(startTimeStamp);

        Element url = new Element("url");
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
        url.addContent(matchUrl);
        onecontent.addContent(url);

        InternalQuery q1 = new InternalQuery();
        q1.addCriteria(new InternalCriteria("mids", "eq", episode.getMid()));
        q1.addCriteria(new InternalCriteria("clone", "eq", false));
        q1.addCriteria(new InternalCriteria("type").in(Lists.newArrayList( VideoContentType.HIGHLIGHTS)));
        q1.addCriteria(new InternalCriteria("deleted", "eq", false));
        q1.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        List<Long> highlightsIds = SopsInternalApis.getVideoIdsByQuery(q1);

//        GetRelatedVideosParam p1 = new GetRelatedVideosParam();
//        p1.setRelatedId(episode.getMid());
//        p1.setTypes(Lists.newArrayList(VideoContentType.HIGHLIGHTS));
//        List<Long> highlightsIds = SopsApis.getVideoIdsByRelatedId(p1,PageUtils.createPageParam(0,100), CallerUtils.getDefaultCaller());


        InternalQuery q2 = new InternalQuery();
        q2.addCriteria(new InternalCriteria("mids", "eq", episode.getMid()));
        q2.addCriteria(new InternalCriteria("clone", "eq", false));
        q2.addCriteria(new InternalCriteria("type").in(Lists.newArrayList( VideoContentType.RECORD)));
        q2.addCriteria(new InternalCriteria("deleted", "eq", false));
        q2.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(org.springframework.data.domain.Sort.Direction.ASC, "start_time"))));
        List<Long> playbackIds = SopsInternalApis.getVideoIdsByQuery(q2);

//        GetRelatedVideosParam p2 = new GetRelatedVideosParam();
//        p2.setRelatedId(episode.getMid());
//        p2.setTypes(Lists.newArrayList(VideoContentType.MATCH_REPORT));
//        List<Long> playbackIds = SopsApis.getVideoIdsByRelatedId(p2,PageUtils.createPageParam(0,100), CallerUtils.getDefaultCaller());
        int total = highlightsIds.size()+playbackIds.size();
        if(total <= 0){
            return null;
        }

        Element num = new Element("num");
        num.addContent(highlightsIds.size()+playbackIds.size()+"");
        onecontent.addContent(num);


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

        Element key = new Element("key");
        String keyStr = competition.getAbbreviation()+"#"+LeDateUtils.formatYYYY_MM_DD(date) +"#"+homeName+"VS"+awayName;
        key.addContent(keyStr);
        onecontent.addContent(key);

        Element leagueName = new Element("leagueName");
        leagueName.addContent(competition.getAbbreviation());
        onecontent.addContent(leagueName);

        Element isDelete = new Element("isDelete");
        isDelete.addContent("0");
        onecontent.addContent(isDelete);

        Element startTime = new Element("startTime");

        Element day = new Element("day");
        day.addContent(LeDateUtils.formatYYYY_MM_DD(date));
        startTime.addContent(day);

        Element time = new Element("time");
        time.addContent(simpleDateFormat.format(date));
        startTime.addContent(time);
        onecontent.addContent(startTime);

        if(CollectionUtils.isNotEmpty(highlightsIds)){
            Element highlights = new Element("highlights");
            List<TVideo> highlightsVideos = SopsApis.getTVideosByIds(highlightsIds,CallerUtils.getDefaultCaller());
            for(TVideo highlightsVideo:highlightsVideos){
                Element video = createHighlightsVideoItemElement(highlightsVideo,CH);
                highlights.addContent(video);
            }
            onecontent.addContent(highlights);
        }

        if(CollectionUtils.isNotEmpty(playbackIds)){
            Element playbackList = new Element("playbackList");
            List<TVideo> playbackVideos = SopsApis.getTVideosByIds(playbackIds,CallerUtils.getDefaultCaller());
            for(TVideo playbackVideo:playbackVideos){
                Element video = createHighlightsVideoItemElement(playbackVideo,CH);
                playbackList.addContent(video);
            }
            onecontent.addContent(playbackList);
        }
        return onecontent;
    }


    //生成每个视频的Element
    public Element createHighlightsVideoItemElement(TVideo video,String ch) throws Exception{

        Element onecontent = new Element("item");

        Element title = new Element("title");
        title.addContent(video.getName());
        onecontent.addContent(title);

        Element url = new Element("url");
        url.addContent("http://minisite.letv.com/tuiguang/index.shtml?vid="+video.getId()+"&typeFrom=baidu&ark=1388");
        onecontent.addContent(url);

        Element image = new Element("image");
        image.addContent(video.getImageUrl());
        onecontent.addContent(image);


        Element source = new Element("source");
        source.addContent("乐视体育");
        onecontent.addContent(source);

        Element duration = new Element("duration");
        duration.addContent(LeDateUtils.formatHHMMSS(video.getDuration()));
        onecontent.addContent(duration);

        Element wiseurl = new Element("wiseurl");
        wiseurl.addContent("http://minisite.letv.com/tuiguang/index.shtml?vid="+video.getId()+"&typeFrom=baidu&ark=1388");
        onecontent.addContent(wiseurl);

        return onecontent;
    }

    /**************************************   集锦结束   **********************************************/


    /**************************************   点播开始   **********************************************/

    //生成给百度wise的热门xml，并且上传到ftp服务器
    public void baiduWiseHotStats(Long cid, String fileHead,String CH) {
        String fileName = fileHead +"_hot" + Constants.fileextraname+".xml";
        Boolean flag = createHotXmlFile(Constants.filelocalpath+fileName,cid,CH);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadBaiduXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }

    }


    //生成集锦xml文件
    public boolean createHotXmlFile(String file,Long cid,String CH){
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
            root.setAttribute("content_method","full");
            Document Doc  = new Document(root);

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent(competition.getAbbreviation());
            item.addContent(key);

            Element display = new Element("display");

            Element url = new Element("url");
            url.addContent("http://www.lesports.com");
            display.addContent(url);

            Element type = new Element("type");
            type.addContent("HotVideo");
            display.addContent(type);

            Element data = new Element("data");




            InternalQuery q1 = new InternalQuery();
            q1.addCriteria(new InternalCriteria("cid", "eq", cid));
//            q1.addCriteria(new InternalCriteria("create_at", "gt", LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.addDay(new Date(),-3))));
            q1.addCriteria(new InternalCriteria("clone", "eq", false));
            q1.addCriteria(new InternalCriteria("type", "eq", VideoContentType.OTHER));
            q1.addCriteria(new InternalCriteria("deleted", "eq", false));
            q1.with(new org.springframework.data.domain.Sort(Lists.newArrayList(new org.springframework.data.domain.Sort.Order(Sort.Direction.DESC, "create_at"))));
            q1.with(PageUtils.createPageable(0,100));
            List<Long> hotVideoIds = SopsInternalApis.getVideoIdsByQuery(q1);
            List<TVideo> hotVideos = SopsApis.getTVideosByIds(hotVideoIds,CallerUtils.getDefaultCaller());

            if(CollectionUtils.isEmpty(hotVideos)){
                return false;
            }
            String day = hotVideos.get(0).getCreateAt().substring(0,8);
            List<TVideo> dataList = Lists.newArrayList();
            for(TVideo hotVideo:hotVideos){
                //day变换，而且数据不为空
                if(!day.equals(hotVideo.getCreateAt().substring(0,8)) && CollectionUtils.isNotEmpty(dataList)){
                    Element dayItemElement = createDayItemElement(dataList,CH,competition,day);
                    data.addContent(dayItemElement);
                    day = hotVideo.getCreateAt().substring(0,8);
                    dataList = Lists.newArrayList();
                }
                dataList.add(hotVideo);
            }
            if(CollectionUtils.isNotEmpty(dataList)){
                Element dayItemElement = createDayItemElement(dataList,CH,competition,day);
                data.addContent(dayItemElement);
            }

            display.addContent(data);
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

    //生成每个视频的Element
    public Element createDayItemElement(List<TVideo> dataList,String ch,Competition competition,String dayStr) throws Exception{
        Element onecontent = new Element("item");

        Element updateTime = new Element("updateTime");
        updateTime.addContent(LeDateUtils.formatYMDHMS(new Date()));
        onecontent.addContent(updateTime);

        Element leagueName = new Element("leagueName");
        leagueName.addContent(competition.getAbbreviation());
        onecontent.addContent(leagueName);

        Element day = new Element("day");
        day.addContent(dayStr.substring(0,4)+"-"+dayStr.substring(4,6)+"-"+dayStr.substring(6,8));
        onecontent.addContent(day);

        Element highlights = new Element("highlights");
        for(TVideo video:dataList){
            Element highlightsVideo = createHotVideoItemElement(video,ch,competition);
            highlights.addContent(highlightsVideo);
        }

        onecontent.addContent(highlights);
        return onecontent;
    }


    //生成每个视频的Element
    public Element createHotVideoItemElement(TVideo video,String ch,Competition competition) throws Exception{

        Element onecontent = new Element("item");



        Element title = new Element("title");
        title.addContent(video.getName());
        onecontent.addContent(title);

        Element url = new Element("url");
        url.addContent("http://minisite.letv.com/tuiguang/index.shtml?vid="+video.getId()+"&typeFrom=baidu&ark=1388");
        onecontent.addContent(url);

        Element image = new Element("image");
        image.addContent(video.getImageUrl());
        onecontent.addContent(image);

        Element wiseurl = new Element("wiseurl");
        wiseurl.addContent("http://minisite.letv.com/tuiguang/index.shtml?vid="+video.getId()+"&typeFrom=baidu&ark=1388");
        onecontent.addContent(wiseurl);

        Element source = new Element("source");
        source.addContent("乐视体育");
        onecontent.addContent(source);

        Element duration = new Element("duration");
        duration.addContent(LeDateUtils.formatHHMMSS(video.getDuration()));
        onecontent.addContent(duration);

        Element releaseTime = new Element("releaseTime");
        releaseTime.addContent(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(video.getCreateAt())));
        onecontent.addContent(releaseTime);

        Element clickTimes = new Element("clickTimes");
        clickTimes.addContent("1000");
        onecontent.addContent(clickTimes);

        Element vid = new Element("vid");
        vid.addContent(video.getId()+"");
        onecontent.addContent(vid);



        return onecontent;
    }

    /**************************************   点播结束   **********************************************/



}
