package com.lesports.sms.cooperation.service.Top12;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/9/8.
 */
@Service("top12OneboxPcSingleService")
public class Top12OneboxPcSingleService {


    private static final Logger LOG = LoggerFactory.getLogger(Top12OneboxPcService.class);


    public  static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
    public  static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
    private static final String TOP12_CH = "360_12";


    //生成给360的赛程xml，并且上传到ftp服务器
    public void oneboxTop12SingleStats() {
        String fileName = "onebox2016Top12Single"+ Constants.fileextraname+".xml";
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
                LOG.error("competitionSeason is  null cid:{}", Constants.TOP12_COMPETITION_ID);
                return false;
            }

            //当前轮次
            int round = competitionSeason.getCurrentRound();
            long csid = competitionSeason.getId();
            Map<String,List<Match>> itemMap = Maps.newHashMap();



            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("csid", "eq", csid));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.addCriteria(new InternalCriteria("allow_countries", "eq", "CN"));
            q.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.ASC, "start_time"));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);

            for(Match match:matches){
                String homeName = "";
                String awayName = "";
                for(Match.Competitor competitor:match.getCompetitors()){
                    if(competitor.getGround().equals(GroundType.HOME)){
                        homeName = Constants.top12TeamMap.get(competitor.getCompetitorId());
                    }
                    else{
                        awayName = Constants.top12TeamMap.get(competitor.getCompetitorId());
                    }
                }
                if(StringUtils.isBlank(homeName) || StringUtils.isBlank(awayName)){
                    return false;
                }

                if(itemMap.get(homeName+" "+awayName)==null && itemMap.get(awayName+" "+ homeName)==null){
                    List<Match> matchList = Lists.newArrayList(match);
                    itemMap.put((homeName+" "+awayName),matchList);
                }
                else{
                    List<Match> matchList;
                    if(itemMap.get(homeName+" "+awayName)!=null){
                        matchList = itemMap.get((homeName+" "+awayName));
                    }
                    else{
                        matchList = itemMap.get((awayName+" "+homeName));
                    }
                    matchList.add(match);
                    itemMap.put((homeName+" "+awayName),matchList);
                }
            }

            for(String key:itemMap.keySet()){
                Element item = createItemElement(key,itemMap.get(key));
                if(item!=null){
                    root.addContent(item);
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

    public Element createItemElement(String keyStr,List<Match> matches) throws Exception{
        Element item = new Element("item");

        Element key = new Element("key");
        key.addContent(new CDATA(keyStr));
        item.addContent(key);

        Element display = new Element("display");
        item.addContent(display);

        Element title = new Element("title");
        title.addContent(new CDATA("2018世界杯亚洲区预选赛_乐视体育"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://12.lesports.com/"));
        display.addContent(url);


        Element tabMatch = new Element("tab_match");


        Element matchTitle = new Element("title");
        Element li = new Element("li");
        li.addContent(new CDATA("状态"));
        matchTitle.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("比赛日期"));
        matchTitle.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("时间"));
        matchTitle.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("对阵"));
        matchTitle.addContent(li);
        li = new Element("li");
        li.addContent(new CDATA("赛事观看"));
        matchTitle.addContent(li);
        tabMatch.addContent(matchTitle);

        for(Match match:matches){
            Element onecontent = createOneContentElement(match);
            if(onecontent != null){
                tabMatch.addContent(onecontent);
            }
        }
        display.addContent(tabMatch);


        Element moretext = new Element("moretext");
        moretext.addContent(new CDATA("查看世预赛全部赛程》"));
        display.addContent(moretext);

        Element moreurl = new Element("moreurl");
        moreurl.addContent(new CDATA("http://12.lesports.com/schedule?ch="+TOP12_CH));
        display.addContent(moreurl);


        Element showUrl = new Element("showurl");
        showUrl.addContent(new CDATA("12.lesports.com"));
        display.addContent(showUrl);
        return item;
    }



    //生成每场比赛的Element
    public Element createOneContentElement(Match match) throws Exception{
        Element onecontent = new Element("onecontent");
        Element status = new Element("status");
        status.addContent(new CDATA(match.getStatus().getValue()+""));
        onecontent.addContent(status);

        Date startTime = LeDateUtils.parseYYYYMMDDHHMMSS(match.getStartTime());
        Element date = new Element("date");

        String weekNick = XmlUtil.getWeekNick(startTime);

        date.addContent(new CDATA(LeDateUtils.formatYYYY_MM_DD(startTime)+weekNick));
        onecontent.addContent(date);

        Element time = new Element("time");
        time.addContent(new CDATA(simpleDateFormat1.format(startTime)));
        onecontent.addContent(time);

        Element teamVs = new Element("team_vs");
        Set<Match.Competitor> competitors = match.getCompetitors();

        String homeResult = "";
        String awayResult = "";
        Team homeTeam = null;
        Team awayTeam = null;

        for(Match.Competitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeResult = competitor.getFinalResult();
                homeTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayResult = competitor.getFinalResult();
                awayTeam = SbdsInternalApis.getTeamById(competitor.getCompetitorId());
            }
        }
        if(homeTeam == null || awayTeam == null){
            return null;
        }
        Element team1 = new Element("team1");
        team1.addContent(new CDATA(homeTeam.getName()));
        teamVs.addContent(team1);
        Element team1url = new Element("team1url");
        team1url.addContent(new CDATA(""));
//        team1url.addContent(new CDATA("http://www.lesports.com/team/"+homeTeam.getId()+".html?ch="+TOP12_CH));
        teamVs.addContent(team1url);

        Element team2 = new Element("team2");
        team2.addContent(new CDATA(awayTeam.getName()));
        teamVs.addContent(team2);
        Element team2url = new Element("team2url");
        team2url.addContent(new CDATA(""));
//        team2url.addContent(new CDATA("http://www.lesports.com/team/"+awayTeam.getId()+".html?ch="+TOP12_CH));
        teamVs.addContent(team2url);

        Element score = new Element("score");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            score.addContent(new CDATA(homeResult+"-"+awayResult));
        }
        else{
            score.addContent(new CDATA("VS"));
        }
        teamVs.addContent(score);
        onecontent.addContent(teamVs);
        Element link = new Element("link");
        Element text = new Element("text");
        if(match.getStatus().equals(MatchStatus.MATCH_END)){
            text.addContent(new CDATA("视频回放"));
        }
        else{
            text.addContent(new CDATA("视频直播"));
        }
        link.addContent(text);
        Element url = new Element("url");
        url.addContent(new CDATA("http://sports.le.com/match/"+match.getId()+".html?ch="+TOP12_CH));
        link.addContent(url);
        onecontent.addContent(link);

        return onecontent;
    }



}
