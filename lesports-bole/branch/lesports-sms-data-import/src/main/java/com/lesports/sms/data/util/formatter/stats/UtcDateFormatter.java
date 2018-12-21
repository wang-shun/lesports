package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.utils.LeDateUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class UtcDateFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        String[] timeLists = input.split("\\-");
        if (timeLists.length == 6) {
            return CommonUtil.getDataYYYYMMDDHHMMSS(timeLists[0], timeLists[1], timeLists[2], timeLists[3], timeLists[4], timeLists[5]);
        } else {
            return null;
        }
    }
}

