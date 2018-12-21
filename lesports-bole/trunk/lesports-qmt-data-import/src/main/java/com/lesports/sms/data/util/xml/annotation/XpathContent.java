package com.lesports.sms.data.util.xml.annotation;

import com.google.common.base.Function;
import org.dom4j.XPath;

/**
 * Created by qiaohongxin on 2016/11/15.
 */
public class XpathContent {
    public String type;
    public XPath xpath1;
    public Function function;

    public XpathContent(String type, XPath xpath1, Function function) {
        this.type = type;
        this.xpath1 = xpath1;
        this.function = function;
    }

    public XPath getXpath1() {
        return xpath1;
    }

    public void setXpath(XPath xpath1) {
        this.xpath1 = xpath1;
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
