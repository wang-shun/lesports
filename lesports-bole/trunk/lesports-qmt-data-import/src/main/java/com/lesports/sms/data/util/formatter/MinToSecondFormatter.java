package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class MinToSecondFormatter implements Function<String, Double> {
    @Nullable
    @Override
    public Double apply(String input) {
        if (StringUtils.isEmpty(input)) return 0.0;
        else
            return Double.parseDouble(input) * 60;
    }
}

