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
 * Created by lufei1 on 2015/7/1.
 */
public class SmsSyncConsumer implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SmsSyncConsumer.class);

    @Resource
    private MessageProcessContext messageProcessContext;

    @Override
    public void onMessage(Message message) {
        try {
            String messageBody = null;
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageBody = textMessage.getText();
//                logger.info("receive message {}", messageBody);
                LeMessage leMessage = JSON.parseObject(messageBody, LeMessage.class);
                messageProcessContext.process(leMessage);
            }
        } catch (Exception e) {
            logger.error("SmsSyncConsumer error", e);
        }
    }
}
