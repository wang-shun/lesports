package com.lesports.sms.data.generator.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;

import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class PhaseFileGenerator extends AbstractStatsFileGenerator {
    @Override
    public List<String> getFileName() {
        return Lists.newArrayList("DT_CODES_PHASE.xml");
    }
}
