package com.lesports.msg;

import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 * User: ellios
 * Time: 14-6-11 : 下午10:49
 */
public class MsgApplication extends LeSpringApplication {

    /**
     * Register JAX-RS application components.
     */
    public MsgApplication() {
        packages("com.lesports.msg.web.resources");
    }

}
