package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.qmt.sbd.model.CompetitorSeasonStat;
import com.lesports.sms.data.process.BeanProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("competitorSeasonStatsProcessor")
public class DefaultCompetitorStatsProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultCompetitorStatsProcessor.class);

    public Boolean process(Object object) {
        CompetitorSeasonStat newStats = (CompetitorSeasonStat) object;
        try {
            CompetitorSeasonStat competitorSeasonStat = SbdCompetitionSeasonInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(newStats.getCsid(), newStats.getCompetitorId(), CompetitorType.PLAYER);
            if (competitorSeasonStat == null) {
                SbdCompetitionSeasonInternalApis.saveCompetitorSeasonStat(newStats);
                return true;
            } else {
                List<String> configs = getUpdateProperty();
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                competitorSeasonStat = (CompetitorSeasonStat) updateObject(configs, competitorSeasonStat, newStats);
                if (SbdCompetitionSeasonInternalApis.saveCompetitorSeasonStat(competitorSeasonStat) > 0) {
                    LOG.warn("Player Season Stats:{} is updated", competitorSeasonStat.getCompetitorId());
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Schedule Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("avgStats");
        properties.add("stats");
        properties.add("topStats");
        return properties;
    }

}
