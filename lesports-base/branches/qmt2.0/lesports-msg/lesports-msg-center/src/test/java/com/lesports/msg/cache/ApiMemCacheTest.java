package com.lesports.msg.cache;

import com.lesports.msg.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/28
 */
public class ApiMemCacheTest extends AbstractIntegrationTest {
    @Resource
    private NgxApiMemCache apiMemCache;

    @Test
    public void testDelete() throws Exception {
        boolean result = apiMemCache.delete("/sms/v1/matches/15944003", -1);
        Assert.isTrue(result);
    }
}
