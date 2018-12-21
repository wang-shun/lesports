package com.lesports.bole.api.web;

import com.lesports.jersey.support.filter.LogCacheUrlResponseFilter;
import com.lesports.jersey.support.spring.LeSpringApplication;
import com.lesports.jersey.velocity.VelocityMvcFeature;

/**
 * BoleWebApiApplication
 * @author denghui
 *
 */
public class BoleWebApiApplication extends LeSpringApplication {

    /**
     * Register JAX-RS application components.
     */
    public BoleWebApiApplication() {
        packages("com.lesports.bole.api.web.resources");

        register(LogCacheUrlResponseFilter.class);
        register(VelocityMvcFeature.class);
        property(VelocityMvcFeature.TEMPLATES_BASE_PATH, "pages");
    }

}
