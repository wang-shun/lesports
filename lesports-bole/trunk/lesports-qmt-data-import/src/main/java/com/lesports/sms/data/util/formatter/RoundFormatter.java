package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class RoundFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        InternalQuery query = new InternalQuery();
        String name = "第" + input + "轮";
        query.addCriteria(new InternalCriteria("name", "is", name));
        List<DictEntry> dictEntry = QmtConfigDictInternalApis.getDictsByQuery(query);
        if (CollectionUtils.isNotEmpty(dictEntry)) return dictEntry.get(0).getId();
        return 0L;
    }
}

