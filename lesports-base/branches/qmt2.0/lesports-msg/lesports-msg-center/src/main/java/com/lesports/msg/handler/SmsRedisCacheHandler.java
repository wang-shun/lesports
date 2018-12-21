package com.lesports.msg.handler;

import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageConstants;
import com.lesports.msg.core.MessageContent;
import me.ellios.jedis.RedisClientFactory;
import me.ellios.jedis.RedisOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:52
 */
@Component
public class SmsRedisCacheHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SmsRedisCacheHandler.class);
    protected static RedisOp redisOpHk;
    protected static RedisOp redisOpUS;

    static {
        try {
            redisOpHk = RedisClientFactory.getRedisClient("rpc_sms_cache_hk");
            redisOpUS = RedisClientFactory.getRedisClient("rpc_sms_cache_us");
        } catch (Exception e) {
            LOG.error("", e.getMessage(), e);
        }
    }

    @Override
    public void handle(LeMessage message) {
        MessageContent messageContent = message.getContent();
        if (null == messageContent) {
            return;
        }
        String key = messageContent.getDesc();
        if (null == key) {
            key = messageContent.getFromMsgBody(MessageConstants.KEY);
        }
        if (null != redisOpHk) {
            boolean result = redisOpHk.del(key);
            LOG.info("delete sms hk redis cache for : {}, result : {}.", key, result);
        }
        if (null != redisOpUS) {
            boolean result = redisOpUS.del(key);
            LOG.info("delete sms us redis cache for : {}, result : {}.", key, result);
        }

    }

    @Override
    Logger getLogger() {
        return LOG;
    }
}
