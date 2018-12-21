package com.lesports.sms.data.util.formatter;

import com.google.common.base.Function;
import net.minidev.json.JSONArray;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class TypeFormatter implements Function<Object, Long> {
    @Nullable
    @Override
    public Long apply(Object input) {
        if (net.minidev.json.JSONArray.class.isAssignableFrom(input.getClass())) {
            JSONArray arrays = (JSONArray) input;
            arrays.size();
            Map<String, Object> currentObject = (Map<String, Object>) arrays.get(0);
            if (currentObject.get("teamId") != null) return 0L;
        }
        return 0L;
    }
}

