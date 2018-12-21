package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.BoleNewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
@Component
public class BoleNewsMessageHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BoleNewsMessageHandler.class);

    @Resource
    private BoleNewsService boleNewsService;

    @Override
    public void handle(LeMessage message) {
        boolean result = boleNewsService.indexBoleNews(message.getEntityId());
        LOG.info("process bole news : {} , result : {}.", message.getEntityId(), result);
    }
}
