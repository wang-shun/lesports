package com.lesports.msg.cache;

import me.ellios.jedis.RedisClient;
import me.ellios.jedis.RedisClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by pangchuanxiao on 2015/6/30.
 */
@Repository
public class HkRedisCache extends AbstractRedisCache {
    private static final Logger LOG = LoggerFactory.getLogger(HkRedisCache.class);
    public static final RedisClient REDIS_CLIENT = RedisClientFactory.getRedisClient("web_clean_url_cache_hk");

    @Override
    RedisClient getClient() {
        return REDIS_CLIENT;
    }

}
