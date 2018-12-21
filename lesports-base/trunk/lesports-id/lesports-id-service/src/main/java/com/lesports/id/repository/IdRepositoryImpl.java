package com.lesports.id.repository;

import com.lesports.id.IDConstants;
import com.lesports.id.api.IdType;
import me.ellios.jedis.RedisClientFactory;
import me.ellios.jedis.RedisOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * User: ellios
 * Time: 15-6-1 : 下午9:21
 */
@Repository("idRepository")
public class IdRepositoryImpl implements IdRepository{

    public static final RedisOp REDIS_OP = RedisClientFactory.getRedisClient("rpc_id_repo");
    private static final Logger LOG = LoggerFactory.getLogger(IdRepositoryImpl.class);
    private static final String KEY_PREFIX = "LESPORTS_ID_";

    @Override
    public long nextId(IdType type) {
        String key = createKey(type);
        int tryCount = 0;
        while (tryCount++ < 3) {
            try {
                return REDIS_OP.incr(key);
            } catch (Exception e) {
                LOG.error("fail to incr key : {}, try again : {}, error : {},", key, tryCount, e.getMessage(), e);
            }
        }
        LOG.info("fail to incr key : {}, tryCount : {}", tryCount);
        return -1;
    }

    @Override
    public long nextId(IdType type, int step) {
        String key = createKey(type);
        int tryCount = 0;

        while (tryCount++ < 3) {
            try {
                return REDIS_OP.incrBy(key, step);
            } catch (Exception e) {
                LOG.error("fail to incr key : {}, step : {} try again : {}, error : {},", key, step, tryCount, e.getMessage(), e);
            }
        }
        LOG.info("fail to incr key : {}, step : {}, tryCount : {}", key, step, tryCount);
        return -1;
    }

    private String createKey(IdType type){
        return KEY_PREFIX + type.getValue();
    }
}
