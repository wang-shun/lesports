package com.lesports.sms.data.util.formatter;

import com.google.common.collect.Maps;
import com.lesports.sms.data.model.StatsConstants;
import com.lesports.sms.data.util.CommonUtil;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/12/5.
 */
public abstract class ElementFormatter {
    public Map<String, Object> parseStatsElement(Element element, Map<String, String> paths) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = CommonUtil.getStringValue(element.selectObject(String.valueOf(entry.getValue())));
            String valueEnd = value;
            if (key.contains("percentage") || value.contains("percentage") || value.contains("%"))
                valueEnd = CommonUtil.getPercentFormat(value);
            else if (value.startsWith(".")) valueEnd = "0" + value;
            else if (value.startsWith("-")) valueEnd = "0.0";
            stats.put(key, valueEnd);
        }
        return stats;
    }

    public Map<String, Object> parseElementwithRegex(Element element, Map<String, String> paths, String regexValue) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String valueString = String.valueOf(entry.getValue());
            if (valueString.contains("?")) valueString=valueString.replace("?", regexValue);
            String value = CommonUtil.getStringValue(element.selectObject(valueString));
            String valueEnd = value;
            stats.put(key, valueEnd);
        }
        return stats;
    }

    public Map<String, Object> parseElementwithFunction(Element element, Map<String, String> paths) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            String[] functions = value.split("\\|");
            Object valueEnd = null;
            if (functions.length == 3) {
                String left = CommonUtil.getStringValue(element.selectObject(functions[0]));
                String right = CommonUtil.getStringValue(element.selectObject(functions[2]));
                if (functions[1].equals("+")) {
                    valueEnd = Integer.valueOf(left) + Integer.valueOf(right);
                } else if (functions[1].equals("-")) {
                    valueEnd = Integer.valueOf(left) - Integer.valueOf(right);
                } else {
                    valueEnd = 0;
                }
            } else {
                valueEnd = CommonUtil.getStringValue(element.selectObject(value));
            }
            stats.put(key, valueEnd);
        }
        return stats;
    }
}

