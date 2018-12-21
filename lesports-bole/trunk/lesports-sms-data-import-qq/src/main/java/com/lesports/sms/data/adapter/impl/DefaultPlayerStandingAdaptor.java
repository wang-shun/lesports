package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerStanding;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import com.lesports.sms.data.process.impl.DefaultTopListProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.TopList;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("playerStandingAdaptor")
public class DefaultPlayerStandingAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultPlayerStandingAdaptor.class);
    @Resource
    DefaultTopListProcessor topListProcessor;

    public Boolean nextProcessor(TransModel transModel, Object object) {
        try {
            DefaultPlayerStanding.PlayerStandings playerStandings = (DefaultPlayerStanding.PlayerStandings) object;
            CompetitionSeason currentSeason = SbdsInternalApis.getCompetitionSeasonById(transModel.getCsid());
            if (currentSeason == null) {
                LOG.warn("COMPETITIONSEASON:{} MISSED,playstanding,type:", transModel.getCsid(), playerStandings.getType());
                return false;
            }
            TopList curentTopList = convertModelToTopList(transModel.getCsid(), playerStandings);
            if (curentTopList == null) {
                LOG.info("the topList:{} adator is created and send to the nextProcessor", curentTopList.toString());
                LOG.warn("player Standing:{} adapter fail", playerStandings.toString());

                return false;
            }
            topListProcessor.process(curentTopList);
        } catch (Exception e) {
            LOG.error("playerStanding adatpter error", e);
        }
        return false;
    }

    private TopList convertModelToTopList(Long csid, DefaultPlayerStanding.PlayerStandings playerStandings) {

        List<TopList.TopListItem> itemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(playerStandings.getItems())) return null;
        int i = 0;
        for (DefaultPlayerStanding.PlayerStandingItem item : playerStandings.getItems()) {
            i++;
            TopList.TopListItem topListItem = new TopList.TopListItem();
            Player player = SbdsInternalApis.getPlayerByQQId(item.getPartnerId());
            if (player == null) continue;
            topListItem.setCompetitorId(player.getId());
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(item.getRank() == null ? i : item.getRank());
            topListItem.setStats(item.getStats());
            itemList.add(topListItem);
        }
        TopList topList = new TopList();
        topList.setScope(100009000L);
        topList.setScopType(ScopeType.CONFERENCE);
        topList.setCompetitorType(CompetitorType.PLAYER);
        topList.setType(playerStandings.getType());
        topList.setCsid(csid);
        topList.setItems(itemList);
        return topList;
    }

}
