package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.model.PlayerCareerStat;
import com.lesports.sms.data.process.BeanProcessor;
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
            List<PlayerCareerStat> playerCareerStats = SbdPlayerInternalApis.getPlayerCareerStatByPidAndScopeAndScopeIdAndType(newStats.getPlayerId(), newStats.getScopeType(), newStats.getScopeId(), newStats.getStatType());
            if (CollectionUtils.isEmpty(playerCareerStats)) {
                SbdPlayerInternalApis.savePlayerCareerStat(newStats);
                return true;
            }
            PlayerCareerStat playerCareerStat = playerCareerStats.get(0);
            List<String> configs = getUpdateProperty();
            if (CollectionUtils.isEmpty(configs)) {
                LOG.warn("NO pemission to Update property");
                return false;
            }
            playerCareerStat = (PlayerCareerStat) updateObject(configs, playerCareerStat, newStats);
            if (SbdPlayerInternalApis.savePlayerCareerStat(playerCareerStat) > 0) {
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
