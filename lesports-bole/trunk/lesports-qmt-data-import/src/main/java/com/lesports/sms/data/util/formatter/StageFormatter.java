package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.sms.data.model.CommonConstants;
import com.lesports.sms.data.model.Constants;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class StageFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        String roundName = CommonConstants.stageMap.get(input);
        DictEntry dictEntries = QmtConfigDictInternalApis.getDictByParentIdAndName(0L, "^阶段");
        if (null == dictEntries) {
            return 0L;
        }
        DictEntry dictEntry1 = QmtConfigDictInternalApis.getDictByParentIdAndName(dictEntries.getId(), roundName);
        if (dictEntry1 == null) return 0L;
        return dictEntry1.getId();
    }
}

