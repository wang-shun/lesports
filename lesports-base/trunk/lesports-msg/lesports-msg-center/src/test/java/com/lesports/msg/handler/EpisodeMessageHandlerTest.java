package com.lesports.msg.handler;

import com.lesports.msg.AbstractIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/24
 */
public class EpisodeMessageHandlerTest extends AbstractIntegrationTest {

    @Resource
    private EpisodeMessageHandler episodeMessageHandler;
    @Test
    public void testHandle() throws Exception {
//        LeMessage message = new LeMessage(100335005l, ActionType.UPDATE, MessageSource.SMS_SERVICE, new MessageContent(MessageEvent.STATUS_CHANGED));
//        episodeMessageHandler.handle(message);
    }
}