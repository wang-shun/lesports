package com.lesports.websocket.config.namespace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.namespace.Namespace;
import com.lesports.msg.subpub.MessageListener;
import com.lesports.msg.subpub.SubPubMessage;
import com.lesports.msg.subpub.TLiveUpdate;
import com.lesports.room.api.vo.TRoom;
import com.lesports.room.client.RoomApis;
import com.lesports.websocket.common.type.EventNameType;
import com.lesports.websocket.common.type.NamespaceType;
import com.lesports.websocket.common.type.TextLiveClientEventType;
import com.lesports.websocket.common.type.TextLiveServerEventType;
import com.lesports.websocket.config.RoomWebSocketHandler;
import com.lesports.websocket.config.TextLiveHandler;
import com.lesports.websocket.domain.Ack;
import com.lesports.websocket.domain.MessageData;
import com.lesports.websocket.domain.TextLiveIndexVo;
import com.lesports.websocket.domain.TextLiveMessageVo;
import com.lesports.websocket.domain.room.RoomInfo;
import com.lesports.websocket.domain.tlive.PageInfoRequest;
import com.lesports.websocket.subpub.RedisMessageDispatcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 节目图文直播创建namespace及事件监听
 * Created by zhangdeqiang on 2016/9/6.
 */
@Component
public class TextLiveNamespace implements CommandLineRunner, MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(TextLiveNamespace.class);
    private Namespace namespace;
    private RoomWebSocketHandler roomWebSocketHandler;
    @Autowired
    private TextLiveHandler textLiveHandler;

    @Autowired
    private SocketIOServer server;

    @Override
    public void run(String... strings) throws Exception {
        this.init();
    }

    public void init() {
        final String name = NamespaceType.TEXT_LIVE.getValue();
        namespace = (Namespace) server.addNamespace("/" + name);
        roomWebSocketHandler = new RoomWebSocketHandler().setNamespace(namespace);
        RedisMessageDispatcher.getInstance().registerNamespace(name, this);

        namespace.addConnectListener(client ->
                {
                    LOG.debug("Client : {} connect.", client.getSessionId());
                }
        );

        namespace.addDisconnectListener(client ->
                {
                    LOG.debug("Client : {} disconnect.", client.getSessionId());
                    roomWebSocketHandler.leaveAllRoom(client);
                }
        );

        namespace.addEventListener(EventNameType.JOIN.getValue(),
                MessageData.class, (client, data, ackRequest) -> {
                    roomWebSocketHandler.joinRoom(client, data);
                    //加入聊天室
                    //连接成功将数据返回客户端
                    data.setData(new Ack(true));
                    roomWebSocketHandler.sendObjectToUser(client, EventNameType.JOIN.getValue(), data);
                });
        namespace.addEventListener(EventNameType.LEAVE.getValue(),
                MessageData.class, (client, data, ackRequest) -> {
                    roomWebSocketHandler.leaveRoom(client, data);
                    //离开聊天室
                    //连接成功将数据返回客户端
                    data.setData(new Ack(true));
                    roomWebSocketHandler.sendObjectToUser(client, EventNameType.JOIN.getValue(), data);
                });

        //获取直播室信息
        namespace.addEventListener(TextLiveServerEventType.LIVE_ROOM_INFO.getValue(),
                MessageData.class, (client, data, ackRequest) -> {
                    TRoom tRoom = RoomApis.getRoomInfo(data.getRoomId());
                    int online = 0;
                    if (null != tRoom) {
                        online = tRoom.getOnline();
                    }
                    RoomInfo roomInfo = new RoomInfo();
                    roomInfo.setOnline(online);
                    data.setData(roomInfo);
                    roomWebSocketHandler.sendObjectToUser(client, TextLiveClientEventType.LIVE_ROOM_INFO.getValue(), data);
                });

//        namespace.addEventListener(EpisodeServerEventType.GUEST_TEAM_LIKE.getValue(),
//                TeamLikeData.class, (client, data, ackRequest) -> {
//
//                });
//
//        namespace.addEventListener(EpisodeServerEventType.HOME_TEAM_LIKE.getValue(),
//                TeamLikeData.class, (client, data, ackRequest) -> {
//
//                });
//
//        namespace.addEventListener(EpisodeServerEventType.ANCHOR_UPDOWN.getValue(),
//                TeamLikeData.class, (client, data, ackRequest) -> {
//                    roomWebSocketHandler.sendObjectToUser(client, EpisodeClientEventType.ANCHOR_UPDOWN.getValue(), "success");
//                });

        namespace.addEventListener(TextLiveServerEventType.LATEST_INDEX.getValue(),
                PageInfoRequest.class, (client, data, ackRequest) -> {
                    TextLiveIndexVo textLiveIndexVo = textLiveHandler.getLatestIndex(data.getTliveId(), data.getSection());
                    roomWebSocketHandler.sendObjectToUser(client, TextLiveClientEventType.LATEST_INDEX.getValue(), textLiveIndexVo);
                });

        namespace.addEventListener(TextLiveServerEventType.LATEST_MESSAGE.getValue(),
                PageInfoRequest.class, (client, data, ackRequest) -> {
                    TextLiveMessageVo textLiveMessageVo = textLiveHandler.getLatestMessage(data.getTliveId(), data.getSection());
                    roomWebSocketHandler.sendObjectToUser(client, TextLiveClientEventType.LATEST_MESSAGE.getValue(), textLiveMessageVo);
                });

        namespace.addEventListener(TextLiveServerEventType.LATEST_PAGE.getValue(),
                PageInfoRequest.class, (client, data, ackRequest) -> {
                    List<TextLiveMessageVo> textLiveMessageVo = textLiveHandler.getLatestPage(data.getTliveId(), data.getSection());
                    roomWebSocketHandler.sendObjectToUser(client, TextLiveClientEventType.LATEST_PAGE.getValue(), textLiveMessageVo);
                });

        namespace.addEventListener(TextLiveServerEventType.PAGE_CONTENT.getValue(),
                PageInfoRequest.class, (client, data, ackRequest) -> {
                    List<TextLiveMessageVo> textLiveMessageVo = textLiveHandler.getPage(data.getTliveId(), data.getSection(), data.getPage());
                    roomWebSocketHandler.sendObjectToUser(client, TextLiveClientEventType.PAGE_CONTENT.getValue(), textLiveMessageVo);
                });

    }

    @Override
    public void onMessage(String room, SubPubMessage message) {
        String event = message.getEvent();
        // 最新消息广播
        if (TextLiveServerEventType.LATEST_MESSAGE.getValue().equals(event)) {
            String jsonObject = message.getData();
            TLiveUpdate data = JSONObject.parseObject(jsonObject, TLiveUpdate.class);
            TextLiveMessageVo textLiveMessageVo = textLiveHandler.getMessageById(data.getLiveMessageId());
            LOG.info("Send to room : {}, message : {}.", room, JSONObject.toJSONString(textLiveMessageVo));
            LOG.debug("Send to room : {}, clients : {}.", room, JSON.toJSONString(namespace.getRoomOperations(room).getClients()));
            namespace.getRoomOperations(room).sendEvent(message.getEvent(), textLiveMessageVo);
        }
    }
}
