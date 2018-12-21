package com.lesports.bole.service;

import com.lesports.AbstractIntegrationTest;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.bole.repository.BoleOlympicConfigRepository;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
public class BoleOlympicsServiceTest extends AbstractIntegrationTest {

    @Resource
    private BoleOlympicConfigService boleOlympicConfigService;

    @Resource
    BoleOlympicConfigRepository olympicConfigRepository;

    @Test
    public void testGetAll() throws Exception {
        List<BoleOlympicsLiveConfigSet> boleOlympicsLiveConfigSets = olympicConfigRepository.findAll();
        for (BoleOlympicsLiveConfigSet configSet : boleOlympicsLiveConfigSets) {
            System.out.println(configSet.getId());
        }
    }


    @Test
    public void testSave() throws Exception {
        BoleOlympicsLiveConfigSet bo = olympicConfigRepository.findOne(2689801000L);
        bo.setId(100370000L);
        olympicConfigRepository.save(bo);
    }
    @Test
    public void updateConfigGet() throws Exception {
        BoleOlympicsLiveConfigSet tbCompetitor = boleOlympicConfigService.findOne(2678801000L);
        tbCompetitor.setId(114894000L);
        boleOlympicConfigService.save(tbCompetitor);
    }
}