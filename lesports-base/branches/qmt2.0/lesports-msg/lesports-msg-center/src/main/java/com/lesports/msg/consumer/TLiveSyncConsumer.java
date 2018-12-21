package com.lesports.msg.consumer;

import com.alibaba.fastjson.JSON;
import com.lesports.msg.context.MessageProcessContext;
import com.lesports.msg.core.LeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by lufei1 on 2015/10/15.
 */
public class TLiveSyncConsumer implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(TLiveSyncConsumer.class);

    @Resource
    private MessageProcessContext messageProcessContext;

    @Override
    public void onMessage(Message message) {
        try {
            String messageBody = null;
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageBody = textMessage.getText();
                LeMessage leMessage = JSON.parseObject(messageBody, LeMessage.class);
                messageProcessContext.process(leMessage);
            }
        } catch (Exception e) {
            logger.error("TLiveSyncConsumer error", e);
        }
    }
}
