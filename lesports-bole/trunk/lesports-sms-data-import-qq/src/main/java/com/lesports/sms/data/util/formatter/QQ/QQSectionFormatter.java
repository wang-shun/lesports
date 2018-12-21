package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQSectionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        if(StringUtils.isBlank(input))return 0L;
       String[] sectionTimesList=input.split("\\ ");
        if(sectionTimesList.length<=0)return 0L;
            String sectionName=sectionTimesList[0];
            List<DictEntry> currentDict= SbdsInternalApis.getDictEntriesByName(sectionName);
            if(CollectionUtils.isEmpty(currentDict))return 0L;
        return currentDict.get(0).getId();
        }

    }


