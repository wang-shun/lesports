package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.lesports.qmt.sbd.api.common.ScopeType;
import com.lesports.sms.data.model.StatsConstants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class StatsScopeTypeFormatter implements Function<String, ScopeType> {
    @Nullable
    @Override
    public ScopeType apply(String input) {
        if (StringUtils.isNotEmpty(input)) {
            if (input.contains(StatsConstants.conference))
                return ScopeType.CONFERENCE;
            else if (input.contains(StatsConstants.division)) {
                return ScopeType.DIVISION;
            } else if (input.contains(StatsConstants.group)) {
                return ScopeType.GROUP;
            }
        }
        return ScopeType.CONFERENCE;
    }
}

