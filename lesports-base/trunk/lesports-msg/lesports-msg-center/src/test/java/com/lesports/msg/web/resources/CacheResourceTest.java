package com.lesports.msg.web.resources;

import com.lesports.msg.AbstractIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author pangchuanxiao
 * @since 2015/8/25
 */
public class CacheResourceTest extends AbstractIntegrationTest {
    @Resource
    private CacheResource cacheResource;

    @Test
    public void testClean() throws Exception {
//        cacheResource.clean("http://static.api.sports.letv.com/sis-web/app/match/getMatchStatusByMatchIds?from=8&version=1.0&build_model=X800&build_version=5.0.2&system_name=Android&ids=31561");
        cacheResource.clean("http%3a%2f%2fapi.sports.letv.com%2fsms%2fv1%2frefresh%2fepisodes%3fcaller%3d1001%26ids%3d115715005%26caller%3d1001", true, true);

    }
}