package com.lesports.msg.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.TextLiveService;
import com.lesports.msg.subpub.SubPubMessage;
import com.lesports.msg.subpub.TLiveUpdate;
import com.lesports.sms.client.TextLiveInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * Created by lufei1 on 2015/10/19.
 */
@Component
public class TextLiveHandler extends AbstractHandler implements IHandler {

    private static final Logger logger = LoggerFactory.getLogger(TextLiveHandler.class);

    @Resource
    private TextLiveService textLiveService;

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        long textLiveId = message.getEntityId();
        boolean result = deleteTextLiveCache(textLiveId);
        if (result) {
            logger.info("success to handle textLive message {}", textLiveId);
        } else {
            logger.error("fail to handler textLive message {}", textLiveId);
        }
    }


    private boolean deleteTextLiveCache(long textLiveId) {
        return execute(textLiveId, "textLive", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return textLiveService.deleteTextLiveApiCache(aLong);
            }
        });
    }
}
