package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.lesports.sms.api.common.ScopeType;
import net.minidev.json.JSONArray;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQTeamStandingScopeTypeFormatter implements Function<Object, ScopeType> {
    @Nullable
    @Override
    public ScopeType apply(Object input) {
        if (net.minidev.json.JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            if (arrays != null && arrays.size() == 5) return ScopeType.DIVISION;
        }
        return ScopeType.CONFERENCE;
    }
}