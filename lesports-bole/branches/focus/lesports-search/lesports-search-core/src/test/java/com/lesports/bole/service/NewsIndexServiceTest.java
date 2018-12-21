package com.lesports.bole.service;

import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.bole.AbstractIntegrationTest;
import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.utils.PageResult;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/12
 */
public class NewsIndexServiceTest extends AbstractIntegrationTest {

    @Resource
    private NewsIndexService newsIndexService;

    @Test
    public void testFindByParams() throws Exception {
        /*List<String> publishAt = Lists.newArrayList( "20150708135443");
        PageResult<NewsIndex> newsIndexes = newsIndexService.findByParams("遗憾", null, null, null, null, publishAt, 0, 0, 0, 10);
        Assert.notNull(newsIndexes);*/
    }
}