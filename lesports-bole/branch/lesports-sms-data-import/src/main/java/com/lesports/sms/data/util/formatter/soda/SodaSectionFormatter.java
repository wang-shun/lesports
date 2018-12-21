package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.lesports.api.common.MatchStatus;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/12/19.
 */
public class SodaSectionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        String currentSectionName = "";
        if ("2".equals(input)) {
            currentSectionName = "^上半场$";

        } else if ("4".equals(input)) {
            currentSectionName = "^下半场$";
        } else if ("6".equals(input)) {
            currentSectionName = "^加时$";
        }
        InternalQuery currentQuery = new InternalQuery();
        currentQuery.addCriteria(new InternalCriteria("name", "is", currentSectionName));
        List<DictEntry> validDicts = QmtConfigDictInternalApis.getDictsByQuery(currentQuery);
        if (CollectionUtils.isEmpty(validDicts)) return 0L;
        else return validDicts.get(0).getId();
    }
}
