package com.lesports.sms.cooperation.service.copaAmerican;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Player;
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
@Service("BaiduPlayerRankingService")
public class Baidu_player_rankingGenerator extends AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(Baidu_player_rankingGenerator.class);

    public String getFilePath() {
        String filePath = "playerranking_mobile_copaAmerican2016.xml";
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
        Element root2 = createElement("Test",null);
        Element root3 = createElement("Test"," ");
        item.addContent(getItemElement());
        item.addContent(getItemElement());
        item.addContent(getDisplayElement());
        return item;
    }

    private Element getDisplayElement() {
        Element displayElement = createRooElement("display");
        displayElement.addContent(createElement("url", goalRankingUrl+baidu_logo));
        displayElement.addContent(getTabPlayerRankElement());
        return displayElement;
    }


    //添加球员排行榜
    private Element getTabPlayerRankElement() {
        Element tabElement3 = createRooElement("tab");
        tabElement3.addContent(createElement("type", "bestplayer"));
        tabElement3.addContent(createElement("tab_name", "球员榜"));
        Element tabInnner = createRooElement("tab_inner");
        tabInnner.addContent(createElement("inner_name", "今日最佳"));
        updateElement(tabInnner, getPlayerRanksListElements());
        tabElement3.addContent(tabInnner);
        return tabElement3;
    }


    //players列表
    private List<Element> getPlayerRanksListElements() {
        List<Element> ranksLit = Lists.newArrayList();
        Element ranksListElement = createRooElement("ranklist");
        ranksListElement.addContent(createElement("rankname", "射手榜"));
        ranksListElement.addContent(createElement("rankurl", goalRankingUrl+baidu_logo));
        List<TopList> topList = SbdsInternalApis.getTopListsByCsidAndType(csid, 100160000L);
        if (CollectionUtils.isNotEmpty(topList)) {
            updateElement(ranksListElement, getRankPlayersListElements(topList.get(0).getItems()));
        }
        ranksLit.add(ranksListElement);

        Element ranksListElement2 = createRooElement("ranklist");
        ranksListElement2.addContent(createElement("rankname", "助攻榜"));
        ranksListElement2.addContent(createElement("rankurl", assistRankingUrl+baidu_logo));
        List<TopList> topList2 = SbdsInternalApis.getTopListsByCsidAndType(csid, 100161000L);
        if (CollectionUtils.isNotEmpty(topList2)) {
            updateElement(ranksListElement2, getRankPlayersListElements(getOrderedTopListItems(topList2.get(0))));
        }
        ranksLit.add(ranksListElement2);
        return ranksLit;
    }

    private List<Element> getRankPlayersListElements(List<TopList.TopListItem> players) {
        if (CollectionUtils.isEmpty(players)) return null;
        List<Element> ranksLit = Lists.newArrayList();
        int count=0;
        for (TopList.TopListItem currentPlayer : players) {
            if (currentPlayer == null) continue;
            if(count>=5)break;
            Player player = SbdsInternalApis.getPlayerById(currentPlayer.getCompetitorId());
            Team team = SbdsInternalApis.getTeamById(currentPlayer.getTeamId());
            Element ranksListElement = createRooElement("list");
            ranksListElement.addContent(createElement("img_player", getPlayerUrl(player.getImageUrl(),player.getImageUrlLocal())));
            ranksListElement.addContent(createElement("name_player", player.getName()));
            ranksListElement.addContent(createElement("position", player.getPositionName()));
            ranksListElement.addContent(createElement("team", team.getName()));
            ranksListElement.addContent(createElement("score", currentPlayer.getStats().get("goals") != null ? currentPlayer.getStats().get("goals").toString() : currentPlayer.getStats().get("assists").toString()));
            ranksLit.add(ranksListElement);
            count++;
        }
        return ranksLit;
    }

}
