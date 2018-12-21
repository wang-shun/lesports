package com.lesports.crawler.processor.qq;

import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * QQ新闻列表处理器
 * 
 * @author denghui
 *
 */
@TargetUrl("http://roll.sports.qq.com/$")
@Component
public class QQNewsSeedProcessor extends AbstractPageProcessor<SourceNews[]> {
    public static final String URL_TEMPLATE = "http://roll.sports.qq.com/interface/roll.php?%f&cata=&site=sports&date=&page=%d&mode=1&of=json";
    private static boolean firstTime = true;

    @Override
    protected SourceNews[] doProcess(Page page) {
        if (firstTime) {
            // 10页
            for (int i = 10; i >= 1; i--) {
                String nextPageUrl = String.format(URL_TEMPLATE, Math.random(), i);
                Request nextRequest = new Request(nextPageUrl);
                nextRequest.putExtra("firstTime", true);
                page.addTargetRequest(nextRequest);
            }
            firstTime = false;
        } else {
            // 第1页
            String nextPageUrl = String.format(URL_TEMPLATE, Math.random(), 1);
            page.addTargetRequest(nextPageUrl);
        }
        return null;
    }

    @Override
    protected Source getSource() {
        return Source.QQ;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    public static void main(String[] args) {
        Spider.create(new QQNewsSeedProcessor()).addUrl("http://roll.sports.qq.com").run();
    }
}
