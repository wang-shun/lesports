package com.lesports.scheduler;

import com.lesports.jersey.AbstractJerseyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: ellios
 * Time: 13-11-28 : 上午11:57
 */

public class SchedulerServer extends AbstractJerseyServer {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerServer.class);
    private static final int DEFAULT_PORT = 9520;

    public static void main(final String[] args) throws Exception {

        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);

        SchedulerApplication app = new SchedulerApplication();

        runServer(port, "/scheduler/v1/", app);
    }
}
