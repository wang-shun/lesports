package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.qmt.sbd.api.common.Gender;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class GenderFormatter implements Function<String, Gender> {
    @Nullable
    @Override
    public Gender apply(String input) {
        return Gender.MALE;
    }
}

