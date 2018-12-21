package com.lesports.msg.handler;

import client.SopsApis;
import com.google.common.base.Function;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.msg.service.NewsService;
import com.lesports.msg.util.MsgProducerConstants;
import com.lesports.sms.client.SbdsApis;
import com.lesports.utils.CallerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
@Component
public class NewsMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger logger = LoggerFactory.getLogger(NewsMessageHandler.class);

    @Resource
    private NewsService newsService;

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        long newsId = message.getEntityId();
        boolean result = deleteNewsCache(newsId);
        if (result) {
            logger.info("success to handle news message {}", newsId);
        } else {
            logger.error("fail to handler news message {}", newsId);
        }
        //索引新闻数据
        newsService.indexNews(newsId);
        //同步新闻到国广推荐新闻
        newsService.addNewsToCibnRecommendTv(newsId);

        SwiftMessageApis.sendMsgAsync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
        //加载redis数据
        SopsApis.getTNewsById(newsId, CallerUtils.getDefaultCaller());
    }


    private boolean deleteNewsCache(long newsId) {
        return execute(newsId, "news", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return newsService.deleteNewsApiCache(aLong);
            }
        });
    }
}
