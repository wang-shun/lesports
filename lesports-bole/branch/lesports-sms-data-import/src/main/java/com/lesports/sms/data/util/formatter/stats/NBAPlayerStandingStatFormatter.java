package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.lesports.sms.data.model.StatsConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class NBAPlayerStandingStatFormatter extends ElementFormatter implements Function<Element, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Element input) {
        return parseStatsElement(input, StatsConstants.NBAPlayerStandingStatPath);
    }
}

