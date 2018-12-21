package com.lesports.crawler.processor;

import org.junit.Test;

import com.lesports.crawler.repo.AbstractIntegrationTest;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/27
 */
public class QQListPageProcessorTest extends AbstractIntegrationTest {

    @Test
    public void testDoProcess() throws Exception {
//        Spider spider = Spider.create(new ProcessorDispatcher()
//                .addPageProcessor(new ZhibobaListPageProcessor())
//                .addPageProcessor(new ZhibobaLivePageProcessor())
//                .addPageProcessor(new QQLiveListPageProcessor()))
//                .setScheduler(new PriorityScheduler())
//                .addPipeline(new DataAttachPipeline()
//                        .addHandler(new ListDataAttachHandler())
//                        .addHandler(new LiveDataAttachHandler()))
//                .addPipeline(new FetchedPipeline())
//                .setDownloader(new SeleniumDownloader("d:/chromedriver.exe"))
//                .setExitWhenComplete(false)
//                        //开启5个线程抓取
//                .thread(1)
//                .addUrl("http://sports.qq.com/nba/");
//        spider.setEmptySleepTime(1);
//        spider.run();
    }
}