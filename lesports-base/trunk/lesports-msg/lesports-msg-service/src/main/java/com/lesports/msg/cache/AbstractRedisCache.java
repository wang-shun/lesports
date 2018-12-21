package com.lesports.msg.cache;

import me.ellios.jedis.RedisClient;
import me.ellios.jedis.RedisClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by pangchuanxiao on 2015/6/30.
 */
public abstract class AbstractRedisCache {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRedisCache.class);
    abstract RedisClient getClient();

    public boolean delete(String key) {
        boolean result = getClient().del(key);
        LOG.info("delete key : {} in redis.", key);
        return result;
    }

    public Set<String> smembers(String key) {
        Set<String> ids = getClient().smembers(key);
        if (null == ids) {
            return null;
        }
        LOG.info("get {} ids for key : {}.", ids.size(), key);
        return ids;
    }

    public void srem(String key, String... keys) {
        try {
            if (null == key) {
                return;
            }
            if (null == keys) {
                return;
            }
            Long num = getClient().srem(key, keys);
            if (null == num) {
                return;
            }
            LOG.info("delete {} keys for key : {}, keys : {}.", num, key, keys);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }

    }

}
