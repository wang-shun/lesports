package com.lesports.sms.data.util.JsonPath.annotation;

import com.google.common.base.Function;
import com.lesports.utils.xml.annotation.Formatter;

import javax.annotation.Nullable;

/**
 * aa.
 *
 * @author qiaohongxin
 * @since 2016/2/23
 */
public class DefaultFormatter implements Function<String, String> {
    public static final com.lesports.utils.xml.annotation.Formatter[] x = new Formatter[0];

    @Nullable
    @Override
    public String apply(String input) {
        return input;
    }
}
