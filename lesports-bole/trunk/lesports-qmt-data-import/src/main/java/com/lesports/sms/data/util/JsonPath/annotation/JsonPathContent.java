package com.lesports.sms.data.util.JsonPath.annotation;

import com.google.common.base.Function;
import org.dom4j.XPath;

/**
 * Created by qiaohongxin on 2016/11/15.
 */
public class JsonPathContent {
    public String type;
    public String path1;
    public Function function;

    public JsonPathContent(String type, String path1, Function function) {
        this.type = type;
        this.path1 = path1;
        this.function = function;
    }

    public String getPath1() {
        return path1;
    }

    public void setPath1(String xpath1) {
        this.path1 = xpath1;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
