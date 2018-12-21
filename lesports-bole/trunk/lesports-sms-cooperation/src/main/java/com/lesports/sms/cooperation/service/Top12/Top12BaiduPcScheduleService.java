package com.lesports.sms.cooperation.service.Top12;

import com.lesports.api.common.CountryCode;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.StatsConstants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhonglin on 2016/8/23.
 */
@Service("top12BaiduPcScheduleService")
public class Top12BaiduPcScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(Top12BaiduPcScheduleService.class);
    private static final String MATCH_URL = "http://www.lesports.com/match/%s.html";
    private static final String VIDEO_URL = "http://www.lesports.com/video/%s.html";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
    private static final String TOP12_CH = "baidu_12";

    //生成给360的赛程xml，并且上传到ftp服务器
    public void top12PcStats() {
        String fileName = "baidu2016top12"+ Constants.fileextraname+".xml";
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
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.TOP12_COMPETITION_ID);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", Constants.EPL_COMPETITION_ID);
                return false;
            }

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            Element item = new Element("item");
            Element key = new Element("key");
            key.addContent("中国队赛程");
            item.addContent(key);

            Element display = new Element("display");

            Element url = new Element("url");
            url.addContent("http://12.lesports.com?ch="+TOP12_CH);
            display.addContent(url);

            Element title = new Element("title");
            title.addContent("2018世界杯预选赛亚洲区12强赛中国队赛程");
            display.addContent(title);

            Element showCount = new Element("show_count");
            showCount.addContent("10");
            display.addContent(showCount);


            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("csid", "eq", competitionSeason.getId()));
            q.addCriteria(new InternalCriteria("competitors.competitor_id", "eq", Constants.CHINA_FOOTBALL_TEAM_ID));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.setSort(new Sort(Sort.Direction.ASC, "start_time"));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);

            for(Match match:matches){
                Element tableContent = createOneContentElement(match);
                if(tableContent != null ){
                    display.addContent(tableContent);
                }
            }

            Element bottomLink = new Element("bottom_link");
            bottomLink.setAttribute("text","完整赛程");
            bottomLink.setAttribute("url","http://www.lesports.com/topic/i/12qschedule/index.html?ch="+TOP12_CH);
            display.addContent(bottomLink);

            bottomLink = new Element("bottom_link");
            bottomLink.setAttribute("text","积分榜");
            bottomLink.setAttribute("url","http://12.lesports.com/standings?ch="+TOP12_CH);
            display.addContent(bottomLink);

            Element source = new Element("source");
            source.addContent("以上资源来自乐视体育，赛程时间均为北京时间");
            display.addContent(source);

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
    public Element createOneContentElement(Match match) throws Exception{
        Element tableContent = new Element("table_content");

        Element status = new Element("status");
        status.addContent(match.getStatus().getValue()+"");
        tableContent.addContent(status);

        Element date = new Element("date");
        date.addContent(sdf.format(LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime())));
        tableContent.addContent(date);

        Element time = new Element("time");
        time.addContent(sdf1.format(LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime())));
        tableContent.addContent(time);


        Element vsText = new Element("vs_text");
        Set<Match.Competitor> competitors = match.getCompetitors();
        Team homeTeam = null;
        Team awayTeam = null;
        String homeResult = "";
        String awayResult = "";

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                homeResult = competitor.getFinalResult();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayResult = competitor.getFinalResult();
                awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
            }
        }

        if(homeTeam == null || awayTeam == null){
            return null;
        }
        Element country1 = new Element("country1");
        country1.setAttribute("text",homeTeam.getName());
        country1.setAttribute("img",Constants.top12LogoUrlMap.get(homeTeam.getId()));
        tableContent.addContent(country1);

        Element country2 = new Element("country2");
        country2.setAttribute("text",awayTeam.getName());
        country2.setAttribute("img",Constants.top12LogoUrlMap.get(awayTeam.getId()));
        tableContent.addContent(country2);

        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            vsText.addContent(homeResult+":"+awayResult);
        }
        else{
            vsText.addContent("VS");
        }
        tableContent.addContent(vsText);

        Element links = new Element("links");
        if(match.getStatus().equals(MatchStatus.MATCH_END)) {
            links.setAttribute("text", "视频回放");
        }
        else{
            links.setAttribute("text", "乐视直播");
        }
        links.setAttribute("url",String.format(MATCH_URL,match.getId())+"?ch="+TOP12_CH);
        tableContent.addContent(links);

        links = new Element("links");


        if(match.getStatus().equals(MatchStatus.MATCH_END)) {
            Pageable pageable = PageUtils.createPageable(0,5);
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.setPageable(pageable);
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("mids").is(match.getId()));
            internalQuery.addCriteria(InternalCriteria.where("type").is(VideoContentType.HIGHLIGHTS));
            internalQuery.setSort(new Sort(Sort.Direction.DESC, "create_at"));
            List<Video> videos = SopsInternalApis.getVideosByQuery(internalQuery);
            if(CollectionUtils.isNotEmpty(videos)){
                links.setAttribute("text", "视频集锦");
                links.setAttribute("url",String.format(VIDEO_URL,videos.get(0).getId())+"?ch="+TOP12_CH);
            }
            else{
                links.setAttribute("text", "视频集锦");
                links.setAttribute("url","");
            }

        }
        else{
            links.setAttribute("text", "视频集锦");
            links.setAttribute("url","");
        }
        tableContent.addContent(links);

        return tableContent;
    }

}
