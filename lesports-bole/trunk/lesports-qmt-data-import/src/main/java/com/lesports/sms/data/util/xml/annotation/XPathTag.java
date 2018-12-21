package com.lesports.sms.data.util.xml.annotation;

import com.lesports.sms.data.util.xml.annotation.Formatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * lesports-projects
 * 可以配置多个路径，从前向后匹配到数据后停止匹配
 *
 * @author pangchuanxiao
 * @since 16-2-16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathTag {
    Formatter[] formatter() default {};

    String[] value() default {};

}
