package com.lesports.room.thrift;

import com.lesports.room.api.service.TRoomService;
import com.lesports.room.api.vo.TRoom;
import com.lesports.utils.math.LeNumberUtils;
import me.ellios.jedis.RedisClient;
import me.ellios.jedis.RedisClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static me.ellios.hedwig.http.mediatype.ExtendedMediaType.APPLICATION_X_THRIFT;

/**
 * Created by lufei1 on 2015/9/15.
 */
@Service("thriftRoomService")
@Path("/r/room")
@Produces({APPLICATION_X_THRIFT})
public class TRoomAdapter implements TRoomService.Iface {
    private static final Logger LOG = LoggerFactory.getLogger(TRoomAdapter.class);
    private static final RedisClient redis = RedisClientFactory.getRedisClient("rpc_tlive_repo");

    @Override
    public TRoom getRoomInfo(String id) throws TException {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        long online = LeNumberUtils.toLong(redis.get(id));
        return fakeRoom(id, online);
    }

    @Override
    public TRoom joinRoom(String uid, String roomId) throws TException {
        if (StringUtils.isEmpty(roomId)) {
            return null;
        }
        long online = redis.incr(roomId);
        LOG.info("User : {} join room : {}, current online : {}.", uid, roomId, online);
        return fakeRoom(roomId, online);
    }

    @Override
    public TRoom leaveRoom(String uid, String roomId) throws TException {
        if (StringUtils.isEmpty(roomId)) {
            return null;
        }
        long online = redis.incrBy(roomId, -1);
        LOG.info("User : {} leave room : {}, current online : {}.", uid, roomId, online);
        return fakeRoom(roomId, online);
    }


    private TRoom fakeRoom(String id, long online) {
        TRoom tRoom = new TRoom();
        tRoom.setId(id);
        tRoom.setName("图文直播室" + id);
        tRoom.setOnline((int) online);
        return tRoom;
    }
}
