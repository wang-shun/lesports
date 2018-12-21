package com.lesports.sms.data.utils.dataFormatterUtils;

import com.google.common.base.Function;
import com.hankcs.hanlp.corpus.util.StringUtils;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class PercentFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        if(StringUtils.isBlankOrNull(input))return "";
        return input + "%";
    }
}

