package com.lesports.sms.cooperation.service.baidu;

import client.SopsApis;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.TTag;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.PageUtils;
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

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/9/19.
 */
@Service("baiduLiveMatch")
public class BaiduLiveMatch {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduLiveMatch.class);
    private static final String MATCH_URL = "http://sports.le.com/match/%s.html";
    private static final String LIVE_CH = "baiduzhibo_csl_live";

    //生成给百度的直播数据xml
    public void baiduLiveStats() {

        String fileName = "baiduLive"+ Constants.fileextraname+".xml";
        Boolean flag = createXmlFile(Constants.filelocalpath+fileName);

        //生成文件成功上传文件x
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
            Element root = new Element("root");
            Document Doc  = new Document(root);

            Element type = new Element("type");
            type.addContent("Match");
            root.addContent(type);

            Element data = new Element("data");

            long cid = Constants.CSL_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }

            int curPage = 0;
            while (true) {
                Pageable pageable = PageUtils.createPageable(curPage,100);
                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
//                internalQuery.addCriteria(InternalCriteria.where("status").is("MATCH_NOT_START"));
                internalQuery.addCriteria(InternalCriteria.where("allow_countries").is("CN"));
                internalQuery.addCriteria(InternalCriteria.where("csid").is(competitionSeason.getId()));
                internalQuery.setPageable(pageable);
                internalQuery.setSort(new Sort(Sort.Direction.ASC, "start_time"));
                List<Match> matches = SbdsInternalApis.getMatchsByQuery(internalQuery);

                LOG.info("curPage: {} , csid:{} , size: {} ",curPage,competitionSeason.getId(),matches.size());

                //翻到最后一页了
                if (org.springframework.util.CollectionUtils.isEmpty(matches)) {
                    break;
                }

                for (Match match : matches) {
                    Element item = createOneContentElement(match);
                    if(item != null){
                        data.addContent(item);
                    }
                }
                //翻页
                curPage++;
            }

            root.addContent(data);
            XMLOutputter XMLOut = new XMLOutputter(XmlUtil.FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }


    //每个album的Element
    public static Element createOneContentElement(Match match) throws Exception{
        Element item = new Element("item");

        Set<Match.Competitor> competitors = match.getCompetitors();

        Team homeTeam = null;
        Team awayTeam = null;
        String homeScore = "0";
        String awayScore = "0";
        int zhangYuSize = 0;

        for(Match.Competitor competitor:competitors){
            if(competitor.getGround().equals(GroundType.HOME)){
                homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                if(StringUtils.isNotBlank(competitor.getFinalResult())){
                    homeScore = competitor.getFinalResult();
                }
            }
            else{
                awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
                if(StringUtils.isNotBlank(competitor.getFinalResult())){
                    awayScore = competitor.getFinalResult();
                }
            }
        }

        if(homeTeam == null || awayTeam == null){
            return  null;
        }

        Element delay = new Element("delay");
        delay.addContent("False");
        item.addContent(delay);

        Element videoURL = new Element("videoURL");
        videoURL.addContent(String.format(MATCH_URL,match.getId()));
        item.addContent(videoURL);

        Element rightGoal = new Element("rightGoal");
        rightGoal.addContent(awayScore);
        item.addContent(rightGoal);

        Element leftName = new Element("leftName");
        leftName.addContent(homeTeam.getName());
        item.addContent(leftName);

        Date startDate = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());

        Element startTimeStamp = new Element("startTimeStamp");
        startTimeStamp.addContent(startDate.getTime()/1000+".0");
        item.addContent(startTimeStamp);

        Element key = new Element("key");
        key.addContent("中超#"+LeDateUtils.formatYYYY_MM_DD(startDate)+"#"+homeTeam.getName()+"vs"+awayTeam.getName());
        item.addContent(key);

        Element leftGoal = new Element("leftGoal");
        leftGoal.addContent(homeScore);
        item.addContent(leftGoal);

        Element num = new Element("num");
        num.addContent(1+zhangYuSize+"");
        item.addContent(num);

        Element startTime = new Element("startTime");

        Element day = new Element("day");
        day.addContent(LeDateUtils.formatYYYY_MM_DD(startDate));
        startTime.addContent(day);

        Element time = new Element("time");
        time.addContent(LeDateUtils.formatYMDHMS(startDate).substring(10    ));
        startTime.addContent(time);

        item.addContent(startTime);

        Element leagueName = new Element("leagueName");
        leagueName.addContent("中超");
        item.addContent(leagueName);

        Element isDelete = new Element("isDelete");
        isDelete.addContent("0");
        item.addContent(isDelete);

        Element rightName = new Element("rightName");
        rightName.addContent(awayTeam.getName());
        item.addContent(rightName);

        Element onlineURL = new Element("onlineURL");

        Element itemElement = new Element("item");

        Element url = new Element("url");
        url.addContent(String.format(MATCH_URL,match.getId())+"?ch="+LIVE_CH);
        itemElement.addContent(url);

        Element source = new Element("source");
        source.addContent("乐视视频");
        itemElement.addContent(source);

        Element liveType = new Element("liveType");
        liveType.addContent("视频直播");
        itemElement.addContent(liveType);
        onlineURL.addContent(itemElement);
        item.addContent(onlineURL);

        return  item;
    }
}
