package com.lesports.sms.data.process.impl;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.TopList;
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
            InternalQuery query=new InternalQuery();
            query.addCriteria(new InternalCriteria("csid","is",newTopList.getCsid()));
            query.addCriteria(new InternalCriteria("type","is",newTopList.getType()));
            query.addCriteria(new InternalCriteria("scope","is",newTopList.getScope()));
            query.addCriteria(new InternalCriteria("competitor_type","is",newTopList.getCompetitorType()));
          List<TopList> topLists= SbdsInternalApis.getTopListsByQuery(query);
            if (CollectionUtils.isEmpty(topLists) ) {
                SbdsInternalApis.saveTopList(newTopList);
                return true;
            } else {
                TopList topList=topLists.get(0);
                List<String> configs = getUpdateProperty();
                if (CollectionUtils.isEmpty(configs)) {
                    LOG.warn("NO pemission to Update property");
                    return false;
                }
                topList = (TopList) updateObject(configs, topList, newTopList);
                if (SbdsInternalApis.saveTopList(topList) > 0) {
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
