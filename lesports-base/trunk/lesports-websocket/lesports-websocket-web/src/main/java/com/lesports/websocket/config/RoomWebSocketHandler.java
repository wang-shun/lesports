package com.lesports.websocket.config;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.namespace.Namespace;
import com.lesports.room.client.RoomApis;
import com.lesports.websocket.domain.MessageData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * Created by zhangdeqiang on 2016/9/12.
 */
@Component
public class RoomWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RoomWebSocketHandler.class);
    public Namespace namespace;

    public RoomWebSocketHandler setNamespace(Namespace namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * 连接建立成功后回调
     */
    public void joinRoom(SocketIOClient client, MessageData data) throws Exception {
        String room = data.getRoomId();
        namespace.join(room, client.getSessionId());
        RoomApis.joinRoom(client.getSessionId().toString(), room);
    }

    public void sendMessage(String eventName, Object... data) {
        if (data == null || StringUtils.isBlank(eventName)) {
            return;
        }
        // 发送文本数据消息
        namespace.getBroadcastOperations().sendEvent(eventName, data);

    }

    public void sendJsonToUser(SocketIOClient client, String eventName, Object... data) {
        if (client == null || data == null || StringUtils.isBlank(eventName)) {
            return;
        }
        client.sendEvent(eventName, JSONObject.toJSONString(data));
    }

    public void sendObjectToUser(SocketIOClient client, String eventName, Object... data) {
        if (client == null || data == null || StringUtils.isBlank(eventName)) {
            return;
        }
        client.sendEvent(eventName, data);
    }

    public void leaveRoom(SocketIOClient client, MessageData data) {
        String room = data.getRoomId();
        leaveRoom(client, room);
    }

    public void leaveRoom(SocketIOClient client, String room) {
        LOG.info("Client {} leave room {}", client.getSessionId(), room);
        namespace.leave(room, client.getSessionId());
        RoomApis.leaveRoom(client.getSessionId().toString(), room);
    }


    public void leaveAllRoom(SocketIOClient client) {
        Set<String> rooms = namespace.getRooms(client);
        if (CollectionUtils.isEmpty(rooms)) {
            return;
        }
        for (String room : rooms) {
            // 默认namespace不处理
            if (!namespace.getName().equals(room)) {
                RoomApis.leaveRoom(client.getSessionId().toString(), room);
            }
        }
    }


}
