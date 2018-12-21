package com.lesports.websocket.subpub;

import com.alibaba.fastjson.JSONObject;
import com.lesports.msg.subpub.MessageListener;
import com.lesports.msg.subpub.SubPubMessage;
import me.ellios.jedis.RedisClient;
import me.ellios.jedis.RedisClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * lesports-base.
 * 将接收到的redis消息进行分发到不同业务
 *
 * @author pangchuanxiao
 * @since 2016/9/20
 */
public class RedisMessageDispatcher extends JedisPubSub {
    private static final Logger LOG = LoggerFactory.getLogger(RedisMessageDispatcher.class);

    private static final RedisClient redis = RedisClientFactory.getRedisClient("rpc_tlive_repo");
    private static final RedisMessageDispatcher dispatcher = new RedisMessageDispatcher();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private Map<String, MessageListener> container = new HashMap<>();

    private RedisMessageDispatcher() {
    }

    public static RedisMessageDispatcher getInstance() {
        return dispatcher;
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        LOG.info("on message {}", message);
        MessageListener redisListener = container.get(pattern);
        if (null == redisListener) {
            return;
        }
        String room = StringUtils.substringAfter(channel, "_");
        redisListener.onMessage(room, JSONObject.parseObject(message, SubPubMessage.class));
    }

    public void registerNamespace(String pattern, final MessageListener redisListener) {
        final String newPattern = pattern + "_*";
        container.put(newPattern, redisListener);

        EXECUTOR_SERVICE.execute(() -> {
                try {
                    redis.psubscribe(RedisMessageDispatcher.getInstance(), newPattern);
                } catch (Exception e) {
                    LOG.error("{}", e.getMessage(), e);
                }
        });
    }
}
