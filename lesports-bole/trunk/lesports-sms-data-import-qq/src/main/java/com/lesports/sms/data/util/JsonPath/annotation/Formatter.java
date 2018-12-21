package com.lesports.sms.data.util.JsonPath.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2016/2/23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Formatter {
    Class formatter() default DefaultFormatter.class;

    String value();

}
