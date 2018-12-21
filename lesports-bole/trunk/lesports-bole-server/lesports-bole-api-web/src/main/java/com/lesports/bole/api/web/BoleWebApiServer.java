package com.lesports.bole.api.web;

import com.lesports.jersey.AbstractJerseyServer;

/**
 * BoleWebApiServer
 * @author denghui
 *
 */
public class BoleWebApiServer extends AbstractJerseyServer {

    private static final int DEFAULT_PORT = 8413;

    public static void main(final String[] args) throws Exception {

        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);

        BoleWebApiApplication app = new BoleWebApiApplication();

        runServer(port, "/bole/v1/", app);
    }
}
