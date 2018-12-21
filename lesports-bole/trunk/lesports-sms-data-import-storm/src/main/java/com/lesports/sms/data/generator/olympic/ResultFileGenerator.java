package com.lesports.sms.data.generator.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;
import com.lesports.sms.model.Match;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class ResultFileGenerator extends AbstractStatsFileGenerator {
    @Override
    public List<String> getFileName() {
        return getAllFiles("DT_RESULT");
    }
}
