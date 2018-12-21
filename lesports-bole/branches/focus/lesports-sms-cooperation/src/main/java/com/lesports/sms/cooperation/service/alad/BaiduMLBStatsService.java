package com.lesports.sms.cooperation.service.alad;

import com.lesports.api.common.CountryCode;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.FtpUtil;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.lesports.sms.repository.EpisodeRepository;
import com.lesports.sms.service.CompetitionSeasonService;
import com.lesports.sms.service.MatchService;
import com.lesports.sms.service.TeamService;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.LeProperties;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by zhonglin on 2016/5/6.
 */
@Service("baiduMLBStatsService")
public class BaiduMLBStatsService {
    private static final Logger LOG = LoggerFactory.getLogger(BaiduMLBStatsService.class);


    //生成给百度的赛程xml，并且上传到ftp服务器
    public void baiduMlbStats() {

        String fileName = "2016mlb-test.xml";
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

            Element item = new Element("item");

            Element key = new Element("key");
            key.addContent("MLB");
            item.addContent(key);

            Element display = new Element("display");

            Element url = new Element("url");
            url.addContent("http://sports.le.com/mlb/");
            display.addContent(url);

            Element title = new Element("title");
            title.addContent("MLB视频_MLB直播_MLB美国职业棒球大联盟_乐视体育");
            display.addContent(title);

            Element showtabindex = new Element("showtabindex");
            showtabindex.addContent("1");
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

            int days = 0;
            int size = 0;
            String currentTabStr = "";
            Date now = new Date();

            //计算前一天的
            while (size<1){
                days --;
                Date date = LeDateUtils.addDay(now, days);
                String startDate = LeDateUtils.formatYYYYMMDD(date);


                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.MLB_COMPETITION_ID));
                internalQuery.addCriteria(InternalCriteria.where("start_date").is(startDate));
                List<Episode> episodes = SopsInternalApis.getEpisodesByQuery(internalQuery);

                internalQuery.with(PageUtils.createPageable(0,20));

                LOG.info("startDate: {} " ,startDate + " size: " + episodes.size());
                if(CollectionUtils.isEmpty(episodes)){
                    continue;
                }

                size ++;
                Element tab = createTab(episodes,date);
                display.addContent(tab);
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
                Element tab = createTab(episodes,date);
                display.addContent(tab);
            }

            Element showurl = new Element("showurl");
            showurl.addContent("sports.le.com/mlb");
            display.addContent(showurl);

            item.addContent(display);
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
            if(!episode.getAllowCountry().equals(CountryCode.CN)){
                continue;
            }
            Element tr = createTr(episode);
            if(tr == null) continue;
            tab.addContent(tr);
        }

        Element bottomlink = new Element("bottomlink");
        bottomlink.setAttribute("text","球队巡礼");
        bottomlink.setAttribute("url","http://www.lesports.com/topic/s/mlb30teams/");
        tab.addContent(bottomlink);

        bottomlink = new Element("bottomlink");
        bottomlink.setAttribute("text","球队积分");
        bottomlink.setAttribute("url","http://mlb.lesports.com/");
        tab.addContent(bottomlink);

        bottomlink = new Element("bottomlink");
        bottomlink.setAttribute("text","社区讨论");
        bottomlink.setAttribute("url","http://z.lesports.com/camp/40101");
        tab.addContent(bottomlink);

        return tab;
    }

    //生成每场比赛的Element
    public Element createTr(Episode episode) throws Exception{
        Element tr = new Element("tr");
        Element status = new Element("status");
        Match match = SbdsInternalApis.getMatchById(episode.getMid());
        if(match == null) return  null;
        SimpleDateFormat format = new SimpleDateFormat( "MM/dd HH:mm" );
        tr.setAttribute("time",format.format(LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime())));

        String homeResult = "";
        String awayResult = "";
        long homeId = 0;
        long awayId = 0;

        for(Match.Competitor competitor:match.getCompetitors()){
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
        Team homeTeam = SbdsInternalApis.getTeamById(homeId);
        if(homeTeam == null) return  null;


        tr.setAttribute("player1",Constants.getShortName(awayId));
        tr.setAttribute("player1logo", Constants.getMlbLogoUrl(awayId));
        tr.setAttribute("player1url",Constants.getMlbTeamUrl(awayId));

        tr.setAttribute("player2",Constants.getShortName(homeId));
        tr.setAttribute("player2logo",Constants.getMlbLogoUrl(homeId));
        tr.setAttribute("player2url",Constants.getMlbTeamUrl(homeId));

        tr.setAttribute("status",match.getStatus().getValue()+"");



        Element score = new Element("score");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            tr.setAttribute("score",awayResult+"-"+homeResult);
        }
        else{
            tr.setAttribute("score","VS");
        }


        //没有直播的
        if(match.getDeleted()){
            tr.setAttribute("link1text","视频录播");
            tr.setAttribute("link1url","");
            tr.setAttribute("link2text","比赛集锦");
            tr.setAttribute("link2url","");
        }
        else{
            tr.setAttribute("link1text","视频直播");
            tr.setAttribute("link1url","http://sports.le.com/match/"+episode.getMid()+".html?ch=alad");
            tr.setAttribute("link2text","比赛集锦");
            tr.setAttribute("link2url","");

        }
        return tr;
    }

    public Format FormatXML(){
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }


}
