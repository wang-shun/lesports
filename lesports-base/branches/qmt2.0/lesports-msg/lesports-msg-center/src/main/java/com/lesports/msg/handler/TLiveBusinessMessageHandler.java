package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageContent;
import com.lesports.msg.service.TextLiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: lufei
 * @since: 2015/10/15
 */
@Component
public class TLiveBusinessMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger logger = LoggerFactory.getLogger(TLiveBusinessMessageHandler.class);

    @Resource
    private TextLiveService textLiveService;

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        MessageContent messageContent = message.getContent();
        if (messageContent == null) {
            return;
        }
        boolean result = textLiveService.deleteTextLiveApiCache(messageContent.getMsgBody());
        if (result) {
            logger.info("success to handle tLive message {}", message);
        } else {
            logger.error("fail to handler tLive message {}", message);
        }
    }

}
