package com.lesports.msg.handler;

import com.lesports.id.api.IdType;
import com.lesports.msg.AbstractIntegrationTest;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageContent;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.sms.api.common.ScopeType;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: qiaohongxin
 * @since: 2016/8/10
 */
public class CompetitorStatsMessageHandlerTest extends AbstractIntegrationTest {

    @Resource
    private CompetitorSeasonStatHandler competitorSeasonStatHandler;
    @Resource
    private ScopeTopListHandler scopeTopListHandler;

    @Test
    public void testHandle() throws Exception {
        Long scopeId = 100009000L;
        MessageContent messageContent = new MessageContent();
        messageContent.addToMsgBody("scope", scopeId.toString()).addToMsgBody("scopeType", ScopeType.CONFERENCE.toString()).addToMsgBody("cid", "44001").addToMsgBody("csid", "100193002");
        LeMessage message = LeMessageBuilder.create().setEntityId(scopeId)
                .setIdType(IdType.TOP_LIST).setContent(messageContent).build();
        scopeTopListHandler.handle(message);

    }
    @Test
    public void testHandle2() throws Exception {
        MessageContent messageContent = new MessageContent();
        messageContent.addToMsgBody("cid", "44001").addToMsgBody("csid", "100193002");
        LeMessage message = LeMessageBuilder.create().setEntityId(116817003L)
                .setIdType(IdType.COMPETITOR_SEASON_STAT).setContent(messageContent).build();
        competitorSeasonStatHandler.handle(message);

    }
}