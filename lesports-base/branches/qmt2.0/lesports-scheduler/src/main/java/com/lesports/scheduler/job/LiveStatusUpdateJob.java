package com.lesports.scheduler.job;

import com.google.common.base.Stopwatch;
import com.lesports.qmt.sbc.client.QmtSbcLiveInternalApis;
import com.lesports.scheduler.job.support.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangxudong@le.com on 2016/2/15.
 */
@Component("liveStatusUpdateJob")
public class LiveStatusUpdateJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(LiveStatusUpdateJob.class);

    @Scheduled(cron = "* 0/1 * * * *")
    public void updateLiveStatus() {
        try {
            LOG.info("begin to sync updateLiveStatus");
            Stopwatch stopwatch = Stopwatch.createStarted();
            QmtSbcLiveInternalApis.schedulerUpdateLiveStatus();
            stopwatch.stop();
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            LOG.info("[LiveStatusUpdateJob] [updateLiveStatus] [elapsed:{}]", Long.valueOf(elapsed));
        } catch (Exception e) {
            LOG.error("fail to updateLiveStatus. error : {}", e.getMessage());
        }
    }
}
