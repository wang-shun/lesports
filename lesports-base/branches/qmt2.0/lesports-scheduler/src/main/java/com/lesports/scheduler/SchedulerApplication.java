package com.lesports.scheduler;

import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 * User: ellios
 * Time: 14-6-11 : 下午10:49
 */
public class SchedulerApplication extends LeSpringApplication {

    /**
     * Register JAX-RS application components.
     */
    public SchedulerApplication() {
        packages("com.lesports.scheduler.resources");
    }

}
