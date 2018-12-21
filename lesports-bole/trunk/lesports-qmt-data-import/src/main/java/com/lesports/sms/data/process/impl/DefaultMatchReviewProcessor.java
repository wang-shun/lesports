package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.SbdTopListInternalApis;
import com.lesports.qmt.sbd.model.MatchReview;
import com.lesports.qmt.sbd.model.TopList;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.data.process.BeanProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("matchReviewProcessor")
public class DefaultMatchReviewProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultMatchReviewProcessor.class);

    public Boolean process(Object object) {
        MatchReview matchReviewNew = (MatchReview) object;
        try {

            MatchReview matchReview = SbdMatchInternalApis.getMatchReviewById(matchReviewNew.getId());
            if (null == matchReview) {
                SbdMatchInternalApis.saveMatchReview(matchReviewNew);
                return true;
            }
            List<String> configs = getUpdateProperty();
            if (CollectionUtils.isEmpty(configs)) {
                LOG.warn("NO pemission to Update property");
                return false;
            }
            matchReview = (MatchReview) updateObject(configs, matchReview, matchReviewNew);
            if (SbdMatchInternalApis.saveMatchReview(matchReview) > 0) {
                LOG.warn("MatchReview:{} is updated", matchReview.getId());
            }

            return true;
        } catch (Exception e) {
            LOG.error("MatchReview Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("confrontations");
        properties.add("matchInfos");
        return properties;
    }

}
