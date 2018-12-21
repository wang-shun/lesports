package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.lesports.api.common.MatchStatus;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class MatchStatusFormatter implements Function<String, MatchStatus> {
    @Nullable
    @Override
    public MatchStatus apply(String matchStatus) {
        Byte matchStatusByte = new Byte("0");
        if ("100".equals(matchStatus) || "110".equals(matchStatus) || "120".equals(matchStatus) || "70".equals(matchStatus) || "80".equals(matchStatus) || "90".equals(matchStatus)) {
            matchStatusByte = new Byte("2");
        } else if ("0".equals(matchStatus) || "60".equals(matchStatus) || "61".equals(matchStatus)) {
            matchStatusByte = new Byte("0");
        } else {
            matchStatusByte = new Byte("1");
        }
        return MatchStatus.findByValue(matchStatusByte);
    }

}

