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
public class FileListGenerator extends AbstractStatsFileGenerator {
    @Override
    public List<String> getFileName() {
        return Lists.newArrayList("FILE_LIST.xml");
    }
}
