package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class RoundFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        DictEntry dictEntry = QmtConfigDictInternalApis.getDictByParentIdAndName(100004000L, "第" + input + "轮");
        if (dictEntry == null) return dictEntry.getId();
        return 0L;
    }
}

