package com.lesports.bole.service.impl;

import com.lesports.bole.AbstractIntegrationTest;
import com.lesports.bole.service.BoleNewsService;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/11
 */
public class BoleNewsServiceTest extends AbstractIntegrationTest {
    @Resource
    private BoleNewsService boleNewsService;

    @Test
    public void testSave() throws Exception {
        boolean result = boleNewsService.save(64449044l);
        Assert.assertTrue(result);
    }
}