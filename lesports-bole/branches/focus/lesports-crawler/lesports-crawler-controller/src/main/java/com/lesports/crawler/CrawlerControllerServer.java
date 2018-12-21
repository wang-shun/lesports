package com.lesports.crawler;

import com.lesports.jersey.AbstractJerseyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class CrawlerControllerServer extends AbstractJerseyServer {

    private static final Logger LOG = LoggerFactory.getLogger(CrawlerControllerServer.class);
    private static final int DEFAULT_PORT = 8213;

    public static void main(final String[] args) throws Exception {

        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);

        CrawlerApiApplication app = new CrawlerApiApplication();

        runServer(port, "/crawler/v1/", app);
    }
}
