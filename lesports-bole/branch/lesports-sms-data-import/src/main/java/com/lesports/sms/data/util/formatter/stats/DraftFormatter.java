package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class DraftFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String input) {
        if (StringUtils.isEmpty(input)) return null;
        String[] contents = input.split("\\-");
        if (contents.length == 3) {
            return contents[0] + "第" + contents[1] + "轮" + contents[2] + "号";
        }
        return null;
    }
}

