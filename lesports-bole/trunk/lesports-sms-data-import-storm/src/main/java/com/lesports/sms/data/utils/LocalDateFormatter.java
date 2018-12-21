package com.lesports.sms.data.utils;

import com.google.common.base.Function;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.utils.LeDateUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class LocalDateFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        if(input!=null&&input.length()>=19) {
            Date date = LeDateUtils.parserYYYY_MM_DDTHH_MM_SS(input.substring(0, 19));
            return LeDateUtils.formatYYYYMMDDHHMMSS(date);
        }
        else return "0";
    }
}

