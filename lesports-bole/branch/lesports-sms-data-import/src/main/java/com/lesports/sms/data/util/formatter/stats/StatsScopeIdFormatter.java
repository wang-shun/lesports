package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.lesports.sms.data.model.StatsConstants;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class StatsScopeIdFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        if (StatsConstants.nbaConferenceMap.get(input) == null) return 0L;
        else return StatsConstants.nbaConferenceMap.get(input);
    }
}

