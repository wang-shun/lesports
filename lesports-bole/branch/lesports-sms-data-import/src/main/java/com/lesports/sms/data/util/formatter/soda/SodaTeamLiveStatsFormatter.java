package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.lesports.sms.data.model.SodaConstants;
import com.lesports.sms.data.model.SportrardConstants;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import org.dom4j.Element;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class SodaTeamLiveStatsFormatter extends ElementFormatter implements Function<Element, Map<String, Object>> {
    @Nullable
    @Override
    public Map<String, Object> apply(Element input) {
        return parseElementwithFunction(input, SodaConstants.sodaLiveTeamStatPath);
    }
}