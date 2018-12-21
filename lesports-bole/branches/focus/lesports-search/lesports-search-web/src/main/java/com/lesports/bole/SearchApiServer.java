package com.lesports.bole;

import com.lesports.jersey.AbstractJerseyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SearchApiServer extends AbstractJerseyServer {

    private static final Logger LOG = LoggerFactory.getLogger(SearchApiServer.class);
    private static final int DEFAULT_PORT = 9380;

    public static void main(final String[] args) throws Exception {

        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);

        SearchApiApplication app = new SearchApiApplication();

        runServer(port, "/search/v1/", app);
    }
}
