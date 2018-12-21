package com.lesports.sms.data.generator.olympic;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;
import com.lesports.sms.model.DictEntry;

import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class MedalFileGenerator extends AbstractStatsFileGenerator {
    @Override
    public List<String> getFileName() {
     return getAllFiles("DT_MEDAL");
    }
}
