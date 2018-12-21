package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class StatsMatchStatusFormatter implements Function<String, com.lesports.api.common.MatchStatus> {
    @Nullable
    @Override
    public com.lesports.api.common.MatchStatus apply(String matchStatus) {

        Map<String, com.lesports.api.common.MatchStatus> stateMap = ImmutableMap.of("Pre-Game", com.lesports.api.common.MatchStatus.MATCH_NOT_START, "In-Progress", com.lesports.api.common.MatchStatus.MATCHING, "Final", com.lesports.api.common.MatchStatus.MATCH_END);
        return stateMap.get(matchStatus);
    }
}

