package com.lesports.bole.repository;

import com.lesports.bole.model.BoleMatch;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/20
 */
public class BoleMatchRepositoryTest extends AbstractIntegrationTest {

    @Resource
    private BoleMatchRepository boleMatchRepository;

    @Test
    public void testMatchByStartTimeAndCompetitors() throws Exception {
        BoleMatch boleMatch = boleMatchRepository.matchByStartTimeAndCompetitors("20160205090000", 191041, 167041);
        Assert.notNull(boleMatch);
    }
}