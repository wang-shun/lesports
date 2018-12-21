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

    public Map<String, Object> parseJsonMap(Map<String, Object> elements, Map<String, String> paths) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            Object elementValue = elements.get(value);
            if (key.contains("percentage") || key.contains("winPic")) {
                elementValue = CommonUtil.getPercentFormat(elementValue == null ? "" : elementValue.toString());
            }
            stats.put(key, elementValue);
        }
        return stats;
    }

    public Map<String, Object> parseJsonMapWithFunction(Map<String, Object> elements, Map<String, String> paths) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            String[] functions = value.split("\\|");
            Object valueEnd = 0;
            if (functions.length == 3) {
                String left = String.valueOf(elements.get(functions[0]));
                String right = String.valueOf(elements.get(functions[2]));

                if (functions[1].equals("+")) {
                    valueEnd = CommonUtil.parseInt(left, 0) + CommonUtil.parseInt(right, 0);
                } else if (functions[1].equals("-")) {
                    valueEnd = CommonUtil.parseInt(left, 0) - CommonUtil.parseInt(right, 0);
                } else if (functions[1].equals("/")) {
                    valueEnd = CommonUtil.parseDouble(left, 0) / CommonUtil.parseDouble(right, 1);
                } else if (functions[1].equals("SPLIT:-")) {
                    right = functions[2];
                    Integer index = CommonUtil.parseInt(right, 0);
                    valueEnd = left.split("\\-")[index];
                }
            } else if (functions.length == 2) {
                String left = String.valueOf(elements.get(functions[0]));
                if (functions[1].equals("ABS:-")) {
                    Integer leftValue = CommonUtil.parseInt(left, 0);
                    if (leftValue > 0) {
                        valueEnd = "";
                    } else {
                        valueEnd = String.valueOf(Math.abs(leftValue));
                    }
                } else if (functions[1].equals("ABS:+")) {
                    Integer leftValue = CommonUtil.parseInt(left, 0);
                    if (leftValue < 0) {
                        valueEnd = "0";
                    } else {
                        valueEnd = String.valueOf(leftValue.toString());
                    }
                }
            } else {
                valueEnd = elements.get(value);
            }
            if (valueEnd == null) {
                valueEnd = 0;
            }
            if (key.contains("percentage") || key.contains("winPic")) {
                valueEnd = CommonUtil.getPercentFormat(valueEnd.toString());
            } else if (valueEnd.toString().startsWith(".")) valueEnd = "0" + valueEnd;
            else if (valueEnd.toString().startsWith("-")) valueEnd = "0.0";
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
            if (valueString.contains("?")) valueString = valueString.replace("?", regexValue);
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

