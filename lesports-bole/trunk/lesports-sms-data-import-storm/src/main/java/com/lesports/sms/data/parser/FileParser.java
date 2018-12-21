package com.lesports.sms.data.parser;

import backtype.storm.topology.BasicOutputCollector;
import com.lesports.sms.data.job.DataImportJob;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
public interface FileParser extends Serializable {

    public List<Object> doExecute(DataImportJob job,  FileInputStream inputStream, BasicOutputCollector collector) throws Exception;

}
