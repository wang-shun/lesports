package com.lesports.crawler.processor.zhibo8;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.utils.CrawlerUtils;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Json;

/**
 * Zhibo8NewsApiProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://news.zhibo8.cc/*/json/*$")
@Component
public class Zhibo8NewsApiProcessor extends AbstractZhibo8Processor<SourceNews[]> {
    private static final String URL_PREFIX = "http://news.zhibo8.cc";
    private long lastTime = 0; // in seconds

    @Override
    protected SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        Json json = new Json(CrawlerUtils.getJsonFromText(page.getRawText()));
        List<String> list = json.jsonPath("$.video_arr.[*]").all();
        if (requiredEmpty("$.video_arr.[*]", list, pageUrl)) {
            return null;
        }

        List<SourceNews> newsList = new ArrayList<>();
        long maxThisTime = 0;
        for (String item : list) {
            Json newsJson = new Json(item);
            String createTime = newsJson.jsonPath("$.createtime").get();
            if (requiredEmpty("$.createtime", createTime, item)) {
                continue;
            }
            long thisTime = TimeUnit.MILLISECONDS.toSeconds(LeDateUtils.parseYMDHMS(createTime).getTime());
            if (lastTime > thisTime) {
                // 较早的新闻不再处理
                continue;
            }

            String title = newsJson.jsonPath("$.title").get();
            if (requiredEmpty("$.title", title, item)) {
                continue;
            }

            String url = newsJson.jsonPath("$.url").get();
            if (requiredEmpty("$.url", url, item)) {
                continue;
            }

            SourceNews news = new SourceNews();
            news.setSource(getSource());
            news.setSourceId(CrawlerUtils.getIdFromUrl(url));
            news.setTitle(title);
            news.setSourceUrl(URL_PREFIX + url);
            // 英超,切尔西,原创
            String tags = newsJson.jsonPath("$.lable").get();
            if (!Strings.isNullOrEmpty(tags)) {
                news.addTags(tags.split(","));
            }

            newsList.add(news);
            if (thisTime > maxThisTime) {
                maxThisTime = thisTime;
            }
            page.addTargetRequest(news.getSourceUrl());
        }

        if (maxThisTime > lastTime) {
            lastTime = maxThisTime;
        }
        return newsList.toArray(new SourceNews[0]);
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    public static void main(String[] args) {
        Spider.create(new Zhibo8NewsApiProcessor())
                .addUrl("http://news.zhibo8.cc/zuqiu/json/2015-12-29.htm?key=0.5393481510691345")
                .addUrl("http://news.zhibo8.cc/nba/json/2015-12-29.htm?key=0.5393481510691345")
                .run();
    }
}
