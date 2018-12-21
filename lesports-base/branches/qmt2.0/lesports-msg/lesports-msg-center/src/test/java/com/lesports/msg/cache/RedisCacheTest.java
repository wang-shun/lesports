package com.lesports.msg.cache;

import com.lesports.msg.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Set;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/9/16
 */
public class RedisCacheTest extends AbstractIntegrationTest {
    @Resource
    private BjRedisCache redisCache;

    @Test
    public void testDelete() throws Exception {
        redisCache.delete("id_18614005");
    }

    @Test
    public void testSmembers() throws Exception {
        Set<String> ids = redisCache.smembers("id_18614005");
        Assert.notEmpty(ids);
    }


}