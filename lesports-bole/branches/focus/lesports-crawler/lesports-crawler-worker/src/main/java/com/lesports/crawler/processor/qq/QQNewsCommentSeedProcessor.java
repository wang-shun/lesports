package com.lesports.crawler.processor.qq;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.repository.SourceNewsRepository;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("http://coral.qq.com/article/$")
@Component
public class QQNewsCommentSeedProcessor extends AbstractPageProcessor<SourceNews[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQNewsCommentSeedProcessor.class);
    // 'http://coral.qq.com/article/' + cmt_id + '/commentnum?callback=_cbSum&source=1&t=' + Math.random()
    private static final String URL_TEMPLATE = "http://coral.qq.com/article/%s/commentnum?callback=_cbSum&source=1&t=%f";
    @Resource
    private SourceNewsRepository sourceNewsRepository;
    
    @Override
    protected SourceNews[] doProcess(Page page) {
        List<SourceNews> newsList = sourceNewsRepository.getInLastDay(getSource());
        for (SourceNews news : newsList) {
            Object cmtId = news.getExtra("cmt_id");
            if (cmtId == null) {
                LOGGER.warn("cmt_id null for: " + news.getSourceUrl());
                continue;
            }
            Request request = new Request();
            request.setUrl(String.format(URL_TEMPLATE, cmtId, Math.random()));
            request.putExtra("SourceId", news.getSourceId());
            page.addTargetRequest(request);
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

}
