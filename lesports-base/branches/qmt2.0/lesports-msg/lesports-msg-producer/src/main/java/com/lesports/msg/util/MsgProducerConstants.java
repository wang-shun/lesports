package com.lesports.msg.util;

import com.lesports.utils.LeProperties;

/**
 * Created by lufei1 on 2015/7/1.
 */
public class MsgProducerConstants {
    static {
        LeProperties.loadProperties("swiftq.properties");
    }
    //${swiftq.connection.url}
    public static String SWIFTQ_CONNECTION_URL = LeProperties.getString("swiftq.connection.url", "");

    public static String SWIFTQ_EXCHANGE_NAME = LeProperties.getString("swiftq.exchange.name", "lesports.sms.direct");

    public static String SWIFTQ_ROUTING_KEY_NAME = LeProperties.getString("swiftq.routingkey.name", "lesports.sms.sync.data");

    public static Boolean MESSAGE_PRODUCER_ON = LeProperties.getBoolean("swiftq.producer.on", true);

    public static String SWIFTQ_MESSAGE_CENTER_EXCHANGE = LeProperties.getString("swiftq.message.center.exchange", "lesports.message.center");

    public static String SWIFTQ_MESSAGE_LECOLUD_EXCHANGE = LeProperties.getString("cloud.swiftq.connection.url", "");
}
