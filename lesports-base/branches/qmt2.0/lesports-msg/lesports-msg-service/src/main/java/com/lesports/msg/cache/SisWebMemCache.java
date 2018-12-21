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
public class SisWebMemCache extends AbstractMemCache{
    private static final Logger LOG = LoggerFactory.getLogger(SisWebMemCache.class);
    public static final MemcachedOp MEMCACHED_CLIENT = HedwigMemcachedClientFactory.getMemcachedClient("web_old_sis");

    @Override
    MemcachedOp memcachedOp() {
        return MEMCACHED_CLIENT;
    }

    @Override
    String memcachedName() {
        return "BEIJING-SIS";
    }

    @Override
    Logger logger() {
        return LOG;
    }
}
