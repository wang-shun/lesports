package com.lesports.crawler.processor.qq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.utils.CrawlerUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Json;

/**
 * QQNewsCommentProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://coral.qq.com/article/*/commentnum*$")
@Component
public class QQNewsCommentProcessor extends AbstractPageProcessor<SourceNews[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQNewsCommentProcessor.class);
    @Override
    protected SourceNews[] doProcess(Page page) {
        // _cbSum({"errCode":0,"data":{"targetid":1290923359,"commentnum":"2"},"info":{"time":1452498734}})
        String pageUrl = page.getRequest().getUrl();
        Json json = new Json(CrawlerUtils.getJsonFromText(page.getRawText()));
        String code = json.jsonPath("$.errCode").get();
        if (requiredEmpty("$.errCode", code, pageUrl)) {
            return null;
        }
        if (!"0".equals(code)) {
            LOGGER.warn("page error, code: {}, url: {}", code, pageUrl);
            return null;
        }
        
        String num = json.jsonPath("$.data.commentnum").get();
        if (requiredEmpty("$.data.commentnum", num, pageUrl)) {
            return null;
        }

        SourceNews news = new SourceNews();
        news.setSource(getSource());
        news.setSourceId(page.getRequest().getExtra("SourceId").toString());
        news.setComments(Integer.valueOf(num));
        return new SourceNews[] { news };
    }

    @Override
    protected Source getSource() {
        return Source.QQ;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

}
