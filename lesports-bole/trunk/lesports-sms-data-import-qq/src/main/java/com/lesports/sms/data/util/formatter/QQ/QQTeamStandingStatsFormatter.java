package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.lesports.sms.data.model.QQConstants;
import com.lesports.sms.data.model.SportrardConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQTeamStandingStatsFormatter extends ElementFormatter implements Function<Map<String, Object>, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        return parseJsonMapWithFunction(input, QQConstants.NBAConferenceStandingStatPath);
    }
}