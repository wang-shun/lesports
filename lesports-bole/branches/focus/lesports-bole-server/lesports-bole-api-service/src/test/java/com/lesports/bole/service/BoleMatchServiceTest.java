package com.lesports.bole.service;

import com.lesports.AbstractIntegrationTest;
import com.lesports.bole.api.vo.TBLive;
import com.lesports.bole.api.vo.TBMatch;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
public class BoleMatchServiceTest extends AbstractIntegrationTest {

    @Resource
    private BoleMatchService boleMatchService;

    @Test
    public void testGetTBMatchById() throws Exception {
        TBMatch tbMatch = boleMatchService.getTBMatchById(128044041);
        Assert.notNull(tbMatch);
    }

    @Test
    public void testGetLivesBySmsMatchId() {
        List<TBLive> tbLives = boleMatchService.getTLivesBySmsMatchId(24003);
        Assert.notEmpty(tbLives);
    }
}