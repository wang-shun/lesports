package com.lesports.sms.data.bolt.generator;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.lesports.sms.data.generator.FileGenerator;
import com.lesports.sms.data.job.DataImportJob;
import com.lesports.sms.data.job.JobConfigLoader;
import com.lesports.sms.data.utils.NamedClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-17
 * <p>
 * Get the files that need to download from ftp. This will emit a file name one time.
 */
public class FileGeneratorBolt extends BaseBasicBolt {
    private static Logger LOG = LoggerFactory.getLogger(FileGeneratorBolt.class);

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String jobName = input.getStringByField(BOLT_FIELD_JOB_NAME);
        LOG.info("Trigger one file generator : {}", jobName);
        DataImportJob job = JobConfigLoader.getJob(jobName);
        FileGenerator fileGenerator = NamedClassLoader.newInstance().getBean(job.fileGenerator());
        if (null != fileGenerator) {
            List<String> fileUrls = fileGenerator.getFileUrl();
            for (String fileUrl : fileUrls) {
                collector.emit(new Values(job, fileUrl));
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(BOLT_FIELD_JOB, BOLT_FIELD_FILE_URI));
    }
}
