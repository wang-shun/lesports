package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class BirthDateFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        if (StringUtils.isEmpty(input)) return null;
        String[] dates = input.split("\\-");
        String birthDate = "";
        if (dates.length == 3) {
            birthDate = dates[0];
            if (dates[1].length() == 1) birthDate = birthDate + "0" + dates[1];
            else {
                birthDate = birthDate + "0" + dates[1];
            }
            if (dates[2].length() == 1) birthDate = birthDate + "0" + dates[2];
            else {
                birthDate = birthDate + dates[2];
            }
            return birthDate;
        }
        return null;
    }
}

