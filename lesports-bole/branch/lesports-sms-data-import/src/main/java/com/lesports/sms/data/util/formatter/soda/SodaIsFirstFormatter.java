package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SodaIsFirstFormatter implements Function<String, Boolean> {
    @Nullable
    @Override
    public Boolean apply(String input) {
        if (input.equals("首发")) {
            return true;
        }
        return false;
    }
}

