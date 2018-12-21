package com.lesports.crawler.processor;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/17
 */
@Component
public class ProcessorDispatcher implements PageProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorDispatcher.class);
    private Site site = Site.me();

    @Resource
    private ProcessorRegistry processorRegistry;

    @Override
    public void process(Page page) {
        Request request = page.getRequest();
        String url = request.getUrl();

        List<PageProcessor> pageProcessors = processorRegistry.getEnabledProcessorsForUrl(url);
        if (pageProcessors == null || pageProcessors.size() == 0) {
            LOG.info("NO enabled processor for url: {}", page.getUrl());
            return;
        }
        
        for (PageProcessor pageProcessor : pageProcessors) {
            try {
                pageProcessor.process(page);
                LOG.info("page : {} is processed by {}", page.getUrl(), pageProcessor.getClass());
            } catch (Exception e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
    
}
