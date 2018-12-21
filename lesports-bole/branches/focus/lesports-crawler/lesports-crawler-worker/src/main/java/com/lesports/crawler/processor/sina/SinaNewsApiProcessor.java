package com.lesports.crawler.processor.sina;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.utils.CrawlerUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Json;

/**
 * SinaNewsApiProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php*$")
@Component
public class SinaNewsApiProcessor extends AbstractPageProcessor<SourceNews[]> {

    @Resource
    private SinaNewsSeedProcessor seedProcessor;

    @Override
    protected SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        // var jsonData = {...};
        Json json = new Json(CrawlerUtils.getJsonFromText(page.getRawText()));
        List<String> list = json.jsonPath("$.list.[*]").all();
        if (list.isEmpty()) {
            return null;
        }

        String lastTime = json.jsonPath("$.last_time").get();
        if (requiredEmpty("$.last_time", lastTime, pageUrl)) {
            return null;
        }

        for (String item : list) {
            Json newsJson = new Json(item);
            String url = newsJson.jsonPath("$.url").get();
            if (requiredEmpty("$.url", url, pageUrl)) {
                continue;
            }

            page.addTargetRequest(url);
        }

        // set last time in seed processor
        seedProcessor.setLastTime(Long.valueOf(lastTime));
        return null;
    }

    @Override
    protected Source getSource() {
        return Source.SINA;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    @Override
    public Site getSite() {
        site.addHeader("Referer", "http://roll.sports.sina.com.cn/s/channel.php?ch=02");
        site.setCharset("GBK");
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new SinaNewsApiProcessor())
                .addUrl("http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=64&spec=&type=&date=&ch=02&k=&offset_page=0&offset_num=0&num=60&asc=&page=1&last_time=1452347696&r=0.12964058574289083")
                .run();
    }
}
