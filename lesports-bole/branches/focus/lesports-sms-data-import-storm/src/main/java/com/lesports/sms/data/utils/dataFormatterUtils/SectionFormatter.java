package com.lesports.sms.data.utils.dataFormatterUtils;

import com.google.common.base.Function;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SectionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        List<DictEntry> parentDicts = SbdsInternalApis.getDictEntriesByName("^节次$");
        if (CollectionUtils.isEmpty(parentDicts)) return 0L;
        List<DictEntry> currrentDicts = SbdsInternalApis.getDictEntryByCodeAndParentId(input, parentDicts.get(0).getId());
        if (CollectionUtils.isEmpty(currrentDicts)) return 0L;
        return currrentDicts.get(0).getId();
    }
}

