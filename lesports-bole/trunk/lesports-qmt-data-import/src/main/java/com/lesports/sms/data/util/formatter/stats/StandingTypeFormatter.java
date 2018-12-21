package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.sms.data.model.CommonConstants;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.StatsConstants;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class StandingTypeFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {

        if (StringUtils.isBlankOrNull(input)) return null;
        else return CommonConstants.rankingTypeId.get(input);
    }
}

