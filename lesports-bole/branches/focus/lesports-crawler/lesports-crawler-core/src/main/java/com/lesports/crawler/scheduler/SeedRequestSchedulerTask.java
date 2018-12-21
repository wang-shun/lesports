package com.lesports.crawler.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lesports.crawler.MsgQueue;
import com.lesports.crawler.component.priority.Priority;
import com.lesports.crawler.model.RequestMessage;
import com.lesports.crawler.model.SeedRequest;
import com.lesports.crawler.utils.SpringUtils;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/24
 */
public class SeedRequestSchedulerTask extends SchedulerTask {
    private static final Logger LOG = LoggerFactory.getLogger(SeedRequestSchedulerTask.class);
    private MsgQueue msgQueue = SpringUtils.getBean(MsgQueue.class);
    private SeedRequest seedRequest;

    public SeedRequestSchedulerTask(SeedRequest seedRequest) {
        this.seedRequest = seedRequest;
        if (seedRequest.getFrequency() != null) {
            this.frequency = seedRequest.getFrequency();
        }
    }

    @Override
    public void run() {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setUrl(seedRequest.getId());
        requestMessage.setPriority(Priority.HIGH.getValue());
        msgQueue.push(requestMessage);
        LOG.info("inject seed : {}, freq: {}", requestMessage.getUrl(), this.frequency);
    }
}
