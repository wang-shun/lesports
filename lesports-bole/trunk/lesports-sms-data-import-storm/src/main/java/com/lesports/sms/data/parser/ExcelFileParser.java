package com.lesports.sms.data.parser;

import backtype.storm.topology.BasicOutputCollector;
import com.google.common.collect.Lists;
import com.lesports.sms.data.job.DataImportJob;
import com.lesports.sms.data.model.olympic.CommonCode;
import com.lesports.utils.excel.ExcelParser;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
public class ExcelFileParser extends AbstractFileParser implements FileParser {

    @Override
    public List<Object> doExecute(DataImportJob job, FileInputStream inputStream, BasicOutputCollector collector) throws Exception {
        List objects = ExcelParser.parse(inputStream, CommonCode.class,0);
        return objects;
    }
}
