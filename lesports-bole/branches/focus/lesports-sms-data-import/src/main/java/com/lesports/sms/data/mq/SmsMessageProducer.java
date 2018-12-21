package com.lesports.sms.data.mq;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Author: chengen
 * Date: 2014/7/24
 * Time: 18:28
 */
public class SmsMessageProducer {
    private static final Logger logger = LoggerFactory.getLogger("mqlog");

    private RabbitTemplate smsMQTemplate;

    public void sendToSvr(EventMessage message) throws Exception{
        smsMQTemplate.convertAndSend("sis.svr", JSON.toJSONString(message));
        logger.info("notify sms-svr success! eventMessage {}", JSON.toJSON(message));
    }

    public RabbitTemplate getSmsMQTemplate() {
        return smsMQTemplate;
    }

    public void setSmsMQTemplate(RabbitTemplate smsMQTemplate) {
        this.smsMQTemplate = smsMQTemplate;
    }
}
