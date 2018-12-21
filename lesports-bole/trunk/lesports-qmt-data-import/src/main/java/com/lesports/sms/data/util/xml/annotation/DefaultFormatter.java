package com.lesports.sms.data.util.xml.annotation;

import com.google.common.base.Function;
import javax.annotation.Nullable;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2016/2/23
 */
public class DefaultFormatter implements Function<String, String> {
    public static  final      Formatter[] x = new Formatter[0];

    @Nullable
    @Override
    public String apply(String input) {
        return input;
    }
}
