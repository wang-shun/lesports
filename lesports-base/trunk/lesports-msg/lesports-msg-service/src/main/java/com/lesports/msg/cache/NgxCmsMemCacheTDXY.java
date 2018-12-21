package com.lesports.msg.cache;

import me.ellios.memcached.HedwigMemcachedClientFactory;
import me.ellios.memcached.MemcachedOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
@Repository
public class NgxCmsMemCacheTDXY extends AbstractMemCache {
    private static final Logger LOG = LoggerFactory.getLogger(NgxCmsMemCacheTDXY.class);
    public static MemcachedOp MEMCACHED_CLIENT;

    static {
        try {
            MEMCACHED_CLIENT = HedwigMemcachedClientFactory.getMemcachedClient("ngx_cms_cache_tdxy");
        } catch (Exception e) {
            LOG.error("Fail to init memcached ngx_cms_cache_tdxy");
        }
    }

    @Override
    MemcachedOp memcachedOp() {
        return MEMCACHED_CLIENT;
    }

    @Override
    String memcachedName() {
        return "BEIJING-TDXY-CMS-NGINX";
    }

    @Override
    Logger logger() {
        return LOG;
    }
}
