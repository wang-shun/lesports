package com.lesports.sms.cooperation.service.baidu;

import client.SopsApis;
import com.lesports.api.common.CallerParam;
import com.lesports.api.common.PageParam;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.api.service.*;
import com.lesports.sms.api.vo.*;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.OlyUtil;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.News;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.Video;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import com.lesports.utils.PlayApis;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by zhonglin on 2016/7/25.
 */
@Service("baiduVideoTeam")
public class BaiduVideoTeam {


    private static final Logger LOG = LoggerFactory.getLogger(BaiduVideoTeam.class);
    private static final String TEAM_URL = "http://sports.le.com/team/%s.html";
    private static final String MATCH_URL = "http://sports.le.com/match/%s.html";
    private static final String VIDEO_URL = "http://sports.le.com/video/%s.html";
    private static final String START_TIME = "20160101000000";
    private static final String CSL_BAIDU_CH = "v_baidu_csl";

    //生成给百度视频的中超球队xml，并且上传到ftp服务器
        public void baiduVideoTeam() {
        String fileName = "baiduVideoTeam"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filebaidurootpath ,fileName);
        LOG.info("sogouOlyChampionStats begin path:{}",Constants.filebaidurootpath + fileName);
//        //生成文件成功上传文件
//        if(flag){
//            XmlUtil.uploadXmlFile(fileName, Constants.filebaidurootpath, Constants.filebaiduuploadpath);
//        }
//        else{
//            LOG.error("ftpXmlFile error file: {}" ,fileName);
//        }
    }

    //定时生成最热的视频
    public boolean createXmlFile(String path,String file) {
        LOG.info("createVideoTeamXml begin");
        List<String> fileList = Lists.newArrayList();
        int fileSize = 1;

        try {
            //视频列表
            long totalBeginTime = System.currentTimeMillis();

            Element root = new Element("urlset");
            Document document = new Document(root);

            CallerParam caller = CallerUtils.getDefaultCaller();
            PageParam page = PageUtils.createPageParam(1,20);


            GetTeamsOfSeasonParam p = new GetTeamsOfSeasonParam();
            p.setCid(Constants.CSL_COMPETITION_ID);
            TCompetitionSeason competitionSeason = SbdsApis.getLatestTCompetitionSeasonsByCid(Constants.CSL_COMPETITION_ID,caller);
            if(competitionSeason == null){
                return false;
            }
            p.setCsid(competitionSeason.getId());
            List<TTeam> teams = SbdsApis.getTeamsOfSeason(p,page,caller);

            for(TTeam team:teams){
                Element teamElement = createTeamElement(team,caller,page,competitionSeason);
                if(teamElement != null){
                    root.addContent(teamElement);
                    BaiduXmlUtil.createAndUploadXmlFile(document,"baiduVideoTeam"+ Constants.fileextraname,"" ,fileSize, fileList,Constants.fileuploadflag);
                    //重新开始
                    root = new Element("urlset");
                    document = new Document(root);
                    fileSize ++;
                }
            }

            BaiduXmlUtil.createAndUploadIndexXmlFile(fileList, "v.api.le.com","baiduVideoTeamIndex"+ Constants.fileextraname,Constants.fileuploadflag);


//            File existPath = new File(path);
//            if(!existPath.exists()){
//                XmlUtil.createPath(path);
//            }
//
//            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
//            XMLOut.output(document, new FileOutputStream(path + file));

        } catch (Exception e) {
            LOG.error("createVideoTeamXml error", e);
            return false;
        }

        LOG.info("createVideoTeamXml end");
        return  true;
    }


    //生成每个球队的的Element
    public Element createTeamElement(TTeam team,CallerParam caller,PageParam page,TCompetitionSeason competitionSeason) throws Exception{
        Element url = new Element("url");
        Element loc = new Element("loc");
        loc.addContent(String.format(TEAM_URL,team.getId())+"?ch="+CSL_BAIDU_CH);
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

        Element teamUrl = new Element("teamUrl");
        teamUrl.addContent(String.format(TEAM_URL,team.getId())+"?ch="+CSL_BAIDU_CH);
        serialinfo.addContent(teamUrl);

        Element teamimgUrl = new Element("teamimgUrl");
        teamimgUrl.addContent(team.getLogoUrl().replace(".png","/11.png"));
        serialinfo.addContent(teamimgUrl);

        Element teamimgRatio = new Element("teamimgRatio");
        teamimgRatio.addContent("100*100");
        serialinfo.addContent(teamimgRatio);

        Element title = new Element("title");
        title.addContent(team.getName());
        serialinfo.addContent(title);

        Element shortTitle = new Element("shortTitle");
        shortTitle.addContent(team.getNickname());
        serialinfo.addContent(shortTitle);

        Element nation = new Element("nation");
        nation.addContent("中国");
        serialinfo.addContent(nation);

        Element city = new Element("city");
        city.addContent(team.getCity());
        serialinfo.addContent(city);

        Element brief = new Element("brief");
        brief.addContent(team.getDesc());
        serialinfo.addContent(brief);

        GetEpisodesOfSeasonByMetaEntryIdParam p = new GetEpisodesOfSeasonByMetaEntryIdParam();
        p.setCsid(competitionSeason.getId());
        p.setLiveShowStatusParam(LiveShowStatusParam.LIVE_END);
        List<TComboEpisode> episodes = SopsApis.getCurrentEpisodesByCompetitorId(team.getId(), p, page, caller);


        Element lastgame = new Element("lastgame");
        lastgame.addContent(LeDateUtils.formatYYYY_MM_DD(LeDateUtils.parseYYYYMMDDHHMMSS(episodes.get(0).getStartTime())));
        serialinfo.addContent(lastgame);

        Element sport = new Element("sport");
        sport.addContent("足球");
        serialinfo.addContent(sport);

        Element state = new Element("state");
        state.addContent("亚洲");
        serialinfo.addContent(state);

        Element leagueShortTitle = new Element("leagueShortTitle");
        leagueShortTitle.addContent("中超");
        serialinfo.addContent(leagueShortTitle);

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

        int curPage = 0;
        GetRelatedVideosParam p1 = new GetRelatedVideosParam();
        p1.setRelatedId(team.getId());

        boolean loopFlag = true;

        while(loopFlag){
            page = PageUtils.createPageParam(curPage,100);
            List<Long> ids = SopsApis.getVideoIdsByRelatedId(p1, page, caller);
            if(org.apache.commons.collections.CollectionUtils.isEmpty(ids)){
                break;
            }
            List<TVideo> videos = SopsApis.getTVideosByIds(ids,caller);
            LOG.info("tid:"+team.getId() + " curPage: " + curPage);
            for(TVideo video:videos){
                Element videoElement = createVideoElement(video,caller,competitionSeason);
                if(videoElement != null){
                    if(video.getCreateAt().compareTo(START_TIME)<0){
                        loopFlag = false;
                        break;
                    }
                    serialinfo.addContent(videoElement);
                }
            }
            curPage ++;
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
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(video.getMids())){
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
        if(match!=null){
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
