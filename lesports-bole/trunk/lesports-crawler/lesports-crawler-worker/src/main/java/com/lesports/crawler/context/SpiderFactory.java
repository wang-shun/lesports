package com.lesports.crawler.context;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.lesports.crawler.listener.DefaultListener;
import com.lesports.crawler.pipeline.DataAttachPipeline;
import com.lesports.crawler.pipeline.FetchedPipeline;
import com.lesports.crawler.pipeline.handler.SourceMatchHandler;
import com.lesports.crawler.pipeline.handler.SourceNewsHandler;
import com.lesports.crawler.processor.ProcessorDispatcher;
import com.lesports.crawler.scheduler.PriorityMsgQueueScheduler;
import com.lesports.crawler.spider.LeSpider;

import us.codecraft.webmagic.SpiderListener;

/**
 * SpiderFactory
 * 
 * @author denghui
 *
 */
@Component
public class SpiderFactory {

    private static final int DEFAULT_THREAD_COUNT = 5;
    private static LeSpider spider;

    @Resource
    private ProcessorDispatcher processorDispatcher;

    /**
     * 获取Spider
     * 
     * @return
     */
    public synchronized LeSpider getInstance() {
        if (spider == null) {
            spider = createSpider();
        }
        return spider;
    }

    private LeSpider createSpider() {
        LeSpider spider = (LeSpider) LeSpider.create(processorDispatcher)
                //.addUrl("http://roll.sports.qq.com/")
                //.addUrl("http://news.zhibo8.cc/nba/")
                //.addUrl("http://sports.qq.com/nba/")
                //.addUrl("http://www.zhibo8.cc/")
                .setScheduler(new PriorityMsgQueueScheduler())
                .addPipeline(new DataAttachPipeline()
                        .addHandler(new SourceMatchHandler())
                        .addHandler(new SourceNewsHandler()))
                .addPipeline(new FetchedPipeline())
                .setSpiderListeners(Lists.<SpiderListener> newArrayList(new DefaultListener()))
                .setExitWhenComplete(false)
                .thread(DEFAULT_THREAD_COUNT);
        spider.setEmptySleepTime(1);

        return spider;
    }

}
