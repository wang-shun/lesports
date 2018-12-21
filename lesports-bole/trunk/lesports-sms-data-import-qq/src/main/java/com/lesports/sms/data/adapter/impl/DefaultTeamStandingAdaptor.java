package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.model.commonImpl.DefaultTeamStanding;
import com.lesports.sms.data.process.impl.DefaultTopListProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.Team;
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
@Service("teamStandingAdaptor")
public class DefaultTeamStandingAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultTeamStandingAdaptor.class);
    @Resource
    DefaultTopListProcessor topListProcessor;

    public Boolean nextProcessor(TransModel transModel, Object object) {
        try {
            DefaultTeamStanding.TeamStanding teamStanding = (DefaultTeamStanding.TeamStanding) object;
            CompetitionSeason currentSeason = SbdsInternalApis.getCompetitionSeasonById(transModel.getCsid());
            if (currentSeason == null) return false;
            TopList curentTopList = convertModelToTopList(transModel.getCsid(), teamStanding);
            if (curentTopList == null) {
                LOG.warn("topList:{},csid:{} adapter fail", teamStanding.getType(), transModel.getCsid());
                return false;
            }
            LOG.warn("team-topList:{} adpter fail,the sid:{}", curentTopList.toString());
            return topListProcessor.process(curentTopList);
        } catch (Exception e) {
            LOG.error("topList  adapter fail", e);
        }
        return false;

    }

    private TopList convertModelToTopList(Long csid, DefaultTeamStanding.TeamStanding teamStanding) {
        List<TopList.TopListItem> itemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(teamStanding.getItems())) return null;
        for (DefaultTeamStanding.TeamStanding.TeamStandingItem item : teamStanding.getItems()) {
            TopList.TopListItem topListItem = new TopList.TopListItem();

            Team currentTeam = SbdsInternalApis.getTeamByQQId(item.getTeamId().equals("5312") ? "30" : item.getTeamId());
            if (currentTeam == null) continue;
            topListItem.setCompetitorId(currentTeam.getId());
            topListItem.setCompetitorType(CompetitorType.TEAM);
            topListItem.setRank(item.getRank());
            topListItem.setStats(item.getTeamStats());
            itemList.add(topListItem);
        }
        TopList topList = new TopList();
        topList.setType(teamStanding.getType());
        topList.setScopType(teamStanding.getScopeType());
        topList.setScope(teamStanding.getScopeId());
        topList.setCompetitorType(CompetitorType.TEAM);
        topList.setCsid(csid);
        topList.setItems(itemList);
        return topList;
    }

}
