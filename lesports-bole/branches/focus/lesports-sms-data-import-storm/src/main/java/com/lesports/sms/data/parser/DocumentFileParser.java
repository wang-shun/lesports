package com.lesports.sms.data.parser;

import backtype.storm.topology.BasicOutputCollector;
import com.google.common.collect.Lists;
import com.lesports.sms.data.job.DataImportJob;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
public class DocumentFileParser extends AbstractFileParser implements FileParser {

    @Override
    public List<Object> doExecute(DataImportJob job, FileInputStream inputStream, BasicOutputCollector collector) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        List documents = Lists.newArrayList(document);
        return documents;
    }
}
