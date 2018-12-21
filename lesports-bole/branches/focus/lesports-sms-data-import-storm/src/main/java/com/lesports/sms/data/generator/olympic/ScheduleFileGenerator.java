package com.lesports.sms.data.generator.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.annotation.Tag;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
@Tag(Constants.TAG_GENERATOR_SCHEDULE)
public class ScheduleFileGenerator extends AbstractStatsFileGenerator {
    @Override
    public List<String> getFileName() {
        return getAllFiles("DT_SCHEDULE");
    }
}
