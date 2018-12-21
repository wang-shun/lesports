package com.lesports.scheduler.job;

import com.lesports.scheduler.job.support.AbstractJob;
import com.lesports.utils.http.RestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * User: ellios
 * Time: 16-6-20 : 下午6:28
 */
@Component
public class MixedCacheUpdateJob extends AbstractJob{

    private static final Logger LOG = LoggerFactory.getLogger(ActionLogStatsJob.class);

    private static final String MIXED_UPDATE_CACHE_URL = "http://10.185.30.13:9172/sms/internal/v1/mixed/update";

    private static final RestTemplate REST_TEMPLATE = RestTemplateFactory.getTemplate();


    /*@Scheduled(cron="* *//*2 * * * *")
    public void statsAcionLog(){
        try {
            String s = REST_TEMPLATE.postForObject(MIXED_UPDATE_CACHE_URL, null, String.class);
            LOG.info("try to update mixed cache by url : {}, result : {}",
                    MIXED_UPDATE_CACHE_URL, s);
        } catch (RestClientException e) {
            LOG.error("fail to call url : {}", MIXED_UPDATE_CACHE_URL, e);
        }
    }*/
}
