package com.lesports.bole.server;

import com.lesports.bole.api.service.TBoleApiService;
import com.lesports.bole.api.service.TBoleInternalService;
import com.lesports.bole.thrift.TBoleApiServiceAdapter;
import com.lesports.bole.thrift.TBoleInternalServiceAdapter;
import me.ellios.hedwig.rpc.core.ServiceConfig;
import me.ellios.hedwig.rpc.core.ServiceSchema;
import me.ellios.hedwig.rpc.core.ServiceType;
import me.ellios.hedwig.rpc.server.HedwigServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TBoleServer {
    private static final Logger LOG = LoggerFactory.getLogger(TBoleServer.class);

    private static final int DEFAULT_PORT = 8412;

    public static void main(String... args) {
        int port = parsePortFromCommandLineArguments(DEFAULT_PORT, args);
        LOG.info("Ready to start server on port: {}", port);
        HedwigServer server = HedwigServer.getServer();

        ServiceConfig config = ServiceConfig.newBuilder()
                .serviceFace(TBoleInternalService.Iface.class)
                .serviceImpl(TBoleInternalServiceAdapter.class)
                .port(port)
                .type(ServiceType.THRIFT)
                .build();

        server.registerService(config);

        ServiceConfig httpConfig = ServiceConfig.newBuilder()
                .serviceFace(TBoleInternalService.Iface.class)
                .serviceImpl(TBoleInternalServiceAdapter.class)
                .port(port + 1)
                .type(ServiceType.THRIFT)
                .schema(ServiceSchema.HTTP)
                .build();

        server.registerService(httpConfig);

        ServiceConfig apiConfig = ServiceConfig.newBuilder()
                .serviceFace(TBoleApiService.Iface.class)
                .serviceImpl(TBoleApiServiceAdapter.class)
                .port(port)
                .type(ServiceType.THRIFT)
                .build();

        server.registerService(apiConfig);

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
