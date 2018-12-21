package com.lesports.msg.handler;

import com.lesports.msg.cache.HkCacheApis;
import com.lesports.msg.cache.SmsMemCache;
import com.lesports.msg.cache.UsCacheApis;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageConstants;
import com.lesports.msg.core.MessageContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:52
 */
@Component
public class SmsWebCbaseCacheHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SmsWebCbaseCacheHandler.class);
    @Resource
    private SmsMemCache smsMemCache;

    @Override
    public void handle(LeMessage message) {
        MessageContent messageContent = message.getContent();
        if (null == messageContent) {
            return;
        }
        String key = messageContent.getDesc();
        if (null == key) {
            key = messageContent.getFromMsgBody(MessageConstants.KEY);
        }
        smsMemCache.delete(key, -1L);
        HkCacheApis.deleteWeb(key, -1L);
        UsCacheApis.deleteWeb(key, -1L);
    }

    @Override
    Logger getLogger() {
        return LOG;
    }
}
