package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.MatchStats;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("liveStatsProcessor")
public class DefaultLiveStatsProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultLiveStatsProcessor.class);

    public Boolean process(Object object) {
        MatchStats newMatchStats = (MatchStats) object;
        try {
            MatchStats matchStats = SbdsInternalApis.getMatchStatsById(newMatchStats.getId());
            List<String> configs = getUpdateProperty();
            if (CollectionUtils.isEmpty(configs)) {
                LOG.warn("NO pemission to Update property");
                return false;
            }
            matchStats = (MatchStats) updateObject(configs, matchStats, newMatchStats);
            if (SbdsInternalApis.saveMatchStats(matchStats) > 0) {
                LOG.warn("The match:{} is updated", matchStats.getId());
            }
            return true;
        } catch (Exception e) {
            LOG.error("LiveScore Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("competitorStats");
        properties.add("squads");
        return properties;
    }
}
