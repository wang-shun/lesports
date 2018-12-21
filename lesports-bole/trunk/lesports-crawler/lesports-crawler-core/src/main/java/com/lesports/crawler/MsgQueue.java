package com.lesports.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lesports.crawler.component.priority.Priority;
import com.lesports.crawler.model.RequestMessage;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.framing.AMQShortString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.*;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-11-13
 */
@Component("msgQueue")
public class MsgQueue {

    private static final Logger LOG = LoggerFactory.getLogger(MsgQueue.class);

    @Resource
    private ConnectionFactory crawlerSingleConnectionFactory;

    @Resource
    private Destination consumeDestination;

    @Resource
    private String produceExchange;

    @Resource
    private String routingKey;

    private MessageConsumer messageConsumer;

    private MessageProducer messageProducer;

    private Session session;

    @PostConstruct
    public void init() {
        try {
            Connection connection = crawlerSingleConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageConsumer = session.createConsumer(consumeDestination);
            messageProducer = session.createProducer(new AMQAnyDestination(new AMQShortString(produceExchange),
                    new AMQShortString("direct"), new AMQShortString(routingKey), false,
                    false, null, false, null));
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    public void push(RequestMessage requestMessage, Priority priority) {
        try {
            String content = JSONObject.toJSONString(requestMessage);
            TextMessage message = session.createTextMessage(content);
            int priorityValue = Priority.DEFAULT.getValue();
            if (null == priority) {
                priorityValue = priority.getValue();
            } else if (null != requestMessage.getPriority()) {
                priorityValue = requestMessage.getPriority();
            }
            message.setJMSPriority(priorityValue);
            messageProducer.send(message, DeliveryMode.PERSISTENT, priorityValue, Message.DEFAULT_TIME_TO_LIVE);
            LOG.info("send request {} to : {}, priority : {}", content, messageProducer.getDestination(), priorityValue);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    public void push(RequestMessage requestMessage) {
        push(requestMessage, Priority.DEFAULT);
    }

    public RequestMessage poll() {
        Message message = null;
        try {
            message = messageConsumer.receive();
            if (null == message) {
                return null;
            }
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String messageBody = textMessage.getText();
                return JSON.parseObject(messageBody, RequestMessage.class);
            }
        } catch (JMSException e) {
            LOG.error("{}", e.getMessage(), e);
        }

        return null;
    }
}
