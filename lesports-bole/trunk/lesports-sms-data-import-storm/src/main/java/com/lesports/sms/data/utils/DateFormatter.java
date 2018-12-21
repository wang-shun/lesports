package com.lesports.sms.data.utils;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class DateFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        Date date = LeDateUtils.parseYYYY_MM_DDTHH_MM_SSZ(input);
        return LeDateUtils.formatYYYYMMDDHHMMSS(date);
    }
}

