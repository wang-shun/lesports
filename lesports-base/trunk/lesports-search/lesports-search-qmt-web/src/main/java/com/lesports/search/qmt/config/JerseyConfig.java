/**
 * 
 */
package com.lesports.search.qmt.config;

import org.springframework.stereotype.Component;

import com.lesports.jersey.support.spring.LeSpringApplication;

/**
 * @author sunyue7
 *
 */
@Component
public class JerseyConfig extends LeSpringApplication {

	public JerseyConfig() {
		packages("com.lesports.search.qmt.resources");
	}

}
