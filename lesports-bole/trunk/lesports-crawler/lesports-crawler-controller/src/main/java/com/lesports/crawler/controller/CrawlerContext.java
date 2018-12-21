package com.lesports.crawler.controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.lesports.crawler.component.filter.MongoDuplicateFilter;
import com.lesports.crawler.component.filter.RegexConfigFilter;
import com.lesports.crawler.component.priority.PriorityProcessor;
import com.lesports.crawler.listener.ControllerFinishListener;
import com.lesports.crawler.repository.SeedRepository;
import com.lesports.crawler.scheduler.BoleMatchAutoInvalidTask;
import com.lesports.crawler.scheduler.SeedLoadSchedulerTask;
import com.lesports.crawler.scheduler.SuspendRequestSchedulerTask;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Component
public class CrawlerContext {
    @Resource
    private Controller controller;
    @Resource
    private SeedRepository seedRepository;
    
    @PostConstruct
    public void start() {
        PriorityProcessor priorityProcessor = new PriorityProcessor();
        controller.setPriorityProcessor(priorityProcessor);
        controller.addFilter(new MongoDuplicateFilter());
        controller.addFilter(new RegexConfigFilter());
        controller.addSchedulerTask(new SeedLoadSchedulerTask());
        controller.addSchedulerTask(new SuspendRequestSchedulerTask());
        controller.addSchedulerTask(new BoleMatchAutoInvalidTask());
        controller.setFinishListener(new ControllerFinishListener());
        controller.run();
    }
}
