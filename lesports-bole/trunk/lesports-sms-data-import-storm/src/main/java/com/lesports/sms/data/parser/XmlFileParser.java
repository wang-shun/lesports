package com.lesports.sms.data.parser;

import backtype.storm.topology.BasicOutputCollector;
import com.lesports.sms.data.job.DataImportJob;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.sms.data.utils.NamedClassLoader;
import com.lesports.utils.xml.Parser;
import com.lesports.utils.xml.ParserFactory;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
public class XmlFileParser extends AbstractFileParser implements FileParser {

    @Override
    public List<Object> doExecute(DataImportJob job, FileInputStream inputStream, BasicOutputCollector collector) throws Exception {
        //通过文件名称获取文件解析模板
        Class clazz = NamedClassLoader.newInstance().getClazz(job.xmlTemplate());
        if (clazz != null) {
            Parser<XmlTemplate> parser = new ParserFactory().createXmlParser(clazz);
            //使用模板将文件流转为对象并发出
            XmlTemplate xmlTemplate = parser.parse(inputStream);
            List<Object> objects = xmlTemplate.getData();
            return objects;
        }
        return Collections.emptyList();
    }
}
