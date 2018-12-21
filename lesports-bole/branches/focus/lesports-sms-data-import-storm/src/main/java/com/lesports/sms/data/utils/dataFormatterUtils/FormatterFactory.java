package com.lesports.sms.data.utils.dataFormatterUtils;

import com.google.common.base.Function;

import java.io.Serializable;

/**
 * Created by qiaohongxin on 2016/5/4.
 */
public class FormatterFactory implements Serializable {
    public static Function<String, ?> getFunction(String type) {
        if(type==null)return null;
        if (type.equals("PercentFormatter")) {
            return new PercentFormatter();
        } else if (type.equals("HomeAwayFormatter")) {
            return new HomeAwayFormatter();
        } else if (type.equals("sectionFormatter")) {
            return new SectionFormatter();
        } else if (type.equals("BooleanFormatter")) {
            return new BooleanFormatter();
        }

        return null;
    }
}
