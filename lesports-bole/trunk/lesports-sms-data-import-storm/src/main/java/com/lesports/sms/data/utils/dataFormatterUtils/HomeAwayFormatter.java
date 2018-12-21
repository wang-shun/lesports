package com.lesports.sms.data.utils.dataFormatterUtils;

import com.google.common.base.Function;
import com.lesports.sms.api.common.GroundType;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class HomeAwayFormatter implements Function<String, GroundType> {
    @Nullable
    @Override
    public GroundType apply(String input) {
        if (input.equalsIgnoreCase("HOME") || input.equals("0")) {
            return GroundType.HOME;
        } else return GroundType.AWAY;
    }
}

