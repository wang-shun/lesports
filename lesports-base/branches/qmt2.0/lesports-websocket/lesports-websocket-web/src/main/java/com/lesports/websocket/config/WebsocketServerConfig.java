package com.lesports.websocket.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WebsocketServerConfig {
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketServerConfig.class);

    @Value("${server.port}")
    private int port;

    @Value("${socket.ping.interval}")
    private int interval;

    @Value("${socket.tcp.keepalive}")
    private boolean keepalive;

    @Bean(name="webSocketServer")
    public SocketIOServer webSocketServer() throws InterruptedException {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        //config.setHostname("localhost");
        config.setPort(port);
        config.getSocketConfig().setReuseAddress(true);
        config.getSocketConfig().setTcpKeepAlive(true);
        config.setContext("/sms");
        config.setPingInterval(interval);
        final SocketIOServer server = new SocketIOServer(config);
        server.start();

        return server;

    }

}
