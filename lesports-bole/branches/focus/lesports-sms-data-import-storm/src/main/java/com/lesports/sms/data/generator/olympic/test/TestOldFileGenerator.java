package com.lesports.sms.data.generator.olympic.test;

import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.FileGenerator;
import com.lesports.sms.data.utils.FtpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class TestOldFileGenerator implements FileGenerator {
    public String statsHttpUrl = "http://beitai-LeTV:letv2015@downloads.stats.com/beitai-LeTv_archive/";

    @Override
    public List<String> getFileUrl() {
        List<String> fileNames = Lists.newArrayList();
        String fileName = "D://oldFiles.txt";
        File file = new File(fileName);
        try {
            List<String> files = FileUtils.readLines(file);
            if (CollectionUtils.isNotEmpty(files)) {
                for (String name : files) {
                    fileNames.add(statsHttpUrl + name);
                }
            }
        } catch (Exception e) {
            e.toString();
        }
        return fileNames;
    }
}
