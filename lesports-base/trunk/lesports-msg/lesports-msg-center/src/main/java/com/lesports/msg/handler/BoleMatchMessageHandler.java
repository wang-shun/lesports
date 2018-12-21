package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.BoleMatchService;
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
public class BoleMatchMessageHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BoleMatchMessageHandler.class);

    @Resource
    private BoleMatchService boleMatchService;

    @Override
    public void handle(LeMessage message) {
        boolean result = boleMatchService.indexBoleMatch(message.getEntityId());
        LOG.info("process bole match : {} , result : {}.", message.getEntityId(), result);
    }
}
