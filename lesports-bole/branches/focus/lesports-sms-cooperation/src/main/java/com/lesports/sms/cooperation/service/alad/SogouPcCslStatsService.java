package com.lesports.sms.cooperation.service.alad;

import client.SopsApis;
import com.google.common.collect.Lists;
import com.lesports.LeConstants;
import com.lesports.api.common.*;
import com.lesports.api.common.Sort;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.*;
import com.lesports.sms.api.service.GetEpisodesOfSeasonByMetaEntryIdParam;
import com.lesports.sms.api.service.GetRecommendParam;
import com.lesports.sms.api.service.GetRelatedNewsParam;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TCompetitor;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.LeDateUtils;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/6/20.
 */
@Service("sogouPcCslStatsService")
public class SogouPcCslStatsService {

    private static final Logger LOG = LoggerFactory.getLogger(SogouPcCslStatsService.class);

    //生成给sogou的赛程xml，并且上传到ftp服务器
    public void sogouCslScheduleStats() {

        String fileName = "sogou2016csl_schedule"+Constants.fileextraname+".xml";
        Boolean flag = createScheduleXmlFile(Constants.filelocalpath + fileName);
        //生成文件成功上传文件
        if(flag && Constants.fileuploadflag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }

    //生成给sogou的积分榜xml，并且上传到ftp服务器
    public void sogouCslScoreStats() {

        String fileName = "sogou2016csl_score"+Constants.fileextraname+".xml";
        Boolean flag = createScoreXmlFile(Constants.filelocalpath + fileName);
        //生成文件成功上传文件
        if(flag && Constants.fileuploadflag){
            XmlUtil.uploadXmlFile(fileName, Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }


    //生成给sogou的射手榜xml，并且上传到ftp服务器
    public void sogouCslShotStats() {

        String fileName = "sogou2016csl_shot"+Constants.fileextraname+".xml";
        Boolean flag = createShotXmlFile(Constants.filelocalpath + fileName);
        //生成文件成功上传文件
        if(flag && Constants.fileuploadflag){
            XmlUtil.uploadXmlFile(fileName,  Constants.filelocalpath, Constants.fileuploadpath);
        }
        else{
            LOG.error("ftpXmlFile error file: {}" ,fileName);
        }
    }


    //生成schedule xml文件
    public boolean createScheduleXmlFile(String file){
        try{

            long cid = Constants.CSL_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }

            //当前轮次
            int currentRound = competitionSeason.getCurrentRound();
            LOG.info("currentRound: " +currentRound);
            long csid = competitionSeason.getId();

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            //战报的tagId
            TTag tTag = SopsApis.getTTagByName("战报", CallerUtils.getDefaultCaller());
            if(tTag == null) return false;

            PageParam pageParam = new PageParam();
            pageParam.setCount(20);
            pageParam.setPage(0);
            Sort sort = new Sort();
            sort.setOrders(Lists.newArrayList(new Order("start_time", Direction.ASC)));
            pageParam.setSort(sort);

            CallerParam callerParam = new CallerParam();
            callerParam.setCallerId(LeConstants.LESPORTS_PC_CALLER_CODE);
            callerParam.setLanguage(LanguageCode.ZH_CN);
            callerParam.setCountry(CountryCode.CN);
            List<Integer> rounds = Lists.newArrayList();
            if(currentRound>2 && currentRound<30){
                rounds = Lists.newArrayList(currentRound-1,currentRound,currentRound+1);
            }
            else if(currentRound<2){
                rounds = Lists.newArrayList(1,2,3);
                currentRound = 1;
            }
            else{
                rounds = Lists.newArrayList(28,29,30);
                currentRound = 30;
            }
            int j = 0;
            for(int round :rounds){
                j++;
                DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("第" + round + "轮", Constants.ROUND_PARENT_KD);
                if(dictEntry == null){
                    LOG.info("dictEntry name:{}, parentId:{}  is null" ,"第" + round + "轮",Constants.ROUND_PARENT_KD);
                }
                GetEpisodesOfSeasonByMetaEntryIdParam p = new GetEpisodesOfSeasonByMetaEntryIdParam();
                p.setCid(cid);
                p.setCsid(csid);
                p.setEntryId(dictEntry.getId());
                List<TComboEpisode> episodes = SopsApis.getEpisodesOfSeasonByMetaEntryId(p, pageParam, callerParam);

                for(TComboEpisode episode:episodes){
//                    LOG.info("tComboEpisode: {} , mid: {} , time: {}", episode.getName(),episode.getMid(), episode.getStartTime());
                    Match match = SbdsInternalApis.getMatchById(episode.getMid());
                    Element oneconten = createItemElement(match, episode,round,currentRound,j, tTag.getId());
                    root.addContent(oneconten);
                }
            }

            File path = new File(file);
            if(!path.exists()){
                XmlUtil.createPath(file);
            }

            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("statsCal createXmlFile  error", e);
            return false;
        }
        return true;
    }



    //生成每场比赛的Element
    public Element createItemElement(Match match,TComboEpisode episode,int round,int currentRound,int order,long tagId) {
        Element item = new Element("item");
        Element key = new Element("key");
        key.addContent(new CDATA(match.getId()+""));
        item.addContent(key);


        Element display = new Element("display");

        Element title = new Element("title");
        title.addContent(new CDATA("2016-2017中超_球队积分榜_乐视体育_乐视网"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://csl.lesports.com/"));
        display.addContent(url);

        Element showurl = new Element("showurl");
        showurl.addContent(new CDATA("www.lesports.com"));
        display.addContent(showurl);

        Element date = new Element("date");
        date.addContent(new CDATA("${sogouToday}"));
        display.addContent(date);

        Element name = new Element("name");
        name.addContent(new CDATA("中超"));
        display.addContent(name);

        Element tabname = new Element("tabname");
        tabname.addContent(new CDATA("第"+round+"轮"));
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
        timeorder.addContent(new CDATA(match.getStartTime().substring(4,12)));
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

        List<TCompetitor> competitors = episode.getCompetitors();
        TCompetitor homeCompetitor = null,awayCompetitor = null;

        for(TCompetitor competitor:competitors){
            if (competitor.getGround().equals(GroundType.HOME)) {
                homeCompetitor = competitor;
            } else if (competitor.getGround().equals(GroundType.AWAY)) {
                awayCompetitor = competitor;
            }
        }

        if(homeCompetitor == null || awayCompetitor == null) return null;

        Element p1 = new Element("p1");

        Element p1name = new Element("p1name");
        p1name.addContent(new CDATA(homeCompetitor.getName()));
        p1.addContent(p1name);

        String logo1 = homeCompetitor.getLogoUrl();
        int pos1 = logo1.lastIndexOf(".");
        logo1 = logo1.substring(0,pos1) +"/11_112_112"+logo1.substring(pos1, logo1.length());
        Element p1icon = new Element("p1icon");
        p1icon.addContent(new CDATA(logo1));
        p1.addContent(p1icon);

//        Element p1icon_b = new Element("p1icon_b");
//        p1icon_b.addContent(new CDATA("")).setText("");
//        p1.addContent(p1icon_b);

        Element p1link = new Element("p1link");
        p1link.addContent(new CDATA("http://www.lesports.com/team/"+homeCompetitor.getId()+".html?ch=sogou_csl"));
        p1.addContent(p1link);



        Element p1score = new Element("p1score");
        if(StringUtils.isNotBlank(homeCompetitor.getFinalResult())){
            p1score.addContent(new CDATA(homeCompetitor.getFinalResult()));
        }
        else{
            p1score.addContent(new CDATA("0"));
        }
        p1.addContent(p1score);

        display.addContent(p1);

        Element p2 = new Element("p2");

        Element p2name = new Element("p2name");
        p2name.addContent(new CDATA(awayCompetitor.getName()));
        p2.addContent(p2name);

        String logo2 = homeCompetitor.getLogoUrl();
        int pos2 = logo2.lastIndexOf(".");
        logo2 = logo2.substring(0,pos2) +"/11_112_112"+logo2.substring(pos2, logo2.length());
        Element p2icon = new Element("p2icon");
        p2icon.addContent(new CDATA(logo2));
        p2.addContent(p2icon);

//        Element p2icon_b = new Element("p2icon_b");
//        p2icon_b.addContent(new CDATA("")).setText("");
//        p2.addContent(p2icon_b);

        Element p2link = new Element("p2link");
        p2link.addContent(new CDATA("http://www.lesports.com/team/"+awayCompetitor.getId()+".html?ch=sogou_csl"));
        p2.addContent(p2link);

        Element p2score = new Element("p2score");
        if(StringUtils.isNotBlank(awayCompetitor.getFinalResult())){
            p2score.addContent(new CDATA(awayCompetitor.getFinalResult()));
        }
        else{
            p2score.addContent(new CDATA("0"));
        }
        p2.addContent(p2score);

        display.addContent(p2);

        Element oncur = new Element("oncur");
        if(round == currentRound ){
            oncur.addContent(new CDATA("1"));
        }
        else{
            oncur.addContent(new CDATA("0"));
        }
        display.addContent(oncur);

        Element live = new Element("live");

        Element livename = new Element("livename");
        if(LiveShowStatus.LIVE_NOT_START.equals(episode.getStatus()) || LiveShowStatus.LIVING.equals(episode.getStatus())){
            livename.addContent(new CDATA("直播"));
        }
        else{
            livename.addContent(new CDATA("回放"));
        }
        live.addContent(livename);

        Element livelink = new Element("livelink");
        livelink.addContent(new CDATA("http://sports.le.com/match/"+match.getId()+".html?ch=sogou_csl"));
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

        Pageable pageable = PageUtils.createPageable(0, 20);
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(InternalCriteria.where("mids").is(match.getId()));
        internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
        internalQuery.addCriteria(InternalCriteria.where("online").is(OnlineStatus.ONLINE));
        internalQuery.addCriteria(InternalCriteria.where("tag_ids").is(tagId));
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
        groupname.addContent(new CDATA("第" + round + "轮"));
        group.addContent(groupname);

        Element groupnum = new Element("groupnum");
        groupnum.addContent(new CDATA(order+""));
        group.addContent(groupnum);

        display.addContent(group);

        Element  more = new Element("more");
        Element morename = new Element("morename");
        morename.addContent(new CDATA("全部赛程"));
        more.addContent(morename);

        Element morelink = new Element("morelink");
        morelink.addContent(new CDATA("http://www.lesports.com/column/csl2016/csl-schedule/index.shtml?ch=sogou_csl"));
        more.addContent(morelink);
        display.addContent(more);

        item.addContent(display);
        return item;
    }


    //生成积分排行榜的xml
    public boolean createScoreXmlFile(String file) {
        try{
            long cid = Constants.CSL_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }


            long csid = competitionSeason.getId();
            List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORE_ID);
            if(CollectionUtils.isEmpty(scoreTopList)){
                LOG.error("score toplist is  null cid:{}", cid);
                return false;
            }

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            List<TopList.TopListItem> topListItems = scoreTopList.get(0).getItems();
            topListItems = topListItems.subList(0,10);
            for(TopList.TopListItem topListItem:topListItems){
                Element item = createScoreItemtElement(topListItem);
                root.addContent(item);
            }

            File path = new File(file);
            if(!path.exists()){
                XmlUtil.createPath(file);
            }

            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("csl score   createXmlFile  error", e);
            return false;
        }
        return true;
    }

    //生成射手榜的xml
    public boolean createShotXmlFile(String file) {
        try{
            long cid = Constants.CSL_COMPETITION_ID;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if(competitionSeason == null){
                LOG.error("competitionSeason is  null cid:{}", cid);
                return false;
            }


            long csid = competitionSeason.getId();
            List<TopList> scoreTopList = SbdsInternalApis.getTopListsByCsidAndType(csid, Constants.TOPLIST_SCORER_ID);
            if(CollectionUtils.isEmpty(scoreTopList)){
                LOG.error("score toplist is  null cid:{}", cid);
                return false;
            }

            Element root = new Element("DOCUMENT");
            Document Doc  = new Document(root);

            List<TopList.TopListItem> topListItems = scoreTopList.get(0).getItems();
            topListItems = topListItems.subList(0,10);
            for(TopList.TopListItem topListItem:topListItems){
                Element item = createShotItemtElement(topListItem);
                root.addContent(item);
            }

            File path = new File(file);
            if(!path.exists()){
                XmlUtil.createPath(file);
            }

            XMLOutputter XMLOut = new XMLOutputter(FormatXML());
            XMLOut.output(Doc, new FileOutputStream(file));
        }
        catch (Exception e){
            LOG.error("csl shot   createXmlFile  error", e);
            return false;
        }
        return true;

    }

    //生成积分榜每个球队的Element
    public Element createScoreItemtElement(TopList.TopListItem topListItem) throws Exception{
        Element item = new Element("item");

        Element key = new Element("key");
        key.addContent(new CDATA(topListItem.getCompetitorId()+""));
        item.addContent(key);
        Team team = SbdsInternalApis.getTeamById(topListItem.getCompetitorId());
        Element display = new Element("display");

        Element title = new Element("title");
        title.addContent(new CDATA("2016-2017中超_球队积分榜_乐视体育_乐视网"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://www.lesports.com/team/"+team.getId()+".html?ch=sogou_csl"));
        display.addContent(url);

        Element showurl = new Element("showurl");
        showurl.addContent(new CDATA("www.lesports.com"));
        display.addContent(showurl);

        Element date = new Element("date");
        date.addContent(new CDATA("${sogouToday}"));
        display.addContent(date);

        Element name = new Element("name");
        name.addContent(new CDATA("中超"));
        display.addContent(name);

        Element tabname = new Element("tabname");
        tabname.addContent(new CDATA("积分榜"));
        display.addContent(tabname);

        Element rank = new Element("rank");
        rank.addContent(new CDATA(topListItem.getRank()+""));
        display.addContent(rank);

        Element player = new Element("player");

        Element playername = new Element("playername");
        playername.addContent(new CDATA(""));
        player.addContent(playername);

        Element playerlink = new Element("playerlink");
        playerlink.addContent(new CDATA(""));
        player.addContent(playerlink);
        display.addContent(player);

        Element teamElement = new Element("team");

        Element teamname = new Element("teamname");
        teamname.addContent(new CDATA(team.getName()));
        teamElement.addContent(teamname);

        Element teamlink = new Element("teamlink");
        teamlink.addContent(new CDATA("http://www.lesports.com/team/"+team.getId()+".html?ch=sogou_csl"));
        teamElement.addContent(teamlink);
        display.addContent(teamElement);

        Element games = new Element("games");

        Map<String,Object> stats = topListItem.getStats();

        Element total = new Element("total");
        total.addContent(new CDATA(stats.get("matchNumber").toString()));
        games.addContent(total);

        Element win = new Element("win");
        win.addContent(new CDATA(stats.get("winMatch").toString()));
        games.addContent(win);

        Element ever = new Element("ever");
        ever.addContent(new CDATA(stats.get("flatMatch").toString()));
        games.addContent(ever);

        Element lose = new Element("lose");
        lose.addContent(new CDATA(stats.get("lossMatch").toString()));
        games.addContent(lose);

        display.addContent(games);

        Element score1 = new Element("score1");
        score1.addContent(new CDATA(stats.get("goalDiffer").toString()));
        display.addContent(score1);


        Element score2 = new Element("score2");
        score2.addContent(new CDATA(stats.get("teamScore").toString()));
        display.addContent(score2);

        Element group = new Element("group");
        group.addContent(new CDATA(""));
        display.addContent(group);

        Element  more = new Element("more");
        Element morename = new Element("morename");
        morename.addContent(new CDATA("全部赛程"));
        more.addContent(morename);

        Element morelink = new Element("morelink");
        morelink.addContent(new CDATA("http://www.lesports.com/column/csl2016/csl-schedule/index.shtml?ch=sogou_csl"));
        more.addContent(morelink);
        display.addContent(more);

        item.addContent(display);
        return item;
    }

    //生成射手榜每个球员的Element
    public Element createShotItemtElement(TopList.TopListItem topListItem) throws Exception{
        Element item = new Element("item");

        Element key = new Element("key");
        key.addContent(new CDATA(topListItem.getCompetitorId()+""));
        item.addContent(key);
        Player player = SbdsInternalApis.getPlayerById(topListItem.getCompetitorId());
        Team team = SbdsInternalApis.getTeamById(topListItem.getTeamId());
        Element display = new Element("display");

        Element title = new Element("title");
        title.addContent(new CDATA("2016-2017中超_球队积分榜_乐视体育_乐视网"));
        display.addContent(title);

        Element url = new Element("url");
        url.addContent(new CDATA("http://www.lesports.com/team/"+team.getId()+".html?ch=sogou_csl"));
        display.addContent(url);

        Element showurl = new Element("showurl");
        showurl.addContent(new CDATA("www.lesports.com"));
        display.addContent(showurl);

        Element date = new Element("date");
        date.addContent(new CDATA("${sogouToday}"));
        display.addContent(date);

        Element name = new Element("name");
        name.addContent(new CDATA("中超"));
        display.addContent(name);

        Element tabname = new Element("tabname");
        tabname.addContent(new CDATA("射手榜"));
        display.addContent(tabname);

        Element rank = new Element("rank");
        rank.addContent(new CDATA(topListItem.getRank()+""));
        display.addContent(rank);

        Element playerElement = new Element("player");

        Element playername = new Element("playername");
        playername.addContent(new CDATA(player.getName()+""));
        playerElement.addContent(playername);

        Element playerlink = new Element("playerlink");
        playerlink.addContent(new CDATA("http://www.lesports.com/player/"+player.getId()+".html?ch=sogou_csl"));
        playerElement.addContent(playerlink);
        display.addContent(playerElement);

        Element teamElement = new Element("team");

        Element teamname = new Element("teamname");
        teamname.addContent(new CDATA(team.getName()));
        teamElement.addContent(teamname);

        Element teamlink = new Element("teamlink");
        teamlink.addContent(new CDATA("http://www.lesports.com/team/"+team.getId()+".html?ch=sogou_csl"));
        teamElement.addContent(teamlink);
        display.addContent(teamElement);


        Map<String,Object> stats = topListItem.getStats();

        Element score1 = new Element("score1");
        score1.addContent(new CDATA(stats.get("penaltyNumber").toString()));
        display.addContent(score1);


        Element score2 = new Element("score2");
        score2.addContent(new CDATA(stats.get("goals").toString()));
        display.addContent(score2);


        Element  more = new Element("more");
        Element morename = new Element("morename");
        morename.addContent(new CDATA("全部赛程"));
        more.addContent(morename);

        Element morelink = new Element("morelink");
        morelink.addContent(new CDATA("http://www.lesports.com/column/csl2016/csl-schedule/index.shtml?ch=sogou_csl"));
        more.addContent(morelink);
        display.addContent(more);

        item.addContent(display);
        return item;
    }

    public Format FormatXML(){
        Format format = Format.getCompactFormat();
        format.setEncoding("utf-8");
        format.setIndent(" ");
        return format;
    }
}
