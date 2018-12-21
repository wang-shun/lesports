package com.lesports.sms.cooperation.service.baidu;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.api.common.*;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.api.service.GetCurrentEpisodesParam;
import com.lesports.sms.api.service.GetRelatedVideosParam;
import com.lesports.sms.api.service.GetTeamsOfSeasonParam;
import com.lesports.sms.api.service.LiveShowStatusParam;
import com.lesports.sms.api.vo.*;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import com.lesports.utils.PlayApis;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by zhonglin on 2016/7/25.
 */
@Service("baiduVideoCompetition")
public class BaiduVideoCompetition {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduVideoCompetition.class);
    private static final String MATCH_URL = "http://sports.le.com/match/%s.html";
    private static final String VIDEO_URL = "http://sports.le.com/video/%s.html";
    private static final String CSL_BAIDU_CH = "v_baidu_csl";
    private static final String START_TIME = "20160101000000";

    //生成给百度视频的中超球队xml，并且上传到ftp服务器
    public void baiduVideoCompetition() {
        String fileName = "baiduVideoCompetition"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filebaidurootpath ,fileName);
        LOG.info("baiduVideoCompetition begin path:{}",Constants.fileolyrootpath + fileName);
        //生成文件成功上传文件
//        if(flag){
//            XmlUtil.uploadXmlFile(fileName, Constants.filebaidurootpath, Constants.filebaiduuploadpath);
//        }
//        else{
//            LOG.error("ftpXmlFile error file: {}" ,fileName);
//        }
    }

    //定时生成最热的视频
    public boolean createXmlFile(String path,String file) {
        LOG.info("createVideoCompetitionXml begin");
        try {
            //视频列表
            long totalBeginTime = System.currentTimeMillis();
            List<String> fileList = Lists.newArrayList();
            int fileSize = 1;
            List<TVideo> videoList = Lists.newArrayList();


            Element root = new Element("urlset");
            Document document = new Document(root);

            CallerParam caller = CallerUtils.getDefaultCaller();
            PageParam page = PageUtils.createPageParam(1,20);

            TCompetition competition = SbdsApis.getTCompetitionById(Constants.CSL_COMPETITION_ID,caller);
            if(competition == null){
                return false;
            }
            TCompetitionSeason competitionSeason = SbdsApis.getLatestTCompetitionSeasonsByCid(Constants.CSL_COMPETITION_ID, caller);
            if(competitionSeason == null){
                return false;
            }

            int curPage = 0;
            GetRelatedVideosParam p1 = new GetRelatedVideosParam();
            p1.setRelatedId(competition.getId());


            boolean loopFlag = true;

            while(loopFlag){
                page = PageUtils.createPageParam(curPage,100);
                List<Long> ids = SopsApis.getVideoIdsByRelatedId(p1, page, caller);
                if(CollectionUtils.isEmpty(ids)){
                    if(curPage!=1){
                        Element competitionElement = createCompetitionElement(competition, competitionSeason,caller ,videoList);
                        if(competitionElement != null){
                            root.addContent(competitionElement);
                            BaiduXmlUtil.createAndUploadXmlFile(document,"baiduVideoCompetition"+ Constants.fileextraname,"" ,fileSize, fileList,Constants.fileuploadflag);
                        }
                    }
                    break;
                }
                List<TVideo> videos = SopsApis.getTVideosByIds(ids,caller);
                LOG.info("curPage: " + curPage);
                for(TVideo video:videos){

                    if(video.getCreateAt().compareTo(START_TIME)<0){
                        loopFlag = false;
                        Element competitionElement = createCompetitionElement(competition, competitionSeason,caller ,videoList);
                        if(competitionElement != null){
                            root.addContent(competitionElement);
                            BaiduXmlUtil.createAndUploadXmlFile(document,"baiduVideoCompetition"+ Constants.fileextraname,"" ,fileSize, fileList,Constants.fileuploadflag);
                        }
                        break;
                    }
                    videoList.add(video);
                }
                if(videoList.size()>=1000){
                    Element competitionElement = createCompetitionElement(competition, competitionSeason,caller,videoList);
                    if(competitionElement != null){
                        root.addContent(competitionElement);
                        BaiduXmlUtil.createAndUploadXmlFile(document,"baiduVideoCompetition"+ Constants.fileextraname,"" ,fileSize, fileList,Constants.fileuploadflag);
                    }
                    //重新开始
                    root = new Element("urlset");
                    document = new Document(root);
                    videoList = Lists.newArrayList();
                    fileSize ++;
                }
                curPage ++;
            }

            BaiduXmlUtil.createAndUploadIndexXmlFile(fileList, "v.api.le.com","baiduVideoCompetitionIndex"+ Constants.fileextraname,Constants.fileuploadflag);


//            File existPath = new File(path);
//            if(!existPath.exists()){
//                XmlUtil.createPath(path);
//            }
//
//            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
//            XMLOut.output(document, new FileOutputStream(path + file));

        } catch (Exception e) {
            LOG.error("createVideoCompetitionXml error", e);
            return false;
        }

        LOG.info("createVideoCompetitionXml end");
        return  true;
    }


    //生成赛事的Element
    public Element createCompetitionElement(TCompetition competition,TCompetitionSeason competitionSeason,CallerParam caller,List<TVideo> videoList) throws Exception{

        Element url = new Element("url");

        Element loc = new Element("loc");
        loc.addContent("http://sports.le.com/soccerc/csl/");
        url.addContent(loc);

        Element lastmod = new Element("lastmod");
        lastmod.addContent(LeDateUtils.formatYYYY_MM_DD(new Date()));
        url.addContent(lastmod);

        Element changefreq = new Element("changefreq");
        changefreq.addContent("always");
        url.addContent(changefreq);

        Element priority = new Element("priority");
        priority.addContent("1.0");
        url.addContent(priority);

        Element data = new Element("data");
        Element display = new Element("display");
        Element serialinfo  = new Element("serialinfo");

        Element site = new Element("site");
        site.addContent("sports.le.com");
        serialinfo.addContent(site);

        Element leagueUrl = new Element("leagueUrl");
        leagueUrl.addContent("http://sports.le.com/soccerc/csl?ch=v_baidu_csl");
        serialinfo.addContent(leagueUrl);

        Element leaguelogoUrl = new Element("leaguelogoUrl");
        leaguelogoUrl.addContent("http://i1.letvimg.com/lc02_sms/201509/09/15/54/cl47001_2/11.png");
        serialinfo.addContent(leaguelogoUrl);

        Element leaguelogoRatio = new Element("leaguelogoRatio");
        leaguelogoRatio.addContent("100*100");
        serialinfo.addContent(leaguelogoRatio);

        Element title = new Element("title");
        title.addContent("中国足球协会超级联赛");
        serialinfo.addContent(title);

        Element shortTitle = new Element("shortTitle");
        shortTitle.addContent("中超");
        serialinfo.addContent(shortTitle);

        Element nation = new Element("nation");
        nation.addContent("中国");
        serialinfo.addContent(nation);

        Element brief = new Element("brief");
        brief.addContent(competition.getIntroduction());
        serialinfo.addContent(brief);

        PageParam page = PageUtils.createPageParam(0,10);
        GetCurrentEpisodesParam p = new GetCurrentEpisodesParam();
        p.setLiveShowStatusParam(LiveShowStatusParam.LIVE_END);
        p.setCsid(competitionSeason.getId());
        Sort sort = new Sort();
        sort.addToOrders(new Order("start_time", Direction.ASC));
        page.setSort(sort);
        List<TComboEpisode> episodes = SopsApis.getCurrentEpisodes(p, page, caller);

        Element lastgame = new Element("lastgame");
        lastgame.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(episodes.get(0).getStartTime())));
        serialinfo.addContent(lastgame);

        Element sport = new Element("sport");
        sport.addContent("足球");
        serialinfo.addContent(sport);

        Element state = new Element("state");
        state.addContent("亚洲");
        serialinfo.addContent(state);


        Element resourceTime = new Element("resourceTime");
        resourceTime.addContent(LeDateUtils.formatYMDHMS(new Date()));
        serialinfo.addContent(resourceTime);

        Element is_delete = new Element("is_delete");
        is_delete.addContent("0");
        serialinfo.addContent(is_delete);

        Element reserved1 = new Element("reserved1");
        reserved1.addContent("");
        serialinfo.addContent(reserved1);

        Element reserved2 = new Element("reserved2");
        reserved2.addContent("");
        serialinfo.addContent(reserved2);


        for(TVideo video:videoList){
            Element videoElement = createVideoElement(video,caller,competitionSeason);
            if(videoElement != null){
                serialinfo.addContent(videoElement);
            }
        }



        display.addContent(serialinfo);
        data.addContent(display);
        url.addContent(data);
        return url;
    }

    //生成每个视频的的Element
    public Element createVideoElement(TVideo  video ,CallerParam caller,TCompetitionSeason competitionSeason) throws Exception{

        Element videoElement = new Element("video");

        Element videoUrl = new Element("videoUrl");
        videoUrl.addContent(String.format(VIDEO_URL,video.getId())+"?ch="+CSL_BAIDU_CH);
        videoElement.addContent(videoUrl);

        TMatch match = null;
        if(CollectionUtils.isNotEmpty(video.getMids())){
            if(video.getContentType().equals(VideoContentType.HIGHLIGHTS) || (video.getContentType().equals(VideoContentType.RECORD) && video.getDuration()>5400)){
                for(Long mid:video.getMids()){
                    match = SbdsApis.getTMatchById(mid,caller);
                    if(match != null && match.getCid() == Constants.CSL_COMPETITION_ID){
                        break;
                    }
                    else{
                        match = null;
                    }
                }
            }

        }


        Element isSchedule = new Element("isSchedule");
        if(match != null){
            isSchedule.addContent("1");
        }
        else{
            isSchedule.addContent("0");
        }
        videoElement.addContent(isSchedule);

        if(match != null) {
            String homeScore = "";
            String awayScore = "";
            String homeTeamName = "";
            String awayTeamName = "";
            String homeTeamLogo = "";
            String awayTeamLogo = "";
            for(TCompetitor competitor:match.getCompetitors()){
                if(competitor.getGround().equals(GroundType.HOME)){
                    homeScore = competitor.getFinalResult();
                    homeTeamName = competitor.getName();
                    homeTeamLogo = competitor.getLogoUrl();
                }
                if(competitor.getGround().equals(GroundType.AWAY)){
                    awayScore = competitor.getFinalResult();
                    awayTeamName = competitor.getName();
                    awayTeamLogo = competitor.getLogoUrl();
                }
            }
            Element highlightsUrl = new Element("highlightsUrl");
            highlightsUrl.addContent(String.format(MATCH_URL,match.getId())+"?ch="+CSL_BAIDU_CH);
            videoElement.addContent(highlightsUrl);

            Element seasonName = new Element("seasonName");
            seasonName.addContent(competitionSeason.getSeason());
            videoElement.addContent(seasonName);

            Element gameTime = new Element("gameTime");
            gameTime.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime())));
            videoElement.addContent(gameTime);

            Element gameRounds = new Element("gameRounds");
            gameRounds.addContent(match.getRound().replace("第","").replace("轮",""));
            videoElement.addContent(gameRounds);

            Element gameScores = new Element("gameScores");
            gameScores.addContent(homeScore + ":" + awayScore);
            videoElement.addContent(gameScores);

            Element gameTeam = new Element("gameTeam");
            gameTeam.addContent(homeTeamName+"$$"+awayTeamName);
            videoElement.addContent(gameTeam);

            Element team1logoUrl = new Element("team1logoUrl");
            team1logoUrl.addContent(homeTeamLogo.replace(".png","/11.png"));
            videoElement.addContent(team1logoUrl);

            Element team1logoRatio = new Element("team1logoRatio");
            team1logoRatio.addContent("195*195");
            videoElement.addContent(team1logoRatio);

            Element team2logoUrl = new Element("team2logoUrl");
            team2logoUrl.addContent(awayTeamLogo.replace(".png","/11.png"));
            videoElement.addContent(team2logoUrl);

            Element team2logoRatio = new Element("team2logoRatio");
            team2logoRatio.addContent("195*195");
            videoElement.addContent(team2logoRatio);
        }

        Element videoType = new Element("videoType");
        if(video.getContentType().equals(VideoContentType.RECORD) && video.getDuration()>5400){
            videoType.addContent("回放");
        }
//        else if(video.getContentType().equals(VideoContentType.FEATURE)){
//            videoType.addContent("正片");
//        }
        else if(video.getContentType().equals(VideoContentType.HIGHLIGHTS)){
            videoType.addContent("集锦");
        }
//        else if(video.getContentType().equals(VideoContentType.MATCH_REPORT)){
//            videoType.addContent("战报");
//        }
        else{
            videoType.addContent("其他");
        }
        videoElement.addContent(videoType);

        Element imgUrl = new Element("imgUrl");
        imgUrl.addContent(video.getImageUrl());
        videoElement.addContent(imgUrl);

        Element imgRatio = new Element("imgRatio");
        imgRatio.addContent("400*225");
        videoElement.addContent(imgRatio);

        Element title = new Element("title");
        title.addContent(video.getName());
        videoElement.addContent(title);

        Element videoTag = new Element("videoTag");
        String tagStr = "" ;
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(video.getTags())){
            for(TTag tag:video.getTags()){
                TTag newTag = SopsApis.getTTagById(tag.getId(),caller);
                if(newTag == null) continue;
                if(StringUtils.isBlank(tagStr)){
                    tagStr = newTag.getName();
                }
                else{
                    tagStr += "$$" + newTag.getName();
                }
            }
        }
        videoTag.addContent(tagStr);
        videoElement.addContent(videoTag);

        Element length = new Element("length");
        length.addContent(video.getDuration()+"");
        videoElement.addContent(length);

        Element subtitle = new Element("subtitle");
        subtitle.addContent(video.getName());
        videoElement.addContent(subtitle);

        Element updateTime = new Element("updateTime");
        updateTime.addContent(LeDateUtils.formatYMDHMS(LeDateUtils.parseYYYYMMDDHHMMSS(video.getCreateAt())));
        videoElement.addContent(updateTime);

        Element watchNum = new Element("watchNum");
//        watchNum.addContent(PlayApis.getPlayNum(video.getId() + "") + "");
        watchNum.addContent("0");
        videoElement.addContent(watchNum);

        Element updateStatus = new Element("updateStatus");
        updateStatus.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(video.getCreateAt())));
        videoElement.addContent(updateStatus);

        Element brief = new Element("brief");
        brief.addContent(video.getDesc());
        videoElement.addContent(brief);

        Element team = new Element("team");
        List<TTeam> teams = SbdsApis.getTTeamsByIds(Lists.newArrayList(video.getTids()),caller);
        String teamStr = "";
        for(TTeam tTeam:teams){
            if(StringUtils.isBlank(teamStr)){
                teamStr = tTeam.getNickname();
            }
            else{
                teamStr += "$$" + tTeam.getNickname();
            }
        }
        team.addContent(teamStr);
        videoElement.addContent(team);

        Element flag_dead = new Element("flag_dead");
        flag_dead.addContent("0");
        videoElement.addContent(flag_dead);

        Element reserved3 = new Element("reserved3");
        reserved3.addContent("");
        videoElement.addContent(reserved3);

        Element reserved4 = new Element("reserved4");
        reserved4.addContent("");
        videoElement.addContent(reserved4);

        return videoElement;
    }
}
