package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.lesports.sms.data.model.SodaConstants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/12/19.
 */
public class SodaMatchStartTimeFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        if (StringUtils.isNotEmpty(input)) {
            String validtime = "";
            validtime = input.replace("-", "");
            validtime = validtime.replace(":", "");
            validtime = validtime.replace(" ", "");
            return validtime;
        }
        return "";
    }
}

