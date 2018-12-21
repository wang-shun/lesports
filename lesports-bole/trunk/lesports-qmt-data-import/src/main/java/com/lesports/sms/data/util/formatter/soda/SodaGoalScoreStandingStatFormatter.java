package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.lesports.sms.data.model.SportrardConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SodaGoalScoreStandingStatFormatter extends ElementFormatter implements Function<Element, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Element input) {
        return parseStatsElement(input, SportrardConstants.goalScorePlayerStandingStatPath);
    }
}

