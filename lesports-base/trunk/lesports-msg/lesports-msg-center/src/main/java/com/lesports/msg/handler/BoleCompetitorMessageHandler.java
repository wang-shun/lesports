package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.BoleCompetitorService;
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
public class BoleCompetitorMessageHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BoleCompetitorMessageHandler.class);

    @Resource
    private BoleCompetitorService boleCompetitorService;

    @Override
    public void handle(LeMessage message) {
        boolean result = boleCompetitorService.indexBoleCompetitor(message.getEntityId());
        LOG.info("process bole competitor : {} , result : {}.", message.getEntityId(), result);
    }
}
