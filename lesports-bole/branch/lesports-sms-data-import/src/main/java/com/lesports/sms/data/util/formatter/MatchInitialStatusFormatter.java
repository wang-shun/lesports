package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import com.lesports.api.common.MatchStatus;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class MatchInitialStatusFormatter implements Function<String, MatchStatus> {
    @Nullable
    @Override
    public MatchStatus apply(String matchStatus) {
        if (StringUtils.isNotEmpty(matchStatus)) {
            if (matchStatus.startsWith("false") && matchStatus.endsWith("false")) return MatchStatus.MATCH_END;
            else if (matchStatus.startsWith("true") && matchStatus.endsWith("false")) return MatchStatus.MATCH_END;
            else if (matchStatus.startsWith("true") && matchStatus.endsWith("true")) return MatchStatus.MATCH_CANCELED;
        }
        return MatchStatus.MATCH_NOT_START;

    }

}

