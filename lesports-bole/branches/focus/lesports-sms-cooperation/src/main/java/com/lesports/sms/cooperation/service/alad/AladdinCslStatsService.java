package com.lesports.sms.cooperation.service.alad;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhonglin on 2016/3/4.
 */
@Service("aladdinCslStatsService")
public class AladdinCslStatsService {
    private static final Logger LOG = LoggerFactory.getLogger(AladdinCslStatsService.class);


    //生成给阿拉丁的赛程xml，并且上传到ftp服务器
    public void statsCal() {

        //查询中超的比赛
        Pageable pageable = PageUtils.createPageable(0,300);
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(InternalCriteria.where("cid").is(Constants.CSL_COMPETITION_ID));
        internalQuery.addCriteria(InternalCriteria.where("csid").is(Constants.CSL_COMPETITION_SEASON_ID));
        internalQuery.setPageable(pageable);

        List<Match> matches = SbdsInternalApis.getMatchsByQuery(internalQuery);
        if (CollectionUtils.isEmpty(matches)) {
            LOG.info("work done. matches  is null");
            return;
        }

        //按时间倒序
        List<Match> newMtaches = new ArrayList<>(matches);
        Collections.sort(newMtaches, new Comparator<Match>() {
            @Override
            public int compare(Match o1, Match o2) {
                return LeDateUtils.parseYYYYMMDDHHMMSS(o1.getStartTime()).compareTo(LeDateUtils.parseYYYYMMDDHHMMSS(o2.getStartTime()));
            }
        });

        //开始生成xml
        String fileName = "2016csl.xml";
        String path = "E:\\";

//            String fileName = "2016csl.xml";
//            String path = "//hd//";

        Boolean flag = createXmlFile(newMtaches,path+fileName);

        //生成文件成功上传文件
        if(flag){
            XmlUtil.uploadXmlFile(fileName, path, "//hd//");
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }



    //生成xml文件
    public boolean createXmlFile(List<Match> matches,String file){
        try{
            Element root = new Element("urlset");
            Document Doc  = new Document(root);
            for (com.lesports.sms.model.Match match : matches) {
                try {
                    Element matchElement = createMatchElement(match);
                    if(matchElement==null){
                        LOG.error("fail to  createMatchElement : mid{}", match.getId());
                        continue;
                    }
                    root.addContent(matchElement);

                } catch (Exception e) {
                    LOG.error("fail to  statsCal : mid{}", match.getId());
                    continue;
                }
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
    public Element createMatchElement(Match match) throws Exception{
        Element url = new Element("url");

        Element loc = new Element("loc");
        loc.addContent("http://sports.le.com/match/"+match.getId()+".html?ch=alad");
        url.addContent(loc);

        Element data = new Element("data");
        Element display = new Element("display");

        Element matchName = new Element("match_name");
        matchName.addContent("中超");
        display.addContent(matchName);

        Element matchId = new Element("match_id");
        DictEntry dictEntry = SbdsInternalApis.getDictById(match.getRound());
        if(dictEntry !=null){
            Pattern p=Pattern.compile("\\d+");
            Matcher m=p.matcher(dictEntry.getName());
            if (m.find()){
                matchId.addContent( m.group());
            }
        }
        display.addContent(matchId);

        Element startTime = new Element("start_time");
        Date startTimeDate = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());
        startTime.addContent(LeDateUtils.formatYMDHMS(startTimeDate));
        display.addContent(startTime);

        Element endTime = new Element("end_time");
        Date endTimeDate = LeDateUtils.addHour(startTimeDate, 2);
        endTime.addContent(LeDateUtils.formatYMDHMS(endTimeDate));
        display.addContent(endTime);

        Element vsTeams = new Element("vs_teams");
        String homeTeamName = "",awayTeamName = "";
        for (Match.Competitor competitor : match.getCompetitors()) {
            if (competitor.getGround().equals(GroundType.HOME)) {
                Team homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                if(homeTeam != null)homeTeamName = homeTeam.getName();
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                Team awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                if(awayTeam != null)awayTeamName = awayTeam.getName();
            }
        }
        vsTeams.addContent(homeTeamName+" VS "+awayTeamName);
        display.addContent(vsTeams);

        Element videoId = new Element("video_id");
        if(Constants.getAladdinCode(match.getId().toString())!=null)videoId.addContent(Constants.getAladdinCode(match.getId().toString()));
        else return null;
        display.addContent(videoId);

        Element source = new Element("source");
        source.addContent("乐视网");
        display.addContent(source);

        Element videoLink = new Element("video_link");
        videoLink.addContent("http://sports.le.com/match/"+match.getId()+".html?ch=alad");
        display.addContent(videoLink);

        Element status = new Element("status");
        status.addContent("免费");
        display.addContent(status);

        data.addContent(display);
        url.addContent(data);

        return url;
    }

}
