package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdCompetitionInternalApis;
import com.lesports.qmt.sbd.api.common.ScopeType;
import com.lesports.qmt.sbd.model.*;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerStanding;
import com.lesports.sms.data.process.impl.DefaultTopListProcessor;
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

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultPlayerStanding.PlayerStandings playerStandings = (DefaultPlayerStanding.PlayerStandings) object;
            CompetitionSeason currentSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid);
            if (currentSeason == null) {
                LOG.warn("COMPETITIONSEASON:{} MISSED,playstanding,type:", csid, playerStandings.getType());
                return false;
            }
            Competition currentCompetition = SbdCompetitionInternalApis.getCompetitionById(currentSeason.getCid());
            TopList curentTopList = convertModelToTopList(partnerType, currentCompetition, csid, playerStandings);
            if (curentTopList == null) {
                LOG.warn("adaptor topList fail");
                return false;
            }
            topListProcessor.process(curentTopList);
            return true;
        } catch (Exception e) {
            LOG.warn("adaptor topList fail", e);
        }
        return false;
    }

    private TopList convertModelToTopList(PartnerType type, Competition competition, Long csid, DefaultPlayerStanding.PlayerStandings playerStandings) {

        List<TopList.TopListItem> itemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(playerStandings.getItems())) return null;
        int i = 0;
        for (DefaultPlayerStanding.PlayerStandingItem item : playerStandings.getItems()) {
            i++;
            TopList.TopListItem topListItem = new TopList.TopListItem();
            Player player = SbdPlayerInternalApis.getPlayerByPartner(getPartner(item.getPartnerId(), type));
            if (player == null) continue;
            topListItem.setCompetitorId(player.getId());
            topListItem.setCompetitorType(CompetitorType.PLAYER);
            topListItem.setRank(item.getRank() == null ? i : item.getRank());
            topListItem.setStats(item.getStats());
            itemList.add(topListItem);
        }
        TopList topList = new TopList();
        topList.setScopeId(100009000L);
        topList.setScopeType(ScopeType.CONFERENCE);
        topList.setCompetitorType(CompetitorType.PLAYER);
        topList.setType(playerStandings.getType());
        topList.setCid(competition.getId());
        topList.setCsid(csid);
        topList.setItems(itemList);
        topList.setAllowCountries(competition.getAllowCountries());
        topList.setOnlineLanguages(competition.getOnlineLanguages());
        topList.setDeleted(false);
        return topList;
    }

}
