package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/12/5.
 */
public class PoundToKgFormatter implements Function<String, Integer> {
    @Nullable
    @Override
    public Integer apply(String input) {
        try {
            Double wieghtNew = Double.valueOf(input) * 0.45;
            return Integer.parseInt(new java.text.DecimalFormat("0").format(wieghtNew));
        } catch (Exception e) {
            return 0;
        }
    }
}