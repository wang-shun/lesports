package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtCountryInternalApis;
import com.lesports.qmt.config.model.Country;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class CountryFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("code", "is", input));
        List<Country> dictEntry = QmtCountryInternalApis.getCountryByQuery(query);
        if (CollectionUtils.isNotEmpty(dictEntry)) return dictEntry.get(0).getId();
        else {
            InternalQuery query1 = new InternalQuery();
            query.addCriteria(new InternalCriteria("chinese_name", "is", input));
            List<Country> dictEntry1 = QmtCountryInternalApis.getCountryByQuery(query1);
            if (CollectionUtils.isNotEmpty(dictEntry1)) return dictEntry1.get(0).getId();
        }
        return 0L;
    }
}

