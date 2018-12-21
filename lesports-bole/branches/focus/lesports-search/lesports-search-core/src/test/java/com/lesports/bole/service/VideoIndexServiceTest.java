package com.lesports.bole.service;

import com.google.common.collect.Lists;
import com.lesports.bole.AbstractIntegrationTest;
import com.lesports.bole.index.VideoIndex;
import com.lesports.bole.utils.PageResult;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/12
 */
public class VideoIndexServiceTest extends AbstractIntegrationTest {
    @Resource
    private VideoIndexService videoIndexService;

    @Test
    public void testFindByParams() throws Exception {
        /*PageResult<VideoIndex> newsIndexes = videoIndexService.findByParams("沃尔奎兹", null, 1, 2, Lists.newArrayList(""), 0, 10);
        Assert.notNull(newsIndexes);*/
    }
}