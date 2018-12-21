package com.lesports.sms.data.util.xml.annotation;

/**
 * Created by qiaohongxin on 2016/11/14.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathStats {
    Class formatter() default DefaultFormatter.class;

    String value() default "";
}
