package com.lesports.sms.cooperation.service.alad;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.FtpUtil;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by zhonglin on 2016/4/12.
 */
@Service("oneboxMLBStatsService")
public class OneboxMLBStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(OneboxMLBStatsService.class);
    //MLB赛事id
    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");


    //生成给360的赛程xml，并且上传到ftp服务器
    public void oneboxMlbStats() {
        String fileName = "onebox2016mlb-test.xml";
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
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.MLB_COMPETITION_ID);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", Constants.MLB_COMPETITION_ID);
                return false;
            }

            //当前轮次
            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = createItemElement();
            root.addContent(item);


            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }

    public Element createItemElement() throws Exception{
        Element item = new Element("item");


        Element key = new Element("key");
        key.addContent(new CDATA("MLB"));
        item.addContent(key);

        Element display = new Element("display");
        item.addContent(display);

        Element title = new Element("title");
        title.addContent(new CDATA("MLB视频_MLB直播_MLB美国职业棒球大联盟_乐视体育"));
        display.addContent(title);

        Element mTitle = new Element("m_title");
        mTitle.addContent(new CDATA("MLB2016赛季_乐视体育"));
        display.addContent(mTitle);

        Element url = new Element("url");
        url.addContent(new CDATA("http://mlb.lesports.com/"));
        display.addContent(url);

        Element mUrl = new Element("m_url");
        mUrl.addContent(new CDATA("http://sports.le.com/kzt/4_mlb2016/"));
        display.addContent(mUrl);

        Element liveInfo = new Element("liveinfo");
        Element img = new Element("img");
        img.addContent(new CDATA("http://i0.letvimg.com/lc07_iscms/201604/11/16/49/9505cff33215496da3c1619bca62c1c5.png"));
        liveInfo.addContent(img);

        Element detail = new Element("detail");
        Element name = new Element("name");
        name.addContent(new CDATA("播出日期"));
        detail.addContent(name);
        Element value = new Element("value");
        value.addContent(new CDATA("2016-04-04到2016-11-01"));
        detail.addContent(value);
        liveInfo.addContent(detail);

        detail = new Element("detail");
        name = new Element("name");
        name.addContent(new CDATA("直播媒体"));
        detail.addContent(name);
        value = new Element("value");
        value.addContent(new CDATA("乐视体育"));
        detail.addContent(value);
        liveInfo.addContent(detail);
        display.addContent(liveInfo);

        Element taglist = new Element("taglist");
        Element tag = new Element("tag");
        Element text = new Element("text");
        text.addContent(new CDATA("专题首页"));
        tag.addContent(text);
        url = new Element("url");
        url.addContent(new CDATA("http://mlb.lesports.com/"));
        tag.addContent(url);
        taglist.addContent(tag);

        tag = new Element("tag");
        text = new Element("text");
        text.addContent(new CDATA("全程回顾"));
        tag.addContent(text);
        url = new Element("url");
        url.addContent(new CDATA("http://sports.le.com/kzt/4_mlb2016/"));
        tag.addContent(url);
        taglist.addContent(tag);

        display.addContent(taglist);

        Element button = new Element("button");
        text = new Element("text");
        Element liveUrl = new Element("liveurl");
        Element mLiveUrl = new Element("m_liveurl");

        text.addContent(new CDATA("直播入口"));
        button.addContent(text);
        liveUrl.addContent(new CDATA("http://mlb.lesports.com/"));
        button.addContent(liveUrl);
        mLiveUrl.addContent(new CDATA("http://m.lesports.com/schedule.html"));
        button.addContent(mLiveUrl);
        display.addContent(button);

        Element tabArea = new Element("tab_area");

        int days = 0;
        int size = 0;
        String currentTabStr = "";
        Date now = new Date();

        //计算前一天的
        while (size<1){
            days --;
            Date date = LeDateUtils.addDay(now,days);
            String startDate = LeDateUtils.formatYYYYMMDD(date);

            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.MLB_COMPETITION_ID));
            internalQuery.addCriteria(InternalCriteria.where("start_date").is(startDate));
            List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);
//            internalQuery.with(PageUtils.createPageable(0, 20));


            LOG.info("startDate: {} " ,startDate + " size: " + episodes.size());
            if(CollectionUtils.isEmpty(episodes)){
                continue;
            }

            size ++;
            String currentDate = simpleDateFormat.format(date);
            String[] times = currentDate.split("-");
            String time = times[0]+"月"+times[1]+"日";
            Element tabVs = createTabVs(episodes,time);
            tabArea.addContent(tabVs);
        }

        days = 0;
        size = 0;

        //后面两天
        while(size<2){
            Date date = LeDateUtils.addDay(now,days);
            String startDate = LeDateUtils.formatYYYYMMDD(date);

            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.MLB_COMPETITION_ID));
            internalQuery.addCriteria(InternalCriteria.where("start_date").is(startDate));
            List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);

            LOG.info("startDate: {} " ,startDate + " size: " + episodes.size());
            days ++;
            if(CollectionUtils.isEmpty(episodes)){
                continue;
            }

            size ++;
            String currentDate = simpleDateFormat1.format(date);
            String[] times = currentDate.split("-");
            String time = times[0]+"月"+times[1]+"日";
            if(StringUtils.isBlank(currentTabStr)){
                currentTabStr = time;
            }
            Element tabVs = createTabVs(episodes,time);
            tabArea.addContent(tabVs);
        }


        Element currentTab = new Element("current_tab");
        currentTab.addContent(new CDATA(currentTabStr));
        display.addContent(currentTab);

        display.addContent(tabArea);

        Element showUrl = new Element("showurl");
        showUrl.addContent(new CDATA("http://sports.le.com/mlb/"));
        display.addContent(showUrl);
        Element source = new Element("source");
        source.addContent(new CDATA("乐视体育"));
        display.addContent(source);
        return item;
    }

    //生成每个轮次的Element
    public Element createTabVs(List<Episode> episodes,String time) throws Exception{
        Element tabVs = new Element("tab_vs");

        Element name = new Element("name");
        name.addContent(new CDATA(time));
        tabVs.addContent(name);

        Element title = new Element("title");
        Element li = new Element("li");
        li.addContent(new CDATA("状态"));
        title.addContent(li);
//        li = new Element("li");
//        li.addContent(new CDATA("日期"));
//        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("时间"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("对阵信息"));
        title.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("赛事观看"));
        title.addContent(li);
        tabVs.addContent(title);


        for(Episode episode:episodes){
            Element oneconten = createOneContentElement(episode);
            if(oneconten == null) continue;
            tabVs.addContent(oneconten);
        }
        return tabVs;
    }

    //生成每场比赛的Element
    public Element createOneContentElement(Episode episode) throws Exception{
        Element onecontent = new Element("onecontent");
        Element status = new Element("status");
        Match match = SbdsInternalApis.getMatchById(episode.getMid());
        if(match == null) return  null;
        status.addContent(new CDATA(match.getStatus().getValue()+""));
        onecontent.addContent(status);

        Date startTime = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());

//        Element date = new Element("date");
//        date.addContent(new CDATA(LeDateUtils.formatMM_DD(startTime)));
//        onecontent.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA(simpleDateFormat1.format(startTime)));
        onecontent.addContent(time);

        Element teamVs = new Element("team_vs");
        Set<Match.Competitor> competitors = match.getCompetitors();

        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeResult = competitor.getFinalResult();
                homeId = competitor.getCompetitorId();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayResult = competitor.getFinalResult();
                awayId = competitor.getCompetitorId();
            }
        }

        Team awayTeam = SbdsInternalApis.getTeamById(awayId);
        if(awayTeam == null) return  null;
        Element team1 = new Element("team1");
        team1.addContent(new CDATA(Constants.getShortName(awayId)));
        teamVs.addContent(team1);
        Element team1url = new Element("team1url");
        team1url.addContent(new CDATA(Constants.getMlbTeamUrl(awayId)));
        teamVs.addContent(team1url);

        Team homeTeam = SbdsInternalApis.getTeamById(homeId);
        if(homeTeam == null) return  null;
        Element team2 = new Element("team2");
        team2.addContent(new CDATA(Constants.getShortName(homeId)));
        teamVs.addContent(team2);
        Element team2url = new Element("team2url");
        team2url.addContent(new CDATA(Constants.getMlbTeamUrl(homeId)));
        teamVs.addContent(team2url);



        Element score = new Element("score");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            score.addContent(new CDATA(awayResult+"-"+homeResult));
        }
        else{
            score.addContent(new CDATA("VS"));
        }
        teamVs.addContent(score);
        onecontent.addContent(teamVs);

        //没有直播的
        if(match.getDeleted()){
            Element link = new Element("link");
            Element linkText = new Element("linktext");
            linkText.addContent(new CDATA("暂无直播"));
            link.addContent(linkText);
            Element linkUrl = new Element("linkurl");
            linkUrl.addContent(new CDATA(""));
            link.addContent(linkUrl);

            Element mLinkUrl = new Element("m_linkurl");
            mLinkUrl.addContent(new CDATA(""));
            link.addContent(mLinkUrl);
            onecontent.addContent(link);
        }
        else{
            Element link = new Element("link");
            Element linkText = new Element("linktext");
            linkText.addContent(new CDATA(""));
            link.addContent(linkText);
            Element linkUrl = new Element("linkurl");
            linkUrl.addContent(new CDATA("http://sports.le.com/match/"+episode.getMid()+".html?ch=onebox"));
            link.addContent(linkUrl);

            Element mLinkUrl = new Element("m_linkurl");
            mLinkUrl.addContent(new CDATA("http://sports.le.com/match/"+episode.getMid()+".html?ch=onebox"));
            link.addContent(mLinkUrl);
            onecontent.addContent(link);
        }



        return onecontent;
    }

    public Format FormatXML(){
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }


}
