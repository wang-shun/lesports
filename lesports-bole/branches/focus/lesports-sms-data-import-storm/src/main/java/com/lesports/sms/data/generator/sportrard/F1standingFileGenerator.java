package com.lesports.sms.data.generator.sportrard;

import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;
import com.lesports.sms.data.generator.FileGenerator;

import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class F1standingFileGenerator implements FileGenerator {


    public List<String> getFileUrl() {
        List<String> fileNames = Lists.newArrayList();

        String fileName = "ftp://" + "Sport" + ":" + "40a38cb71" + "@" +
                "10.120.56.176" + ":" + "65000" + "/Sport/copy/outright_Motorsport/Formula1/" + "outright_Motorsport.Formula1.Formula12016.xml";
        fileNames.add(fileName);
        return fileNames;
    }

}