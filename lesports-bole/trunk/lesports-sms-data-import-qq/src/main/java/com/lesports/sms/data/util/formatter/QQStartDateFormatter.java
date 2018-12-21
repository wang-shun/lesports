package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class QQStartDateFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        if (StringUtils.isEmpty(input)) return null;
        String time = input.replace("-", "");
        String resultDate = time.replace(" ", "");
        String resultTime = resultDate.replace(":", "");
        return resultTime;
    }
}

