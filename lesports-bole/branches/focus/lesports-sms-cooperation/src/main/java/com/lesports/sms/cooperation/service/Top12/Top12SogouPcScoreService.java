package com.lesports.sms.cooperation.service.Top12;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.OnlineStatus;
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

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhonglin on 2016/8/29.
 */
@Service("top12SogouPcScoreService")
public class Top12SogouPcScoreService {

    private static final Logger LOG = LoggerFactory.getLogger(Top12SogouPcScoreService.class);
    private static final String MATCH_URL = "http://sports.letv.com/match/%s.html";
    private static final String TEAM_URL = "http://www.lesports.com/team/%s.html";
    private static final String TOP12_CH = "sogou_12";

    //生成给360的赛程xml，并且上传到ftp服务器
    public void top12ScoreStats() {
        String fileName = "sogou2016top12_score"+ Constants.fileextraname+".xml";
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



            int i = 1;
            List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), Constants.TOPLIST_SCORE_ID);
            if(CollectionUtils.isNotEmpty(scoreTopList)){
                for(TopList topList:scoreTopList){
                    if(topList.getGroup() != 100023000L) continue; //不是A租


                    List<TopList.TopListItem> topListItems = topList.getItems();
                    int size = topListItems.size();
                    if(size>10)size = 10;

                    topListItems = topListItems.subList(0,size);
                    for(TopList.TopListItem topListItem:topListItems){
                        Element item = createScoreItemtElement(topListItem,i);
                        if(item == null)continue;
                        root.addContent(item);
                        i++;
                    }
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

    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem,int i) throws Exception{
        Element item = new Element("item");

        Map<String,Object> stats = topListItem.getStats();
        Team team = SbdsInternalApis.getTeamById(topListItem.getCompetitorId());

        Element key = new Element("key");
        key.addContent(team.getName()+i);
        item.addContent(key);

        Element display = new Element("display");

        Element title = new Element("title");
        title.addContent(new CDATA("2018年世预赛亚洲区12强_积分榜"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://12.lesports.com/standings?ch="+TOP12_CH));
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
        tabname.addContent(new CDATA("积分榜"));
        display.addContent(tabname);

        Element rank = new Element("rank");
//        rank.addContent(new CDATA(topListItem.getRank()+""));
        rank.addContent(new CDATA(i+""));
        display.addContent(rank);



        Element teamElement = new Element("team");

        Element teamname = new Element("teamname");
        teamname.addContent(new CDATA(team.getName()));
        teamElement.addContent(teamname);

        Element teamlink = new Element("teamlink");
        teamlink.addContent(new CDATA(String.format(TEAM_URL,team.getId())+"?ch="+TOP12_CH));
        teamElement.addContent(teamlink);

        display.addContent(teamElement);

        Element games = new Element("games");
        String total = stats.get("matchNumber").toString();
        String win = stats.get("winMatch").toString();
        String ever = stats.get("flatMatch").toString();
        String lose = stats.get("lossMatch").toString();
        String score1 = stats.get("goalDiffer").toString();
        String score2 = stats.get("teamScore").toString();


        Element totalElement = new Element("total");
        totalElement.addContent(new CDATA(total));
        games.addContent(totalElement);

        Element winElement = new Element("win");
        winElement.addContent(new CDATA(win));
        games.addContent(winElement);

        Element everElement = new Element("ever");
        everElement.addContent(new CDATA(ever));
        games.addContent(everElement);

        Element loseElement = new Element("lose");
        loseElement.addContent(new CDATA(lose));
        games.addContent(loseElement);

        display.addContent(games);

        Element score1Element = new Element("score1");
        score1Element.addContent(new CDATA(score1));
        display.addContent(score1Element);

        Element score2Element = new Element("score2");
        score2Element.addContent(new CDATA(score2));
        display.addContent(score2Element);

        Element  more = new Element("more");
        Element morename = new Element("morename");
        morename.addContent(new CDATA("全部积分信息"));
        more.addContent(morename);

        Element morelink = new Element("morelink");
        morelink.addContent(new CDATA("http://12.lesports.com/standings?ch="+TOP12_CH));
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
