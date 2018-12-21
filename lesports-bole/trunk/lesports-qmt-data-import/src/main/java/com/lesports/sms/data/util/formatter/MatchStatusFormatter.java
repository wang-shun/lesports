package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.api.common.MatchStatus;
import javax.annotation.Nullable;
/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class MatchStatusFormatter implements Function<String, MatchStatus> {
    @Nullable
    @Override
    public MatchStatus apply(String matchStatus) {
        MatchStatus status = null;
        if ("100".equals(matchStatus) || "110".equals(matchStatus) || "120".equals(matchStatus) || "70".equals(matchStatus) || "80".equals(matchStatus) || "90".equals(matchStatus)) {
            status = MatchStatus.MATCH_END;
        } else if ("0".equals(matchStatus) || "60".equals(matchStatus) || "61".equals(matchStatus)) {
            status = MatchStatus.MATCH_NOT_START;
        } else {
            status = MatchStatus.MATCHING;
        }
        return status;
    }

}

