package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.SbdTopListInternalApis;
import com.lesports.qmt.sbd.model.MatchStats;
import com.lesports.qmt.sbd.model.TopList;
import com.lesports.sms.data.process.BeanProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("topListProcessor")
public class DefaultTopListProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultTopListProcessor.class);

    public Boolean process(Object object) {
        TopList newTopList = (TopList) object;
        try {
            TopList topList = SbdTopListInternalApis.getTopListById(0L);//TODO ,edit sbds methods;
            if (topList == null) {
                SbdTopListInternalApis.saveTopList(newTopList);
                return true;
            } else {
                List<String> configs = getUpdateProperty();
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                topList = (TopList) updateObject(configs, topList, newTopList);
                if (SbdTopListInternalApis.saveTopList(topList) > 0) {
                    LOG.warn("TopList:{} is updated", topList.getType());
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("TopList Exception error ,e:{}", e);
            return false;
        }
    }

    private List<String> getUpdateProperty() {
        List<String> properties = Lists.newArrayList();
        properties.add("items");
        return properties;
    }

}
