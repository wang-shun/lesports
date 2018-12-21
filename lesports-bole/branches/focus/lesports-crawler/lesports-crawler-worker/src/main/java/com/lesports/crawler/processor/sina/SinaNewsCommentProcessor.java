package com.lesports.crawler.processor.sina;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.utils.CrawlerUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Json;

/**
 * SinaNewsCommentProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://comment5.news.sina.com.cn/page/info*$")
@Component
public class SinaNewsCommentProcessor extends AbstractPageProcessor<SourceNews[]> {

    @Override
    protected SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        // var data={...}
        Json json = new Json(CrawlerUtils.getJsonFromText(page.getRawText()));
        String qreply = json.jsonPath("$.result.count.qreply").get();
        if (requiredEmpty("$.result.count.qreply", qreply, pageUrl)) {
            return null;
        }
        String total = json.jsonPath("$.result.count.total").get();
        if (requiredEmpty("$.result.count.total", total, pageUrl)) {
            return null;
        }

        SourceNews news = new SourceNews();
        news.setSource(getSource());
        news.setSourceId(page.getRequest().getExtra("SourceId").toString());
        news.setComments(Integer.valueOf(qreply));
        news.setParticipants(Integer.valueOf(total));
        return new SourceNews[] { news };
    }

    @Override
    protected Source getSource() {
        return Source.SINA;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    String getSourceId(String pageUrl) {
        // http://comment5.news.sina.com.cn/page/info?format=js&channel=ty&newsid=comos-fxnkkuv4351286&group=0&compress=1&ie=utf-8&oe=utf-8&page=1&page_size=20
        String exp = "newsid=comos-([0-9a-z]*)&";
        Pattern pattern = Pattern.compile(exp);
        Matcher matcher = pattern.matcher(pageUrl);
        if (matcher.find()) {
            return "i" + matcher.group(1);
        }

        throw new RuntimeException("can not parse source id from: " + pageUrl);
    }

    public static void main(String[] args) {
        Spider.create(new SinaNewsCommentProcessor())
                .addUrl("http://comment5.news.sina.com.cn/page/info?format=js&channel=ty&newsid=comos-fxnkkuv4351286&group=0&compress=1&ie=utf-8&oe=utf-8&page=1&page_size=20")
                .run();
    }
}
