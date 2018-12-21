package com.lesports.sms.data.utils;

import com.google.common.base.Function;
import com.lesports.utils.LeDateUtils;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class MomentFormatter implements Function<String, String> {
    @Nullable
    @Override
    public String apply(String matchStatus) {
        String moment=null;
        if ("6".equals(matchStatus)) {
            moment = "上半场-";
        } else if ("7".equals(matchStatus)) {
            moment = "下半场-" ;
        } else if ("41".equals(matchStatus)) {
            moment = "加时上半场-" ;
        } else if ("42".equals(matchStatus)) {
            moment = "加时下半场-";
        } else if ("50".equals(matchStatus)) {
            moment = "点球-" ;
        }
        return moment;
    }
}

