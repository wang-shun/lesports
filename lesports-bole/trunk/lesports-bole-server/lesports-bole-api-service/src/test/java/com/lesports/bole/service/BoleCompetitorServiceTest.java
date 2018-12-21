package com.lesports.bole.service;

import com.lesports.AbstractIntegrationTest;
import com.lesports.bole.api.vo.TBCompetitor;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
public class BoleCompetitorServiceTest extends AbstractIntegrationTest {

    @Resource
    private BoleCompetitorService boleCompetitorService;

    @Test
    public void testGetTBCompetitorById() throws Exception {
        TBCompetitor tbCompetitor = boleCompetitorService.getTBCompetitorById(165349041);
        Assert.notNull(tbCompetitor);
    }
}