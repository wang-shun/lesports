package com.lesports.crawler.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lesports.crawler.MsgQueue;
import com.lesports.crawler.model.FetchedRequest;
import com.lesports.crawler.model.RequestMessage;
import com.lesports.crawler.processor.ProcessorRegistry;
import com.lesports.crawler.repository.FetchedRepository;
import com.lesports.crawler.utils.SpringUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public class PriorityMsgQueueScheduler implements Scheduler {
    private static final Logger LOG = LoggerFactory.getLogger(PriorityMsgQueueScheduler.class);

    private MsgQueue msgQueue = SpringUtils.getBean(MsgQueue.class);
    private FetchedRepository fetchedRepository = SpringUtils.getBean(FetchedRepository.class);
    private ProcessorRegistry processorRegistry = SpringUtils.getBean(ProcessorRegistry.class);;

    @Override
    public void push(Request request, Task task) {
        try {
            RequestMessage requestMessage = RequestMessage.newInstance(request, task);
            msgQueue.push(requestMessage);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    @Override
    public Request poll(Task task) {
        try {
            RequestMessage liveMessage = msgQueue.poll();
            if (null == liveMessage) {
                return null;
            }
            // 没有启用则不下载页面
            if (!processorRegistry.hasEnabledProcessorForUrl(liveMessage.getUrl())) {
                return null;
            }
            FetchedRequest fetchedRequest = new FetchedRequest();
            fetchedRequest.setId(liveMessage.getUrl());
            fetchedRequest.setStatus(FetchedRequest.Status.FETCHING);
            fetchedRepository.save(fetchedRequest);
            return liveMessage.toRequest();
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }

        return null;
    }
}
