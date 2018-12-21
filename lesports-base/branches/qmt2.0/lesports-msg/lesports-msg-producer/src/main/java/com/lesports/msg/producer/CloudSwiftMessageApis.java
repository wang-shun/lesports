package com.lesports.msg.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.util.MsgProducerConstants;
import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.url.URLSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.lesports.msg.util.MsgProducerConstants.*;

/**
 * Created by lufei1 on 2015/7/1.
 */
public class CloudSwiftMessageApis {
    private static final Logger LOG = LoggerFactory.getLogger(CloudSwiftMessageApis.class);
    private static final String dest = "lesports.message.center.direct/lesports.message.lecloud";
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static JmsTemplate leJmsTemplate;

    static {
        synchronized (CloudSwiftMessageApis.class) {
            try {
                AMQConnectionFactory factory = new AMQConnectionFactory(SWIFTQ_MESSAGE_LECOLUD_EXCHANGE);
                CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(factory);
                if (null != cachingConnectionFactory) {
                    leJmsTemplate = new JmsTemplate();
                    leJmsTemplate.setConnectionFactory(cachingConnectionFactory);
                }
            } catch (URLSyntaxException e) {
                LOG.warn("fail to create connection for {}. Ignore this if you don't send message to cloud msg center.", MsgProducerConstants.SWIFTQ_CONNECTION_URL);
            }
        }
    }

    public static boolean sendMsgSync(String dest, LeMessage message) {
        try {
            if (!MESSAGE_PRODUCER_ON) {
                LOG.info("not send message {} to {} as turn is {}.", JSONObject.toJSONString(message), dest, MESSAGE_PRODUCER_ON);
                return false;
            }
            if (null == leJmsTemplate) {
                LOG.info("not send message {} to {} as msg template is null.", JSONObject.toJSONString(message), dest);
                return false;
            }
            leJmsTemplate.convertAndSend(dest, JSON.toJSONString(message));
            LOG.info("send message {} to {}", JSONObject.toJSONString(message), dest);
        } catch (Exception e) {
            LOG.error("fail to send message {} to {}.", JSONObject.toJSONString(message), dest, e);
            return false;
        }
        return true;
    }

    public static Future<Boolean> sendMsgAsync(final LeMessage message) {
        return executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return sendMsgSync(dest, message);
            }
        });
    }
}
