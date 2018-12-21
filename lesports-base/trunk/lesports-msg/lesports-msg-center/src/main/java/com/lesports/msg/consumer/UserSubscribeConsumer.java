package com.lesports.msg.consumer;


import com.alibaba.fastjson.JSON;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.msg.context.MessageProcessContext;
import com.lesports.msg.core.*;
import com.lesports.msg.model.SuperPhoneUserSubscribeMessage;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

/**
 * Created by lufei1 on 2015/6/1.
 */
public class UserSubscribeConsumer implements MessageListener {

    private final static Logger log = LoggerFactory.getLogger(UserSubscribeConsumer.class);

    @Resource
    private MessageProcessContext messageProcessContext;

    public void onMessage(Message message) {
        try {
            if (message instanceof BytesMessage) {
                BytesMessage bm = (BytesMessage) message;
                byte[] msg = new byte[(int) bm.getBodyLength()];
                bm.readBytes(msg);
                String messageInfo = new String(msg);
                SuperPhoneUserSubscribeMessage superPhoneUserSubscribeMessage = JSON.parseObject(messageInfo, SuperPhoneUserSubscribeMessage.class);
                if (null == superPhoneUserSubscribeMessage) {
                    return;
                }
                for (Map.Entry<String, SuperPhoneUserSubscribeMessage.Action> entry : superPhoneUserSubscribeMessage.entrySet()) {
                    processAction(entry);
                }
            }
        } catch (JMSException e) {
            log.error("[user] [message] [subscribeConsumer] [error]", e);
        }
    }

    /**
     * 判断该消息是不是需要乐视体育关注的类型
     * @param entry
     * @return
     */
    private boolean isValid(Map.Entry<String, SuperPhoneUserSubscribeMessage.Action> entry) {
        SuperPhoneUserSubscribeMessage.Action action = entry.getValue();
        if (null == action) {
            return false;
        }
        int type = LeNumberUtils.toInt(StringUtils.substringBefore(entry.getKey(), "_"));
        //1:乐视体育：比赛
        if (action.isEvents() && 1 != type) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户关注的id
     * @param entry
     * @return
     */
    private Long getEntityId(Map.Entry<String, SuperPhoneUserSubscribeMessage.Action> entry) {
        SuperPhoneUserSubscribeMessage.Action action = entry.getValue();
        Long entityId = 0l;
        //获取关注比赛的id
        if (action.isEvents()) {
            entityId = LeNumberUtils.toLong(StringUtils.substringAfterLast(entry.getKey(), "_"));
            if (LeIdApis.isOldId(entityId)) {
                entityId = LeIdApis.getNewId(entityId, IdType.EPISODE);
            }
        } else if (action.isLeword()) {
            //获取乐词id
            long lewordId = LeNumberUtils.toLong(entry.getKey());
            //乐词转换为体育ID
//            entityId = SbdsInternalApis.getEntityIdByLeciId(lewordId);
        }
        return entityId;
    }

    /**
     * 处理一条关注信息
     * @param entry
     */
    private void processAction(Map.Entry<String, SuperPhoneUserSubscribeMessage.Action> entry) {
        SuperPhoneUserSubscribeMessage.Action action = entry.getValue();
        if (!isValid(entry)) {
            return;
        }
        Long entityId = getEntityId(entry);
        if (null == entityId || 0 == entityId) {
            return;
        }
        //为每个增加uid创建一条消息
        if (CollectionUtils.isNotEmpty(action.getAdd())) {
            for (Long uid : action.getAdd()) {
                MessageContent messageContent = new MessageContent();
                messageContent.setUid(String.valueOf(uid));
                LeMessage leMessage = LeMessageBuilder.create()
                        .setSource(MessageSource.SUPER_PHONE)
                        .setIdType(IdType.USER_SUBSCRIBE)
                        .setContent(messageContent)
                        .setActionType(ActionType.ADD)
                        .setEntityId(entityId).build();
                messageProcessContext.process(leMessage);
            }
        }
        //为每个删除uid创建一条消息
        if (CollectionUtils.isNotEmpty(action.getAdd())) {
            for (Long uid : action.getAdd()) {
                MessageContent messageContent = new MessageContent();
                messageContent.setUid(String.valueOf(uid));
                LeMessage leMessage = LeMessageBuilder.create()
                        .setSource(MessageSource.SUPER_PHONE)
                        .setIdType(IdType.USER_SUBSCRIBE)
                        .setContent(messageContent)
                        .setActionType(ActionType.DELETE)
                        .setEntityId(entityId).build();
                messageProcessContext.process(leMessage);
            }
        }
    }

}
