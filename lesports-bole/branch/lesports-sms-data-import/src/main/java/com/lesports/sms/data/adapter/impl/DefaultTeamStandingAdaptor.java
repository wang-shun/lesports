package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.qmt.sbd.model.CompetitionSeason;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.qmt.sbd.model.Team;
import com.lesports.qmt.sbd.model.TopList;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefualtTeamStanding;
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
@Service("teamStandingAdaptor")
public class DefaultTeamStandingAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultTeamStandingAdaptor.class);
    @Resource
    DefaultTopListProcessor topListProcessor;

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefualtTeamStanding.TeamStanding teamStanding = (DefualtTeamStanding.TeamStanding) object;
            CompetitionSeason currentSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid);
            if (currentSeason == null) return false;
            TopList curentTopList = convertModelToTopList(partnerType, csid, teamStanding);
            return topListProcessor.process(curentTopList);
        } catch (Exception e) {
            LOG.error("adaptor teamStanding fail", e);
        }
        return false;
    }

    private TopList convertModelToTopList(PartnerType partnerType, Long csid, DefualtTeamStanding.TeamStanding teamStanding) {
        List<TopList.TopListItem> itemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(teamStanding.getItems())) return null;
        Integer i = 0;
        for (DefualtTeamStanding.TeamStanding.TeamStandingItem item : teamStanding.getItems()) {
            i++;
            TopList.TopListItem topListItem = new TopList.TopListItem();
            Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(item.getTeamId(), partnerType));
            topListItem.setCompetitorId(currentTeam.getId());
            topListItem.setCompetitorType(com.lesports.qmt.sbd.api.common.CompetitorType.TEAM);
            topListItem.setRank(item.getRank() == null ? i : item.getRank());
            topListItem.setStats(item.getTeamStats());
            itemList.add(topListItem);
        }
        TopList topList = new TopList();
        topList.setType(100009000L);
        topList.setScopeType(teamStanding.getScopeType());
        topList.setScopeId(teamStanding.getScopeId());
        topList.setCompetitorType(CompetitorType.TEAM);
        topList.setCsid(csid);
        topList.setItems(itemList);
        return topList;
    }

}
