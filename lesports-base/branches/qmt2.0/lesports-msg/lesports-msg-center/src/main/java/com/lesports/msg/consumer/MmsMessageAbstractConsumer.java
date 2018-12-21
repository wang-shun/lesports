package com.lesports.msg.consumer;

import com.alibaba.fastjson.JSON;
import com.lesports.id.api.IdType;
import com.lesports.msg.context.MessageProcessContext;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.model.LetvMMSMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.jms.TextMessage;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/16
 */
public abstract class MmsMessageAbstractConsumer implements javax.jms.MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MmsMessageAbstractConsumer.class);

    @Resource
    private MessageProcessContext messageProcessContext;

    private LeMessage purseMessage(LetvMMSMessage.MmsMessage mmsMessage) {
        Assert.notNull(mmsMessage);
        Assert.notNull(mmsMessage.getProperties());

        if (mmsMessage.getMessageModuleCode() == LetvMMSMessage.MessageModuleCode.MMS) {
            long entityId = mmsMessage.getProperties().getId();
            ActionType actionType = ActionType.UPDATE;
            MessageSource source = messageSource();
            IdType idType = null;
            if (mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.ALBUM_UPDATE ||
                    mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.ALBUM_ADD ||
                    mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.ALBUM_DELETE
                    ) {
                idType = IdType.MMS_ALBUM;
            } else if (mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.VIDEO_UPDATE ||
                    mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.VIDEO_ADD ||
                    mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.VIDEO_DELETE) {
                idType = IdType.MMS_VIDEO;
            } else {
                return null;
            }
            if (mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.ALBUM_DELETE ||
                    mmsMessage.getMessageActionCode() == LetvMMSMessage.MessageActionCode.VIDEO_DELETE) {
                actionType = ActionType.DELETE;
            }
            return LeMessageBuilder.create().
                    setEntityId(entityId).
                    setIdType(idType).
                    setActionType(actionType).
                    setSource(source).
                    build();
        }
        return null;
    }

    abstract MessageSource messageSource();

    @Override
    public void onMessage(javax.jms.Message message) {
        try {
            String messageBody = null;
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageBody = textMessage.getText();
                logger.info("receive mms message : {}, from : {}", messageBody, messageSource());
                LetvMMSMessage mmsMessage = JSON.parseObject(messageBody, LetvMMSMessage.class);
                if (null != mmsMessage && null != mmsMessage.getLetvMMSMessage()) {
                    LeMessage leMessage = purseMessage(mmsMessage.getLetvMMSMessage());
                    if (null != leMessage) {
                        messageProcessContext.process(leMessage);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("SmsSyncConsumer error", e);
        }
    }
}
