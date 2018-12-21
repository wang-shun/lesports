package com.lesports.scheduler.job;

import com.lesports.scheduler.job.support.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by chenyongjie on 2016/1/27.
 */
@Component("actionLogStatsJob")
public class ActionLogStatsJob extends AbstractJob {

	private static final Logger LOG = LoggerFactory.getLogger(ActionLogStatsJob.class);

	/*@Scheduled(cron="0 10 1 * * *")
	public void statsAcionLog(){
		try {
			LOG.info("begin to sync statsAcionLog");
			UserServiceApiClient.statsAcionLog();
		} catch (Exception e) {
			LOG.error("fail to statsAcionLog. error : {}", e);
		}
	}*/
}
