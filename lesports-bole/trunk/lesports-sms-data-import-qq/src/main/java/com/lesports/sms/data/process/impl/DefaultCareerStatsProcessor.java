package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerCareerStats;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.PlayerCareerStat;
import com.lesports.sms.model.TopList;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("playerCarreerStatsProcessor")
public class DefaultCareerStatsProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultCareerStatsProcessor.class);

    public Boolean process(Object object) {
        PlayerCareerStat newStats = (PlayerCareerStat) object;
        try {
//            InternalQuery query=new InternalQuery();
//            query.addCriteria(new InternalCriteria("pid","is",newTopList.getCsid()));
//            query.addCriteria(new InternalCriteria("tid","is",newTopList.getType()));
//            query.addCriteria(new InternalCriteria("cid","is",newTopList.getScope()));
//            query.addCriteria(new InternalCriteria("competitor_type","is",newTopList.getCompetitorType()));
//            List<TopList> topLists= SbdsInternalApis.getTopListsByQuery(query);
            PlayerCareerStat playerCareerStat = SbdsInternalApis.getPlayerCareerStatByPidAndCid(newStats.getPlayerId(), newStats.getCid());
            if (null == playerCareerStat) {
                SbdsInternalApis.savePlayerCareerStat(newStats);
                return true;
            }
            List<String> configs = getUpdateProperty();
            if (CollectionUtils.isEmpty(configs)) {
                LOG.warn("NO pemission to Update property");
                return false;
            }
            playerCareerStat = (PlayerCareerStat) updateObject(configs, playerCareerStat, newStats);
            if (SbdsInternalApis.savePlayerCareerStat(playerCareerStat) > 0) {
                LOG.warn("Player Season Stats:{} is updated", playerCareerStat.getPlayerId());
            }
            return true;
        } catch (Exception e) {
            LOG.error("Schedule Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("stats");
        return properties;
    }

}
