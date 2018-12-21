package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Team;
import com.lesports.sms.model.TopList;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qiaohognxin on 2016/4/23.
 */
@Service("BaiduTeamRankingService")
public class Baidu_rankingGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_rankingGenerator.class);

    public String getFilePath() {
        String filePath = "teamranking_mobile_copaAmerican2016.xml";
        return filePath;
    }

    public Document getDocument() {
        Element root = createRooElement("DOCUMENT");
        root.addContent(getItemElement());
        return new Document(root);
    }

    private Element getItemElement() {
        Element item = createRooElement("item");//一级目录
        item.addContent(createElement("key", "美洲杯"));
        item.addContent(getDisplayElement());
        return item;
    }

    private Element getDisplayElement() {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", teamRankingUrl+baidu_logo));
        displayElement.addContent(getTabRankingElement());
        return displayElement;
    }

    private Element getTabRankingElement() {
        //添加球队积分榜
        Element tabElement4 = createRooElement("tab");
        tabElement4.addContent(createElement("type", "rank"));
        tabElement4.addContent(createElement("tab_name", "积分榜"));
        Element tabInnner2 = createRooElement("tab_inner");
        tabInnner2.addContent(createElement("inner_type", "teamrank"));
        tabInnner2.addContent(createElement("inner_name", "球队排名"));
        updateElement(tabInnner2, getTeamGroupRankElements());
        tabElement4.addContent(tabInnner2);
        return tabElement4;
    }

    //  team rankings列表
    private List<Element> getTeamGroupRankElements() {
        List<Element> groupRankings = Lists.newArrayList();

        groupRankings.add(createGroupElement("A"));
        groupRankings.add(createGroupElement("B"));
        groupRankings.add(createGroupElement("C"));
        groupRankings.add(createGroupElement("D"));
        return groupRankings;
    }

    private Element createGroupElement(String group) {
        Element groupA = createRooElement("group");
        groupA.addContent(getTh(group));
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, teamRankingTypeId, idMaps.get(group));
        if (CollectionUtils.isNotEmpty(topList)) {
            updateElement(groupA,getTds(topList.get(0).getItems()));
        }
        return groupA;

    }

    private List<Element> getTds(List<TopList.TopListItem> items) {
        if (CollectionUtils.isEmpty(items)) return null;
        List<Element> tds = Lists.newArrayList();
        for (TopList.TopListItem item : items) {
            Team currentTeam = SbdsInternalApis.getTeamById(item.getCompetitorId());
            Element td = createRooElement("td");
            td.addContent(createElement("img_td",logoMaps.get(currentTeam.getId())));
            td.addContent(createElement("name_td", currentTeam.getName()));
            td.addContent(createElement("con_td", item.getStats().get("matchNumber").toString()));
            td.addContent(createElement("con_td", item.getStats().get("winMatch").toString()));
            td.addContent(createElement("con_td", item.getStats().get("flatMatch").toString()));
            td.addContent(createElement("con_td", item.getStats().get("lossMatch").toString()));
            Integer goalDif=Integer.valueOf(item.getStats().get("goal").toString())-Integer.valueOf(item.getStats().get("fumble").toString());
            td.addContent(createElement("con_td", goalDif.toString()));
            td.addContent(createElement("con_td", item.getStats().get("teamScore").toString()));
            td.addContent(createElement("lift_mark", "0"));
            tds.add(td);
        }
        return tds;
    }
    private Element getTh(String group) {
        Element th = createRooElement("th");
        th.addContent(createElement("name",group+"组"));
        th.addContent(createElement("name", "场次"));
        th.addContent(createElement("name", "胜"));
        th.addContent(createElement("name", "平"));
        th.addContent(createElement("name", "负"));
        th.addContent(createElement("name", "净胜球"));
        th.addContent(createElement("name", "积分"));
        return th;
    }


}
