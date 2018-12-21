package com.lesports.sms.data.job;

import com.google.common.collect.Lists;
import com.lesports.storm.job.QuartzJob;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/21
 */
public class JobConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(JobConfigLoader.class);
    private static ConcurrentHashMap<String, DataImportJob> jobMap = new ConcurrentHashMap<>();
    private static final String PROPERTY_FILE_NAME = "jobs";

    public static List<QuartzJob> getAllJobs() {
        Map<String, String> jobConfigs = LeProperties.getAllString(PROPERTY_FILE_NAME);
        if (MapUtils.isEmpty(jobConfigs)) {
            return Collections.emptyList();
        }
        List<QuartzJob> result = Lists.newArrayListWithCapacity(jobConfigs.size());
        for (String name : jobConfigs.keySet()) {
            DataImportJob config = getJob(name);
            if (null == config) {
                continue;
            }
            result.add(config);
        }
        return result;
    }

    public static DataImportJob getJob(final String name) {
        try {
            String config = LeProperties.getString(PROPERTY_FILE_NAME, name, null);
            if (StringUtils.isEmpty(config)) {
                LOG.warn("Invalid job config : {} for : {}.", config, name);
                return null;
            }
            final String[] configs = StringUtils.split(config, "|");
            if (null == configs || configs.length != 4) {
                LOG.warn("Invalid job config : {} for : {}.", config, name);
                return null;
            }
            jobMap.putIfAbsent(name, new DataImportJob() {

                @Override
                public String getExpression() {
                    return configs[0];
                }

                @Override
                public String fileGenerator() {
                    return configs[1];

                }

                @Override
                public String xmlTemplate() {
                    return configs[2];

                }

                @Override
                public String processor() {
                    return configs[3];

                }

                @Override
                public String getJobName() {
                    return name;
                }

            });
            return jobMap.get(name);
        } catch (Exception e) {
            LOG.error("fail to get job config : {}, {}", name, e.getMessage(), e);
        }
        return null;
    }
}
