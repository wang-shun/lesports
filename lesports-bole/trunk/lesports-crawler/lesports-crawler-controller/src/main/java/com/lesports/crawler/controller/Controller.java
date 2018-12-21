package com.lesports.crawler.controller;

import com.google.common.collect.Lists;
import com.lesports.crawler.MsgQueue;
import com.lesports.crawler.component.filter.FilterChain;
import com.lesports.crawler.component.filter.QueuedFilter;
import com.lesports.crawler.component.priority.Priority;
import com.lesports.crawler.component.priority.PriorityProcessor;
import com.lesports.crawler.model.RequestMessage;
import com.lesports.crawler.scheduler.SchedulerTask;
import com.lesports.crawler.utils.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.UrlUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
@Component
public class Controller implements Runnable, InjectTask {
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);
    @Resource
    private MsgQueue msgQueue;

    private ExecutorService EXECUTOR_SERVICE;
    private int threadNum = 1;
    private QueuedFilter filterChains = new QueuedFilter();
    private AtomicInteger stat = new AtomicInteger(STAT_INIT);
    private final static int STAT_INIT = 0;
    private final static int STAT_RUNNING = 1;
    private PriorityProcessor priorityProcessor;
    private List<DomainTask> domains = Lists.newArrayList();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    public final List<SchedulerTask> tasks = new ArrayList<>();
    private SpiderListener finishListener;

    public Controller() {
    }

    public SpiderListener getFinishListener() {
        return finishListener;
    }

    public void setFinishListener(SpiderListener finishListener) {
        this.finishListener = finishListener;
    }

    public void addSchedulerTask(SchedulerTask runnable) {
        tasks.add(runnable);
    }

    public Controller(int threadNum) {
        this.threadNum = threadNum;
    }

    public PriorityProcessor getPriorityProcessor() {
        return priorityProcessor;
    }

    public void setPriorityProcessor(PriorityProcessor priorityProcessor) {
        this.priorityProcessor = priorityProcessor;
    }

    public Controller init() {
        if (null == EXECUTOR_SERVICE) {
            EXECUTOR_SERVICE = Executors.newFixedThreadPool(threadNum);
        }
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (final SchedulerTask task : tasks) {
                scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            task.run();
                        } catch (Exception e) {
                            LOG.error("{}", e.getMessage(), e);
                        }
                    }
                }, Constants.SCHEDULER_INIT_DELAY, task.getFrequency(), TimeUnit.SECONDS);
            }
        }
        return this;
    }

    public Controller addFilter(FilterChain filterChain) {
        filterChains.addFilter(filterChain);
        return this;
    }

    @Override
    public void run() {
        checkRunningStat();
        init();
        EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    RequestMessage requestMessage = msgQueue.poll();
                    if (null == requestMessage) {
                        continue;
                    }
                    Request request = requestMessage.toRequest();
                    try {
                        Task task = requestMessage.toTask();
                        boolean filterResult = filterChains.isValid(request, task);
                        if (!filterResult) {
                            continue;
                        }
                        Priority priority = Priority.DEFAULT;
                        if (null != priorityProcessor) {
                            priority = priorityProcessor.process(requestMessage);
                        }
                        msgQueue.push(requestMessage, priority);
                        if (null != finishListener) {
                            finishListener.onSuccess(request);
                        }
                    } catch (Exception e) {
                        onError(requestMessage, e);
                        if (null != finishListener) {
                            finishListener.onError(request);
                        }
                    }
                }
            }
        });
    }

    private void onError(RequestMessage requestMessage, Exception e) {
        LOG.error("{}", e.getMessage(), e);
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    @Override
    public void inject(Request request, Task task, Priority priority) {
        RequestMessage requestMessage = RequestMessage.newInstance(request, task);
        if (null == priority) {
            priority = priorityProcessor.process(requestMessage);
        }
        msgQueue.push(requestMessage, priority);
    }

    public Controller addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        return this;
    }

    private Controller addRequest(Request request) {
        String domain = UrlUtils.getDomain(request.getUrl());
        if (StringUtils.isEmpty(domain)) {
            throw new RuntimeException("bad request url, no domain get.");
        }

        for (DomainTask domainTask : domains) {
            if (domainTask.getSite().getDomain().equals(domain)) {
                domainTask.getSite().addStartRequest(request);
                return this;
            }
        }
        Site site = Site.me();
        site.setDomain(domain);
        site.addStartRequest(request);
        DomainTask domainTask = new DomainTask(site);
        domains.add(domainTask);
        inject(request, domainTask, Priority.DEFAULT);
        return this;
    }
}
