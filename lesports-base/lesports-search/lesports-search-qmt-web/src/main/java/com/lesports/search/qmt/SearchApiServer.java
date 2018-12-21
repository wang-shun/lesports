package com.lesports.search.qmt;

import com.lesports.jersey.AbstractJerseyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SearchApiServer extends AbstractJerseyServer {

	private static final Logger LOG = LoggerFactory.getLogger(SearchApiServer.class);

	private static final int DEFAULT_PORT = 9381;

	public static void main(final String[] args) throws Exception {
		int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);
		SearchApiApplication app = new SearchApiApplication();
		runServer(port, "/search/v2/", app);
		LOG.info("Search application launched at " + port);
	}
}
