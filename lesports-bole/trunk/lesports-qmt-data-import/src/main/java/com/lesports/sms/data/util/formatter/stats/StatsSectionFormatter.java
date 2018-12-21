package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class StatsSectionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        String currentSectionName = "";
        if ("6".equals(input)) {
            currentSectionName = "^上半场$";

        } else if ("7".equals(input)) {
            currentSectionName = "^下半场$";
        } else if ("41".equals(input)) {
            currentSectionName = "^加时上半场$";
        } else if ("42".equals(input)) {
            currentSectionName = "^加时下半场$";

        } else if ("50".equals(input)) {
            currentSectionName = "^点球$";
        }
        //TODO       SbcDi
        return 0L;
    }
}

