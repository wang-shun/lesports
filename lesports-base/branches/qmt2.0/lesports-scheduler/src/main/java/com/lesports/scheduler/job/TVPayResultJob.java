package com.lesports.scheduler.job;

import com.lesports.scheduler.job.support.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by chenyongjie on 2016/2/15.
 */
@Component("tvPayResultJob")
public class TVPayResultJob extends AbstractJob {

	private static final Logger LOG = LoggerFactory.getLogger(TVPayResultJob.class);

	/*@Scheduled(cron="* 0/30 * * * *")
	public void savePayResult(){
		try {
			LOG.info("begin to sync savePayResult");
			Stopwatch stopwatch = Stopwatch.createStarted();
			MemberVipCNApiClient.savePayResult();
			stopwatch.stop();
			long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
			LOG.info("[TVPayResultJob] [savePayResult] [elapsed:{}]", Long.valueOf(elapsed));
		} catch (Exception e) {
			LOG.error("fail to savePayResult. error : {}", e.getMessage());
		}
	}*/
}
