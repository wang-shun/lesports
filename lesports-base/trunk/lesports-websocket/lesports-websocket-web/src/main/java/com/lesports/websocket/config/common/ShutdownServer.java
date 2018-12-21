package com.lesports.websocket.config.common;

import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by zhangdeqiang on 2016/9/21.
 */
@Component
public class ShutdownServer {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownServer.class);

    @Autowired
    private SocketIOServer server;

    @PreDestroy
    public void destroy() {
        LOG.info("===================shutdown server on destroy===================");
        server.stop();
    }
}
