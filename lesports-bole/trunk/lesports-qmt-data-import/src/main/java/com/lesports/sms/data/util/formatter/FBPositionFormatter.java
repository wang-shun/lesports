package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.data.model.CommonConstants;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class FBPositionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {

        String name = CommonConstants.soccerPositionMap.get(input);
        if (StringUtils.isNotEmpty(name)) return CommonUtil.parseLong(name, 0L);
        return 0L;
    }
}