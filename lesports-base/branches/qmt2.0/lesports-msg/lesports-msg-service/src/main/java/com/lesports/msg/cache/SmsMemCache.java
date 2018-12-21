package com.lesports.msg.cache;

import me.ellios.memcached.HedwigMemcachedClientFactory;
import me.ellios.memcached.MemcachedOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by pangchuanxiao on 2015/6/30.
 */
@Repository
public class SmsMemCache extends AbstractMemCache{
    private static final Logger LOG = LoggerFactory.getLogger(SmsMemCache.class);
    public static final MemcachedOp MEMCACHED_CLIENT = HedwigMemcachedClientFactory.getMemcachedClient("web_sms_cache");

    @Override
    MemcachedOp memcachedOp() {
        return MEMCACHED_CLIENT;
    }

    @Override
    String memcachedName() {
        return "BEIJING-SMS-API-WEB";
    }

    @Override
    Logger logger() {
        return LOG;
    }
}
