package com.lesports.monitor.cat.constant;

import java.lang.annotation.*;

/**
 * Created by zhangdeqiang on 2017/3/7.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CatMonitorAnnotation {
    Class<?> clazz() default Void.class;
    String memo() default "";
}
