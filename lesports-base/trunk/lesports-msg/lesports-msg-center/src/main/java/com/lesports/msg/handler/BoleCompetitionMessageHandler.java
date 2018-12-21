package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.BoleCompetitionService;
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
public class BoleCompetitionMessageHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BoleCompetitionMessageHandler.class);

    @Resource
    private BoleCompetitionService boleCompetitionService;

    @Override
    public void handle(LeMessage message) {
        boolean result = boleCompetitionService.indexBoleCompetition(message.getEntityId());
        LOG.info("process bole competition : {} , result : {}.", message.getEntityId(), result);
    }
}
