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
public class NgxApiMemCacheTDXY extends AbstractMemCache {
    private static final Logger LOG = LoggerFactory.getLogger(NgxApiMemCacheTDXY.class);
    public static MemcachedOp MEMCACHED_CLIENT;

    static {
        try {
            MEMCACHED_CLIENT = HedwigMemcachedClientFactory.getMemcachedClient("ngx_sms_cache_tdxy");
        } catch (Exception e) {
            LOG.error("Fail to init memcached ngx_sms_cache_tdxy");
        }
    }

    @Override
    MemcachedOp memcachedOp() {
        return MEMCACHED_CLIENT;
    }

    @Override
    String memcachedName() {
        return "BEIJING-TDXY-API-NGINX";
    }

    @Override
    Logger logger() {
        return LOG;
    }
}
