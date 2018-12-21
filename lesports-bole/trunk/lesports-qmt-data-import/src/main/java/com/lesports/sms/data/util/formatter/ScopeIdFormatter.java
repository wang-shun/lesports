package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.qmt.sbd.api.common.ScopeType;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class ScopeIdFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        Long partentId = 100009000L;
        InternalQuery currentQuery = new InternalQuery();
        currentQuery.addCriteria(new InternalCriteria("name", "is", "分组"));
        List<DictEntry> validDicts = QmtConfigDictInternalApis.getDictsByQuery(currentQuery);
        if (CollectionUtils.isNotEmpty(validDicts)) partentId = validDicts.get(0).getId();
        int position = input.length();
        String g = input.substring(position - 1, position);
        String gname = g + "组";
        DictEntry dc = QmtConfigDictInternalApis.getDictByParentIdAndName(partentId, gname);
        if (dc == null) {
            return 100009000L;
        } else {
            return dc.getId();
        }
    }
}

