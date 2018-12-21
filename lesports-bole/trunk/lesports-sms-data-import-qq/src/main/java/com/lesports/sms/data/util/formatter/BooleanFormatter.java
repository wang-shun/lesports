package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class BooleanFormatter implements Function<String, Boolean> {
    @Nullable
    @Override
    public Boolean apply(String input) {
        if (input.equals("0") || input.equals("否")) return false;
        else return true;
    }
}

