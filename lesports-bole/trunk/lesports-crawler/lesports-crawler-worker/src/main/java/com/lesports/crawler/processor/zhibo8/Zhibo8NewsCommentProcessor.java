package com.lesports.crawler.processor.zhibo8;

import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.utils.CrawlerUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Json;

/**
 * Zhibo8NewsCommentProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://cache.zhibo8.cc/json/*_count.htm*$")
@Component
public class Zhibo8NewsCommentProcessor extends AbstractZhibo8Processor<SourceNews[]> {

    @Override
    protected SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        // http://cache.zhibo8.cc/json/2016_01_11/news/zuqiu/5693106c0dcd4_count.htm?key=0.30229930859059095
        // {"root_num":4,"root_normal_num":4,"all_num":5}
        Json json = new Json(CrawlerUtils.getJsonFromText(page.getRawText()));
        String num = json.jsonPath("$.all_num").get();
        if (requiredEmpty("$.all_num", num, pageUrl)) {
            return null;
        }
        SourceNews news = new SourceNews();
        news.setSource(getSource());
        news.setSourceId(page.getRequest().getExtra("SourceId").toString());
        news.setComments(Integer.valueOf(num));
        return new SourceNews[] { news };
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    String getSourceId(String pageUrl) {
        String name = CrawlerUtils.getIdFromUrl(pageUrl);
        return name.split("_")[0];
    }

    public static void main(String[] args) {
        Spider.create(new Zhibo8NewsCommentProcessor())
                .addUrl("http://cache.zhibo8.cc/json/2016_01_11/news/zuqiu/5693106c0dcd4_count.htm?key=0.30229930859059095")
                .run();
    }
}
