package com.lesports.msg.service;

import com.lesports.msg.AbstractIntegrationTest;
import com.lesports.sms.api.vo.TMatch;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-9-24
 */
public class MatchServiceImplTest extends AbstractIntegrationTest {
    @Resource
    private MatchService matchService;


    @Test
    public void testDeleteMatchPage() throws Exception {
        matchService.deleteMatchPage(20040003l);
    }


    @Test
    public void testIndexMatch() throws Exception {

    }
}
