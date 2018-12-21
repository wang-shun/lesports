package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.CloudSwiftMessageApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * lesports-projects.
 *
 * @author pangchuanxiao
 * @since 2015/8/14
 */
@Component
public class TeamSeasonMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TeamSeasonMessageHandler.class);

    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    public void handle(LeMessage message) {
		CloudSwiftMessageApis.sendMsgAsync(message);
		LOG.info("teamSeason to cloud message body : {}", message);
	}
}
