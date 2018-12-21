package com.lesports.sms.data;

import com.lesports.jersey.support.filter.AccessControlAllowOriginResponseFilter;
import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 * User: ellios
 * Time: 14-6-11 : 下午10:49
 */
public class SmsDataApplication extends LeSpringApplication {

    /**
     * Register JAX-RS application components.
     */
    public SmsDataApplication() {
        packages("com.lesports.sms.data.resources");
        register(AccessControlAllowOriginResponseFilter.class);
    }

}
