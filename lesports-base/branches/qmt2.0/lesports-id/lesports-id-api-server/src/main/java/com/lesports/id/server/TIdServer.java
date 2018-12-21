package com.lesports.id.server;

import com.lesports.id.api.TIdService;
import com.lesports.id.service.thrift.TIdServiceAdapter;
import me.ellios.hedwig.rpc.core.ServiceConfig;
import me.ellios.hedwig.rpc.core.ServiceSchema;
import me.ellios.hedwig.rpc.core.ServiceType;
import me.ellios.hedwig.rpc.server.HedwigServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TIdServer {
    private static final Logger LOG = LoggerFactory.getLogger(TIdServer.class);

    private static final int DEFAULT_PORT = 8412;

    public static void main(String... args) {
        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);
        LOG.info("Ready to start server on port: {}", port);
        HedwigServer server = HedwigServer.getServer();

        ServiceConfig config = ServiceConfig.newBuilder()
                .serviceFace(TIdService.Iface.class)
                .serviceImpl(TIdServiceAdapter.class)
                .port(port)
                .type(ServiceType.THRIFT)
                .build();
        server.registerService(config);


        ServiceConfig httpConfig = ServiceConfig.newBuilder()
                .serviceFace(TIdService.Iface.class)
                .serviceImpl(TIdServiceAdapter.class)
                .port(port + 1)
                .type(ServiceType.THRIFT)
                .schema(ServiceSchema.HTTP)
                .build();
        server.registerService(httpConfig);
        ThreadLocal<String> bb = new ThreadLocal<>();
        bb.get();

        server.start();
    }


    public static int parsePortFromCommandLineArguments(int defaultPort, String... args) {
        int port = defaultPort;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
            if (port <= 0) {
                port = defaultPort;
            }
        }
        return port;
    }
}
