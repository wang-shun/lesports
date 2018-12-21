package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.lesports.api.common.MatchStatus;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/12/19.
 */
public class SodaMatchStatusFormatter implements Function<String, MatchStatus> {
    @Nullable
    @Override
    public com.lesports.api.common.MatchStatus apply(String input) {
        Map<String, MatchStatus> stateMap = ImmutableMap.of("1", com.lesports.api.common.MatchStatus.MATCH_NOT_START, "8", com.lesports.api.common.MatchStatus.MATCH_END);
        MatchStatus currentMatchStatus = stateMap.get(input);
        if (currentMatchStatus == null) currentMatchStatus = MatchStatus.MATCHING;
        return currentMatchStatus;
    }
}
