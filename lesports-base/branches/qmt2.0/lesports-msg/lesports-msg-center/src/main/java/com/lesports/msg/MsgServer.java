package com.lesports.msg;

import com.lesports.jersey.AbstractJerseyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: ellios
 * Time: 13-11-28 : 上午11:57
 */

public class MsgServer extends AbstractJerseyServer {

    private static final Logger LOG = LoggerFactory.getLogger(MsgServer.class);
    private static final int DEFAULT_PORT = 8211;

    public static void main(final String[] args) throws Exception {

        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);

        MsgApplication app = new MsgApplication();

        runServer(port, "/msg/v1/", app);
    }
}
