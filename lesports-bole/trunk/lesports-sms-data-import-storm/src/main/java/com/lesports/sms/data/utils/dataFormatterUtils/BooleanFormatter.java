package com.lesports.sms.data.utils.dataFormatterUtils;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class BooleanFormatter implements Function<String, Boolean> {
    @Nullable
    @Override
    public Boolean apply(String input) {
        if(input==null)return false;
        if (input.equals("Y") || input.equals("1")) {
            return true;
        } else {
            return false;
        }
    }
}

