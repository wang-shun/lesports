package com.lesports.crawler.processor;

import org.junit.Test;

import com.lesports.crawler.repo.AbstractIntegrationTest;
import com.lesports.utils.LeProperties;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-11-28
 */
public class QQLiveListPageProcessorTest extends AbstractIntegrationTest {
    private final static String url = LeProperties.getString("chrome.driver.path", "/Users/pangchuanxiao/chromedriver");

    @Test
    public void testDoProcess() throws Exception {
//        Spider spider = Spider.create(new ProcessorDispatcher()
//                .addPageProcessor(new QQLiveListPageProcessor()))
//                .addPipeline(new DataAttachPipeline()
//                        .addHandler(new ListDataAttachHandler())
//                        .addHandler(new LiveDataAttachHandler())
//                        .addHandler(new QQListDataAttachHandler()))
//                .addPipeline(new FetchedPipeline())
//                .setExitWhenComplete(false)
//                        //开启5个线程抓取
//                .thread(1);
//        spider.setEmptySleepTime(1);
//        spider.addUrl("http://sports.qq.com/");
//        spider.run();
    }
}
