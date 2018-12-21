package com.lesports.crawler.processor.zhibo8;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * Zhibo8NewsSeedProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://news.zhibo8.cc/$")
@Component
public class Zhibo8NewsSeedProcessor extends AbstractZhibo8Processor<SourceNews[]> {

    private static final String NBA_URL_TEMPLATE = "http://news.zhibo8.cc/nba/json/%s.htm?key=%f";
    private static final String ZUQIU_URL_TEMPLATE = "http://news.zhibo8.cc/zuqiu/json/%s.htm?key=%f";

    @Override
    protected SourceNews[] doProcess(Page page) {
        String date = LeDateUtils.formatYYYY_MM_DD(new Date());
        page.addTargetRequest(String.format(NBA_URL_TEMPLATE, date, Math.random()));
        page.addTargetRequest(String.format(ZUQIU_URL_TEMPLATE, date, Math.random()));
        return null;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    public static void main(String[] args) {
        Spider.create(new Zhibo8NewsSeedProcessor())
                .addUrl("http://news.zhibo8.cc/")
                .run();
    }
}
