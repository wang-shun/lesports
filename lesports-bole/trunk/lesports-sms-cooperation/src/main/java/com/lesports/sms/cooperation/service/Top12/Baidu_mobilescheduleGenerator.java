package com.lesports.sms.cooperation.service.Top12;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.cooperation.service.copaAmerican.AbstractService;
import com.lesports.sms.cooperation.util.CommonUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Team;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduTop12ScheduleService")
public class Baidu_mobilescheduleGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_mobilescheduleGenerator.class);

    public String getFilePath() {
        String filePath = "scheduleandresult_mobile_WordCupTop122018.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        root.addContent(getItemElement());
        return new Document(root);
    }

    private Element getItemElement() {
        Element item = createRooElement("item");//一级目录
        item.addContent(createElement("key", "世预赛"));
        item.addContent(getDisplayElement());
        return item;
    }

    private Element getDisplayElement() {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", resourceMobileUrl+baidu_logo));
        displayElement.addContent(getTabMatchElement());
        return displayElement;
    }

    private Element getTabMatchElement() {
        //添加赛程tab
        Element tabElement = createRooElement("tab");
        tabElement.addContent(createElement("type", "match"));
        tabElement.addContent(createElement("tab_name", "赛程"));
        tabElement.addContent(createElement("selected", "true"));
        updateElement(tabElement, getVilistElements());
        return tabElement;
    }

    //赛程列表
    private List<Element> getVilistElements() {
        List<Element> Vilists = Lists.newArrayList();
        Date validDate = CommonUtil.getDataYYMMDD(top12_start_date);
        Date endDate = CommonUtil.getDataYYMMDD(top12_end_date);
        boolean i = false;
        while (validDate.before(endDate)) {
            Date validDate1 = validDate;
            validDate = CommonUtil.getNextDate(validDate);
            InternalQuery q = new InternalQuery();
            q.addCriteria(new InternalCriteria("csid", "eq", csid));
            q.addCriteria(new InternalCriteria("start_date", "eq", CommonUtil.getYYYYMMDDDate(validDate1)));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            q.with(new Sort(Lists.newArrayList(new Sort.Order(Sort.Direction.ASC, "start_time"))));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
            if (CollectionUtils.isEmpty(matches)) continue;
            Element vsListElement = createRooElement("vslist");
            String status = "0";
            Date currentDate=CommonUtil.getDataYYMMDD(CommonUtil.getYYYYMMDDDate(new Date()));
            if (!currentDate.after(validDate1) && !i) {
                status = "1";
                i = true;
            }
            vsListElement.addContent(createElement("state", status));
            vsListElement.addContent(createElement("date", getMatchDate(CommonUtil.getYYYYMMDDDate(validDate1))));
            q.addCriteria(new InternalCriteria("deleted", "eq", false));
            updateElement(vsListElement, getVsboxElements(matches));
            Vilists.add(vsListElement);
        }
        return Vilists;
    }

    private List<Element> getVsboxElements(List<Match> matchList) {
        List<Element> Vsboxes = Lists.newArrayList();
        Element vsboxtElement = createRooElement("vsbox");
        vsboxtElement.addContent(createElement("img_type", "x"));
        List<Element> Vslines = Lists.newArrayList();
        for (Match currentMatch : matchList) {
            Team homeTeam = null;
            Team awayTeam = null;
            String homeScore = "0";
            String awayScore = "0";
            Set<Match.Competitor> teams = currentMatch.getCompetitors();
            for (Match.Competitor currentCompetitor : teams) {
                Team team = SbdsInternalApis.getTeamById(currentCompetitor.getCompetitorId());
                if (currentCompetitor.getGround().equals(GroundType.HOME)) {
                    homeTeam = team;
                    homeScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? homeScore : currentCompetitor.getFinalResult();
                } else {
                    awayTeam = team;
                    awayScore = StringUtils.isBlankOrNull(currentCompetitor.getFinalResult()) ? awayScore : currentCompetitor.getFinalResult();
                }
            }
            Map<String, Object> attributes = Maps.newHashMap();
            attributes.put("ranks1", homeTeam.getName());
            attributes.put("icon1", Constants.top12LogoUrlMap.get(homeTeam.getId()));
            attributes.put("record1", "");
            attributes.put("ranks2", awayTeam.getName());
            attributes.put("icon2",  Constants.top12LogoUrlMap.get(awayTeam.getId()));
            attributes.put("record2", "");
            attributes.put("score", homeScore + "-" + awayScore);
            attributes.put("vstime", getMatchTime(currentMatch.getStartTime()));
            attributes.put("state", getmatchStatus(currentMatch.getStatus()));
            attributes.put("statetext", currentMatch.getMoment() == null ? "" : currentMatch.getMoment());
            attributes.put("url", matchPageUrl("",currentMatch.getId(),baidu_logo));
            vsboxtElement.addContent(getElementWithAttributes("vsline", attributes));
        }
        updateElement(vsboxtElement, Vslines);
        Vsboxes.add(vsboxtElement);

        return Vsboxes;
    }

    public String getMatchDate(String date) {
        if (date == null) return "";
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return month + "月" + day + "号";
    }

    public String getMatchTime(String time) {
        if (time == null) return "00:00";
        String hour = time.substring(8, 10);
        String minute = time.substring(10, 12);
        return hour + ":" + minute;
    }
}
