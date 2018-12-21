package com.lesports.crawler.processor.sina;

import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * SinaNewsSeedProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://roll.sports.sina.com.cn/s/channel.php$")
@Component
public class SinaNewsSeedProcessor extends AbstractPageProcessor<SourceNews[]> {
    private static final String API_URL_TEMPLATE = "http://roll.news.sina.com.cn/interface/rollnews_ch_out_interface.php?col=64&spec=&type=&ch=02&k=&offset_page=0&offset_num=0&num=600&asc=&page=1&last_time=%d&r=%f";
    private long lastTime = 0; // in seconds

    @Override
    protected SourceNews[] doProcess(Page page) {
        page.addTargetRequest(String.format(API_URL_TEMPLATE, lastTime, Math.random()));
        return null;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public static void main(String[] args) {
        Spider.create(new SinaNewsSeedProcessor())
                .addUrl("http://roll.sports.sina.com.cn/s/channel.php")
                .run();
    }

    @Override
    protected Source getSource() {
        return Source.SINA;
    }
}
