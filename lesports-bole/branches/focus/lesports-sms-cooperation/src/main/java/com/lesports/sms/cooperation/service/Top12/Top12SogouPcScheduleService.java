package com.lesports.sms.cooperation.service.Top12;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.*;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.CDATA;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/8/23.
 */
@Service("top12SogouPcScheduleService")
public class Top12SogouPcScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(Top12SogouPcScheduleService.class);
    private static final String MATCH_URL = "http://sports.letv.com/match/%s.html";
    private static final String TEAM_URL = "http://www.lesports.com/team/%s.html";
    private static final String TOP12_CH = "sogou_12";

    //生成给360的赛程xml，并且上传到ftp服务器
    public void top12PcStats() {
        String fileName = "sogou2016top12"+ Constants.fileextraname+".xml";
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
                LOG.error("competitionSeason is  null cid:{}", Constants.EPL_COMPETITION_ID);
                return false;
            }

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);




            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("csid", "eq", competitionSeason.getId()));
            q.addCriteria(new InternalCriteria("competitors.competitor_id", "eq", Constants.CHINA_FOOTBALL_TEAM_ID));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.setSort(new Sort(Sort.Direction.ASC, "start_time"));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);

            int i = 1;
            for(Match match:matches){
                Element item = createItemElement(match, i);
                if(item != null ){
                    root.addContent(item);
                }
                i++;
            }

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
    public Element createItemElement(Match match,int round) {
        Element item = new Element("item");
        Element key = new Element("key");
        key.addContent(new CDATA("世预赛国足赛程"+round));
        item.addContent(key);

        Element display = new Element("display");
        Element title = new Element("title");
        title.addContent(new CDATA("2018年世界杯预选赛亚洲区12强中国队赛程"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://12.lesports.com?ch="+TOP12_CH));
        display.addContent(url);

        Element showurl = new Element("showurl");
        showurl.addContent(new CDATA("www.lesports.com"));
        display.addContent(showurl);

        Element date = new Element("date");
        date.addContent(new CDATA("${sogouToday}"));
        display.addContent(date);

        Element name = new Element("name");
        name.addContent(new CDATA("世预赛"));
        display.addContent(name);

        Element tabname = new Element("tabname");
        tabname.addContent(new CDATA(match.getStartDate().substring(0,4)+"年赛程"));
        display.addContent(tabname);

        Element month = new Element("month");
        month.addContent(new CDATA(match.getStartTime().substring(4,6)));
        display.addContent(month);

        Element day = new Element("day");
        day.addContent(new CDATA(match.getStartTime().substring(6,8)));
        display.addContent(day);

        Element hour = new Element("hour");
        hour.addContent(new CDATA(match.getStartTime().substring(8,10)));
        display.addContent(hour);

        Element min = new Element("min");
        min.addContent(new CDATA(match.getStartTime().substring(10,12)));
        display.addContent(min);

        Element timeorder = new Element("timeorder");
        timeorder.addContent(new CDATA(match.getStartTime()));
        display.addContent(timeorder);

        Date startTime = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());
        String weekNick = XmlUtil.getWeekNick(startTime);

        Element week = new Element("week");
        week.addContent(new CDATA(weekNick));
        display.addContent(week);

        Element status = new Element("status");
        if(match.getStatus().equals(MatchStatus.MATCH_NOT_START)){
            status.addContent(new CDATA("未开始"));
        }
        else if(match.getStatus().equals(MatchStatus.MATCHING)){
            status.addContent(new CDATA("进行中"));
        }
        else{
            status.addContent(new CDATA("已结束"));
        }
        display.addContent(status);

        Element hot = new Element("hot");
        hot.addContent(new CDATA("1"));
        display.addContent(hot);

        Set<Match.Competitor> competitors = match.getCompetitors();
        Team homeTeam = null;
        Team awayTeam = null;
        String homeScore = "";
        String awayScore = "";

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                homeScore = competitor.getFinalResult();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                awayScore = competitor.getFinalResult();
            }
        }

        if(homeTeam == null || awayTeam == null){
            return null;
        }

        Element p1 = new Element("p1");

        Element p1name = new Element("p1name");
        p1name.addContent(new CDATA(homeTeam.getName()));
        p1.addContent(p1name);

        String logo1 = homeTeam.getLogo();
        int pos1 = logo1.lastIndexOf(".");
        logo1 = logo1.substring(0,pos1) +"/11_112_112"+logo1.substring(pos1, logo1.length());

        Element p1icon = new Element("p1icon");
        p1icon.addContent(new CDATA(logo1));
        p1.addContent(p1icon);


        Element p1link = new Element("p1link");
        p1link.addContent(new CDATA(String.format(TEAM_URL,homeTeam.getId())+"?ch="+TOP12_CH));
        p1.addContent(p1link);



        Element p1score = new Element("p1score");
        if(StringUtils.isNotBlank((homeScore))){
            p1score.addContent(new CDATA(homeScore));
        }
        else{
            p1score.addContent(new CDATA("0"));
        }
        p1.addContent(p1score);

        display.addContent(p1);

        Element p2 = new Element("p2");

        Element p2name = new Element("p2name");
        p2name.addContent(new CDATA(awayTeam.getName()));
        p2.addContent(p2name);


        String logo2 = awayTeam.getLogo();
        int pos2 = logo2.lastIndexOf(".");
        logo2 = logo2.substring(0,pos2) +"/11_112_112"+logo2.substring(pos2, logo2.length());

        Element p2icon = new Element("p2icon");
        p2icon.addContent(new CDATA(logo2));
        p2.addContent(p2icon);


        Element p2link = new Element("p2link");
        p2link.addContent(new CDATA(String.format(TEAM_URL,awayTeam.getId())+"?ch="+TOP12_CH));
        p2.addContent(p2link);

        Element p2score = new Element("p2score");
        if(StringUtils.isNotBlank(awayScore)){
            p2score.addContent(new CDATA(awayScore));
        }
        else{
            p2score.addContent(new CDATA("0"));
        }
        p2.addContent(p2score);

        display.addContent(p2);

        Element oncur = new Element("oncur");
        if(match.getStartDate().startsWith(LeDateUtils.formatYYYY(new Date()))){
            oncur.addContent(new CDATA("1"));
        }
        else{
            oncur.addContent(new CDATA("0"));
        }

        display.addContent(oncur);

        Element live = new Element("live");

        Element livename = new Element("livename");
        if(match.getStatus().equals(MatchStatus.MATCH_NOT_START) || match.getStatus().equals(MatchStatus.MATCHING)){
            livename.addContent(new CDATA("直播"));
        }
        else{
            livename.addContent(new CDATA("回放"));
        }
        live.addContent(livename);

        Element livelink = new Element("livelink");
        livelink.addContent(new CDATA(String.format(MATCH_URL,match.getId())+"?ch="+TOP12_CH));
        live.addContent(livelink);

        Element live01 = new Element("live01");
        live01.addContent(new CDATA("1"));
        live.addContent(live01);
        display.addContent(live);


        Element other = new Element("other");
        Element othername = new Element("othername");
        Element otherlink = new Element("otherlink");

//        othername.addContent(new CDATA("统计"));
//        other.addContent(othername);
//        otherlink.addContent(new CDATA("http://sports.letv.com/match/"+match.getId()+".html?ch=sogou_csl"));
//        other.addContent(otherlink);
//        display.addContent(other);

        Pageable pageable = PageUtils.createPageable(0, 5);
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(InternalCriteria.where("mids").is(match.getId()));
        internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
        internalQuery.addCriteria(InternalCriteria.where("online").is(OnlineStatus.ONLINE));
        internalQuery.addCriteria(InternalCriteria.where("tag_ids").is(Constants.REPORT_TAG_ID));
        internalQuery.setPageable(pageable);
        internalQuery.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.DESC, "create_at"));
        List<News> newses = SopsInternalApis.getNewsByQuery(internalQuery);

        other = new Element("other");
        othername = new Element("othername");
        othername.addContent(new CDATA("战报"));
        other.addContent(othername);


        otherlink = new Element("otherlink");
        if(CollectionUtils.isNotEmpty(newses)){
            otherlink.addContent(new CDATA("http://sports.le.com/news/"+newses.get(0).getId()+".html?ch=sogou_csl"));
        }
        else{
            otherlink.addContent(new CDATA(""));
        }

        other.addContent(otherlink);

        display.addContent(other);

        Element group = new Element("group");
        Element groupname = new Element("groupname");
        groupname.addContent(new CDATA(match.getStartTime().substring(0,4)+"年赛程"));
        group.addContent(groupname);

        Element groupnum = new Element("groupnum");
        if(match.getStartTime().startsWith("2016")){
            groupnum.addContent(new CDATA("1"));
        }
        else{
            groupnum.addContent(new CDATA("2"));
        }
        group.addContent(groupnum);

        display.addContent(group);

        Element  more = new Element("more");
        Element morename = new Element("morename");
        morename.addContent(new CDATA("全部赛程"));
        more.addContent(morename);

        Element morelink = new Element("morelink");
        morelink.addContent(new CDATA("http://12.lesports.com/schedule?ch="+TOP12_CH));
        more.addContent(morelink);
        display.addContent(more);

        more = new Element("more");
        morename = new Element("morename");
        morename.addContent(new CDATA("国足名单"));
        more.addContent(morename);

        morelink = new Element("morelink");
        morelink.addContent(new CDATA("http://www.lesports.com/topic/h/guozudamingdang/?ch="+TOP12_CH));
        more.addContent(morelink);
        display.addContent(more);

        item.addContent(display);
        return item;
    }



}
