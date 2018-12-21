package com.lesports.crawler;

import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 *
 */
public class CrawlerApiApplication extends LeSpringApplication {

    /**
     * Register JAX-RS application components.
     */
    public CrawlerApiApplication() {
        packages("com.lesports.search.resources");


    }

}
