package com.lesports.crawler.pipeline;

import java.lang.annotation.*;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Model {
    public Class clazz();
}
