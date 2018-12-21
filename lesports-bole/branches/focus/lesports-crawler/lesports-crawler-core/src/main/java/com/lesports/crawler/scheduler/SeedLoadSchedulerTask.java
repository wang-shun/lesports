package com.lesports.crawler.scheduler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lesports.crawler.model.SeedRequest;
import com.lesports.crawler.repository.SeedRepository;
import com.lesports.crawler.utils.Constants;
import com.lesports.crawler.utils.SpringUtils;

/**
 * 定期加载种子
 * 
 * @author denghui
 *
 */
public class SeedLoadSchedulerTask extends SchedulerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedLoadSchedulerTask.class);
    private SeedRepository seedRepository = SpringUtils.getBean(SeedRepository.class);
    private ScheduledExecutorService scheduledExecutorService;
    
    public SeedLoadSchedulerTask() {
        super.frequency = Constants.SEED_LOAD_DELAY;
    }
    
    @Override
    public void run() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        
        // NOTE: 种子的frequency如果大于SEED_LOAD_DELAY，会在没到期前就被重新注入
        List<SeedRequest> seedRequests = seedRepository.getAll();
        for (SeedRequest seedRequest : seedRequests) {
            final SchedulerTask task = new SeedRequestSchedulerTask(seedRequest);
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        task.run();
                    } catch (Exception e) {
                        LOGGER.error("{}", e.getMessage(), e);
                    }
                }
            }, Constants.SCHEDULER_INIT_DELAY, task.getFrequency(), TimeUnit.SECONDS);
        }
    }

}
