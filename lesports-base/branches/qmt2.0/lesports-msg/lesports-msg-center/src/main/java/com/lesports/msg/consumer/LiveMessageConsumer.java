package com.lesports.msg.consumer;

import com.alibaba.fastjson.JSON;
import com.lesports.id.api.IdType;
import com.lesports.msg.context.MessageProcessContext;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.model.LiveMessage;
import com.lesports.msg.util.MsgConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/15
 */
public class LiveMessageConsumer implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(LiveMessageConsumer.class);

    @Resource
    private MessageProcessContext messageProcessContext;

    @Override
    public void onMessage(Message message) {
        try {
            String messageBody = null;
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageBody = textMessage.getText();
//                logger.info("receive message {} from epg.", messageBody);
                LiveMessage liveMessage = JSON.parseObject(messageBody, LiveMessage.class);
                LeMessage leMessage = purseMessage(liveMessage);
                if (null != leMessage) {
                    messageProcessContext.process(leMessage);
                }
            }
        } catch (Exception e) {
            logger.error("SmsSyncConsumer error", e);
        }
    }

    private LeMessage purseMessage(LiveMessage liveMessage) {
        Assert.notNull(liveMessage);
        if (liveMessage.getDateType() != MsgConstants.LIVE_MESSAGE_DATA_TYPE) {
            return null;
        }
        Assert.notNull(liveMessage.getParam());
        long entityId = liveMessage.getParam().getLiveId();
        ActionType actionType = ActionType.UPDATE;
        MessageSource source = MessageSource.LETV_LIVE;
        return LeMessageBuilder.create().
                setEntityId(entityId).
                setIdType(IdType.LETV_LIVE).
                setActionType(actionType).
                setSource(source).
                build();

    }
}
