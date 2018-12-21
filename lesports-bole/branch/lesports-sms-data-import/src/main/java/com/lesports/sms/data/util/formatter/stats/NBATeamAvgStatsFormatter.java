package com.lesports.sms.data.util.formatter.stats;

import com.google.common.base.Function;
import com.lesports.sms.data.model.StatsConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class NBATeamAvgStatsFormatter extends ElementFormatter implements Function<Element, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Element input) {
        return parseStatsElement(input, StatsConstants.NBATeamAVGStatPath);
    }
}