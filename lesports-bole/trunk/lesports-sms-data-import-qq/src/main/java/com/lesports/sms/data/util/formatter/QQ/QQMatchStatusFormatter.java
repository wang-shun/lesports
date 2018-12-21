package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQMatchStatusFormatter implements Function<String, MatchStatus> {
    @Nullable
    @Override
    public MatchStatus apply(String input) {
        if (StringUtils.isBlank(input)||input.equals("0")) return MatchStatus.MATCH_NOT_START;
        else if (input.equals("2")) return MatchStatus.MATCH_END;
        return MatchStatus.MATCHING;
    }
}


