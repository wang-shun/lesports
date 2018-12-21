package com.lesports.room.client;

import com.lesports.room.api.service.TRoomService;
import com.lesports.room.api.vo.TRoom;
import me.ellios.hedwig.rpc.client.ClientBuilder;
import me.ellios.hedwig.rpc.core.ServiceType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: ellios
 * Time: 15-6-3 : 下午9:32
 */
public class RoomApis {

    private static final Logger LOG = LoggerFactory.getLogger(RoomApis.class);

    private static TRoomService.Iface roomService = new ClientBuilder<TRoomService.Iface>()
            .serviceType(ServiceType.THRIFT).serviceFace(TRoomService.Iface.class).build();

    public static TRoom getRoomInfo(String id) {
        try {
            return roomService.getRoomInfo(id);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    public static TRoom joinRoom(String uid, String roomId) {
        try {
            return roomService.joinRoom(uid, roomId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    public static TRoom leaveRoom(String uid, String roomId) {
        try {
            return roomService.leaveRoom(uid, roomId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

}
