package com.lesports.elastic.job;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.AbstractThroughputDataFlowElasticJob;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2016/1/29
 */
public class MyElasticJob extends AbstractThroughputDataFlowElasticJob<String> {
    @Override
    public List<String> fetchData(JobExecutionMultipleShardingContext jobExecutionMultipleShardingContext) {
        Map<Integer, String> offset = jobExecutionMultipleShardingContext.getOffsets();
        List<String> result = Lists.newArrayList("hello", "world", "!");// get data from database by sharding items and by offset
        return result;
    }

    @Override
    public boolean processData(JobExecutionMultipleShardingContext jobExecutionMultipleShardingContext, String s) {
        for (int each : jobExecutionMultipleShardingContext.getShardingItems()) {
            updateOffset(each, "your offset, maybe id");
        }
        return false;
    }

    @Override
    public boolean isStreamingProcess() {
        return false;
    }
}
