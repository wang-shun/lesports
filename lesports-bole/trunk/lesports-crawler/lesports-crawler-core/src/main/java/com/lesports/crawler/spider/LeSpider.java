package com.lesports.crawler.spider;

import com.lesports.crawler.processor.ProcessorRegistry;
import com.lesports.crawler.utils.SpringUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * LeSpider
 * 
 * @author denghui
 *
 */
public class LeSpider extends Spider {

    private ProcessorRegistry processorRegistry = SpringUtils.getBean(ProcessorRegistry.class);

    public LeSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    /**
     * 另为每次处理设置Site
     */
    protected void processRequest(Request request) {
        site = processorRegistry.getSiteForUrl(request.getUrl());
        super.processRequest(request);
    }

    public static LeSpider create(PageProcessor pageProcessor) {
        return new LeSpider(pageProcessor);
    }
}
