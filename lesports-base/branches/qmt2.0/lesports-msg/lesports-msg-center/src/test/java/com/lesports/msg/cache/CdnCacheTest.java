package com.lesports.msg.cache;

import com.lesports.msg.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/27
 */
public class CdnCacheTest extends AbstractIntegrationTest {

    @Test
    public void testDelete() throws Exception {
        boolean result = CdnCacheApis.delete("http://static.api.sports.letv.com/sis-web/app/match/get?id=15944&version=1.0");
        Assert.isTrue(result);
    }
}