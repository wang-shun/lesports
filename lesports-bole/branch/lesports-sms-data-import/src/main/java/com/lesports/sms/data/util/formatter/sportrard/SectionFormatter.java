package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SectionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        String currentSectionName = "";
        if ("6".equals(input)) {
            currentSectionName = "^上半场$";

        } else if ("7".equals(input)) {
            currentSectionName = "^下半场$";
        } else if ("41".equals(input)) {
            currentSectionName = "^加时上半场$";
        } else if ("42".equals(input)) {
            currentSectionName = "^加时下半场$";

        } else if ("50".equals(input)) {
            currentSectionName = "^点球$";
        }
        InternalQuery currentQuery = new InternalQuery();
        currentQuery.addCriteria(new InternalCriteria("name", "is", currentSectionName));
        List<DictEntry> validDicts = QmtConfigDictInternalApis.getDictsByQuery(currentQuery);
        if (CollectionUtils.isEmpty(validDicts)) return 0L;
        else return validDicts.get(0).getId();
    }
}

