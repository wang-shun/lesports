package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SectionTimeFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        Date currentDate = new Date();
        long countTime = LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(currentDate.toString()).getTime() - LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(input.substring(0, input.length() - 5)).getTime();
        long currentMatchTime = countTime / 1000L;
        return currentMatchTime;
    }
}

