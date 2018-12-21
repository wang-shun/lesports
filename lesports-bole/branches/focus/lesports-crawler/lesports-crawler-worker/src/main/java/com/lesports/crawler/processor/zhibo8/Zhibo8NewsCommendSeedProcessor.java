package com.lesports.crawler.processor.zhibo8;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.repository.SourceNewsRepository;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * NewsCommendSeedProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://cache.zhibo8.cc/$")
@Component
public class Zhibo8NewsCommendSeedProcessor extends AbstractPageProcessor<SourceNews[]> {

    // http://news.zhibo8.cc/zuqiu/2016-01-11/56932843de0d1.htm
    // http://cache.zhibo8.cc/json/2016_01_11/news/zuqiu/5693106c0dcd4_count.htm?key=0.30229930859059095
    private static final String URL_TEMPLATE = "http://cache.zhibo8.cc/json/%s/news/%s/%s_count.htm?key=%f";
    @Resource
    private SourceNewsRepository sourceNewsRepository;

    @Override
    protected SourceNews[] doProcess(Page page) {
        List<SourceNews> newsList = sourceNewsRepository.getInLastDay(getSource());
        for (SourceNews news : newsList) {
            Request request = new Request();
            String[] items = news.getSourceUrl().split("/");
            if (items.length < 6) {
                continue;
            }
            request.setUrl(String.format(URL_TEMPLATE, items[4].replace("-", "_"), items[3], items[5].replace(".htm", ""), Math.random()));
            request.putExtra("SourceId", news.getSourceId());
            page.addTargetRequest(request);
        }

        return null;
    }

    @Override
    protected Source getSource() {
        return Source.ZHIBO8;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    @Override
    public Site getSite() {
        site.setAcceptStatCode(Sets.newHashSet(200, 304));
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new Zhibo8NewsCommendSeedProcessor())
                .addUrl("http://news.zhibo8.cc/")
                .run();
    }
}
