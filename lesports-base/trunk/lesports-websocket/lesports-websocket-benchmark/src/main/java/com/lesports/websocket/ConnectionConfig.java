package com.lesports.websocket;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * lesports-base.
 *
 * @author pangchuanxiao
 * @since 2016/9/22
 */
@Component
public class ConnectionConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionConfig.class);
    private static final ExecutorService ex = Executors.newCachedThreadPool();
    @Value("${connection.num}")
    private int connectionNum;
    @Value("${connection.path}")
    private String path;

    @Bean
    public Socket run() throws URISyntaxException {
        for (int index = 0; index < connectionNum; index++) {
            final int num = index;
            Socket socket = IO.socket(path);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LOG.info("connect : {}, id : {}", num, socket.id());
                    Map<String, String> joinParam = new HashMap<>();
                    joinParam.put("roomId", "1004760003");
                    socket.emit("JOIN", joinParam);
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    LOG.info("disconnect : {}, id : {}", num, socket.id());
                }

            }).on("LATEST_MESSAGE", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    LOG.info("client {}, id : {} receive message : {}", num, socket.id(), JSONObject.valueToString(args));
                }

            });
            socket.connect();
        }
        return null;
    }
}
