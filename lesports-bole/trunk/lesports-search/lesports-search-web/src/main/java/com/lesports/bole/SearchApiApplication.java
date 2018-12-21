package com.lesports.bole;

import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 *
 */
public class SearchApiApplication extends LeSpringApplication {

    /**
     * Register JAX-RS application components.
     */
    public SearchApiApplication() {
        packages("com.lesports.bole.resources");
    }

}
