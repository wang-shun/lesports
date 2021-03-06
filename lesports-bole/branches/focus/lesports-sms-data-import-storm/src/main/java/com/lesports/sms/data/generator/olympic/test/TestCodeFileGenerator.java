package com.lesports.sms.data.generator.olympic.test;

import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.FileGenerator;

import java.io.File;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class TestCodeFileGenerator implements FileGenerator {
    @Override
    public List<String> getFileUrl() {
        String path = "D:/Codes";
        File file = new File(path);
        File[] tempList = file.listFiles();
        List<String> fileList = Lists.newArrayList();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                fileList.add("file:///" + tempList[i].getAbsolutePath());
            }
        }
        return fileList;
    }
}
