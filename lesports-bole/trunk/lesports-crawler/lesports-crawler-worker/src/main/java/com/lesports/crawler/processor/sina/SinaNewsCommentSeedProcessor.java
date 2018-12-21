package com.lesports.crawler.processor.sina;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.processor.zhibo8.Zhibo8NewsCommendSeedProcessor;
import com.lesports.crawler.repository.SourceNewsRepository;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("http://comment5.news.sina.com.cn/$")
@Component
public class SinaNewsCommentSeedProcessor extends AbstractPageProcessor<SourceNews[]> {

    // http://sports.sina.com.cn/f1/2016-01-08/doc-ifxnkkux0971929.shtml
    // http://comment5.news.sina.com.cn/page/info?format=js&channel=ty&newsid=comos-fxnkkux0971929&group=0&compress=1&ie=utf-8&oe=utf-8&page=1&page_size=20&jsvar=requestId_24304859
    private static final String URL_TEMPLATE = "http://comment5.news.sina.com.cn/page/info?format=js&channel=ty&newsid=comos-%s&group=0&compress=1&ie=utf-8&oe=utf-8&page=1&page_size=20";
    @Resource
    private SourceNewsRepository sourceNewsRepository;

    @Override
    protected SourceNews[] doProcess(Page page) {
        List<SourceNews> newsList = sourceNewsRepository.getInLastDay(getSource());
        for (SourceNews news : newsList) {
            Request request = new Request();
            request.setUrl(String.format(URL_TEMPLATE, news.getSourceId().replaceFirst("doc\\-i", ""), Math.random()));
            request.putExtra("SourceId", news.getSourceId());
            page.addTargetRequest(request);
        }

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
        site.setAcceptStatCode(Sets.newHashSet(200, 404));
        return site;
    }

    public static void main(String[] args) {
        System.out.println("http://sports.sina.com.cn/f1/2016-01-08/doc-ifxnkkux0971929.shtml".replaceFirst("doc\\-i", ""));
        Spider.create(new Zhibo8NewsCommendSeedProcessor())
                .addUrl("http://news.zhibo8.cc/")
                .run();
    }
}
