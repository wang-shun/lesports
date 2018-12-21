package com.lesports.sms.data.generator.olympic.test;

import com.google.common.collect.Lists;
import com.lesports.sms.data.generator.FileGenerator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/7/19.
 */
public class TestFileListGenerator implements FileGenerator {
    @Override
    public List<String> getFileUrl() {

        List<String> fileList = null;
        try {
            fileList = FileUtils.readLines(new File("d:/新建文本文档.txt"));

            return fileList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
