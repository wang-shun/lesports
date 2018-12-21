package com.lesports.crawler;

import com.lesports.jersey.AbstractJerseyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class CrawlerWorkerServer extends AbstractJerseyServer {

    private static final Logger LOG = LoggerFactory.getLogger(CrawlerWorkerServer.class);
    private static final int DEFAULT_PORT = 8211;

    public static void main(final String[] args) throws Exception {

        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);

        CrawlerApiApplication app = new CrawlerApiApplication();

        runServer(port, "/crawler/v1/", app);
    }
}
