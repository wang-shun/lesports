package com.lesports.search.qmt;

import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 *
 */
public class SearchApiApplication extends LeSpringApplication {

	/**
	 * Register JAX-RS application components.
	 */
	public SearchApiApplication() {
		packages("com.lesports.search.qmt.resources");
	}

}
