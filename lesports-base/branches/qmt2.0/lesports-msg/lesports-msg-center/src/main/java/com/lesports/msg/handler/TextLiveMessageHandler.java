package com.lesports.msg.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.TextLiveService;
import com.lesports.msg.subpub.SubPubMessage;
import com.lesports.msg.subpub.TLiveUpdate;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.model.TextLiveMessage;
import com.lesports.utils.math.LeNumberUtils;
import me.ellios.jedis.RedisClient;
import me.ellios.jedis.RedisClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * Created by lufei1 on 2015/10/19.
 */
@Component
public class TextLiveMessageHandler extends AbstractHandler implements IHandler {

    private static final Logger logger = LoggerFactory.getLogger(TextLiveMessageHandler.class);
    private static final RedisClient REDIS_CLIENT = RedisClientFactory.getRedisClient("rpc_tlive_repo");

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        long textLiveMessageId = message.getEntityId();

        TextLiveMessage textLiveMessage = TextLiveInternalApis.getTextLiveMessageById(textLiveMessageId);
        if (null == textLiveMessage) {
            logger.warn("No text live message found : {}.", textLiveMessageId);
            return;
        }
        SubPubMessage subPubMessage = new SubPubMessage("LATEST_MESSAGE",
                JSONObject.toJSONString(new TLiveUpdate(LeNumberUtils.toLong(textLiveMessage.getTextLiveId()),
                        textLiveMessageId, LeNumberUtils.toLong(textLiveMessage.getSection()))));

        Long num = REDIS_CLIENT.publish("tlive_" + String.valueOf(textLiveMessage.getMid()),
                JSONObject.toJSONString(subPubMessage));
        logger.info("Publish text live message {} to clients. {} servers accept.",textLiveMessageId, num);
    }


}
