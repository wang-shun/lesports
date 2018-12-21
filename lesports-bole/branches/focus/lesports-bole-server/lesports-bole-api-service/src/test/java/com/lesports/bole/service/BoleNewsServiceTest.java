package com.lesports.bole.service;

import com.lesports.AbstractIntegrationTest;
import com.lesports.bole.api.vo.TBNews;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
public class BoleNewsServiceTest extends AbstractIntegrationTest {

    @Resource
    private BoleNewsService boleNewsService;

    @Test
    public void testGetTBCompetitorById() throws Exception {
        TBNews news = boleNewsService.getById(483044l);
        Assert.notNull(news);
    }
}