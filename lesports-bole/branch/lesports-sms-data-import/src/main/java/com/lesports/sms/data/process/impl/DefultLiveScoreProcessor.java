package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.qmt.sbd.model.MatchStats;
import com.lesports.sms.data.process.BeanProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("liveScoreProcessor")
public class DefultLiveScoreProcessor extends BeanProcessor {

    private static Logger LOG = LoggerFactory.getLogger(DefultLiveScoreProcessor.class);

    public Boolean process(Object object) {
        Match newMatch = (Match) object;
        try {
            Match match = SbdMatchInternalApis.getMatchById(newMatch.getId());
            List<String> configs = getUpdateProperty();
            if (CollectionUtils.isEmpty(configs)) {
                LOG.warn("NO pemission to Update property");
                return false;
            }
            match = (Match) updateObject(configs, match, newMatch);
            if (SbdMatchInternalApis.saveMatch(match) > 0) {
                LOG.warn("The match:{} is updated", match.getId());
            }

            return true;
        } catch (Exception e) {
            LOG.error("LiveScore Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("status");
        properties.add("currentMoment");
        properties.add("competitors");
        return properties;
    }

}


