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
@Repository
public class BjRedisCache extends AbstractRedisCache {
    private static final Logger LOG = LoggerFactory.getLogger(BjRedisCache.class);
    public static final RedisClient REDIS_CLIENT = RedisClientFactory.getRedisClient("web_clean_url_cache");

    @Override
    RedisClient getClient() {
        return REDIS_CLIENT;
    }

}
