package com.lesports.sms.data.bolt.processor;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.alibaba.fastjson.JSONObject;
import com.lesports.sms.data.job.DataImportJob;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.NamedClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-17
 * <p/>
 * Convert parsed bean to mongodb bean.
 */
public class BeanProcessorBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(BeanProcessorBolt.class);

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        DataImportJob job = (DataImportJob)input.getValueByField(BOLT_FIELD_JOB);
        Object object = input.getValueByField(BOLT_FIELD_PARSER_RESULT);
        String code = input.getStringByField(BOLT_FIELD_CODE);
        BeanProcessor processor = NamedClassLoader.newInstance().getBean(job.processor());
        Boolean res = processor.process(code, object);
        LOG.info("process bean result : {}, processor : {}, object type : {}, content : {}.", res, processor.getClass(), object.getClass(), JSONObject.toJSONString(object));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
