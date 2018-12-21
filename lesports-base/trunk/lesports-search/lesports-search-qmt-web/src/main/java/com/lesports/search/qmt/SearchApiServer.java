package com.lesports.search.qmt;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.lesports.jersey.AbstractJerseyServer;
import com.lesports.search.qmt.config.JerseyConfig;

/**
 *
 */
@Configuration
@ComponentScan(basePackages = "com.lesports")
@ImportResource("classpath*:/META-INF/spring/*.xml")
public class SearchApiServer extends AbstractJerseyServer {

	private static final Logger LOG = LoggerFactory.getLogger(SearchApiServer.class);

	private static final int DEFAULT_PORT = 9381;

	private static int port = DEFAULT_PORT;

	public static void main(final String[] args) throws Exception {
		port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);
		SpringApplication.run(SearchApiServer.class, args);
		LOG.info("Search application launched at " + port);
	}

	@Bean
	public ServletRegistrationBean jerseyServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/*");
		registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
		return registration;
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory container = new TomcatEmbeddedServletContainerFactory();
		container.setPort(port);
		container.setContextPath("/search/v2");
		return container;
	}

}
