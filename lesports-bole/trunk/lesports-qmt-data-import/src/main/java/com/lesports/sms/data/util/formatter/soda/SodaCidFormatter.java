package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.lesports.sms.data.model.SodaConstants;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SodaCidFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        return SodaConstants.cidMap.get(input);
    }
}

