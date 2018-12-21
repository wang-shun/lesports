package com.lesports.sms.data.generator.sportrard;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.annotation.Tag;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;
import com.lesports.sms.data.generator.FileGenerator;
import com.lesports.sms.data.model.sportrard.SportrardConstants;
import com.lesports.sms.model.Match;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
@Tag(Constants.TAG_GENERATOR_SCHEDULE)
public class ScheduleFileGenerator implements FileGenerator {
    @Override
    public List<String> getFileUrl() {
        List<String> fileNames = Lists.newArrayList();

        String fileName = "ftp://" + "Sport" + ":" + "40a38cb71" + "@" +
                "10.120.56.176" + ":" + "65000" + "/Sport/" + "outrightschedule_Motorsport.Formula1.Formula12016.xml";
        fileNames.add(fileName);
        return fileNames;
    }


}
