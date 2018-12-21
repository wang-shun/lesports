package com.lesports.sms.data.topology;

import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import com.lesports.sms.data.bolt.generator.FileGeneratorBolt;
import com.lesports.sms.data.bolt.parser.FileParserBolt;
import com.lesports.sms.data.bolt.processor.BeanProcessorBolt;
import com.lesports.sms.data.job.JobConfigLoader;
import com.lesports.storm.config.LesportsQuartzConfig;
import com.lesports.storm.spout.QuartzSpout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-17
 */
public class SmsDataImportTopology {
    private static final Logger LOG = LoggerFactory.getLogger(SmsDataImportTopology.class);

    public static void main(String[] args) throws Exception {


        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("olympics_spout", new QuartzSpout(), 1);
        builder.setBolt("file_generator", new FileGeneratorBolt(), 1).shuffleGrouping("olympics_spout");
        builder.setBolt("file_parser", new FileParserBolt(), 10).shuffleGrouping("file_generator");
        builder.setBolt("bean_processor", new BeanProcessorBolt(), 10).shuffleGrouping("file_parser");

        LesportsQuartzConfig conf = new LesportsQuartzConfig();
        conf.addJobs(JobConfigLoader.getAllJobs());

        if (args != null && args.length > 0) {
            conf.setNumWorkers(1);

            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {
            conf.setMaxTaskParallelism(1);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

        }
    }
}
