package com.lesports.msg.handler;

import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.user.api.client.UserSubscribeApiClient;
import com.lesports.user.model.TSubType;
import com.lesports.utils.math.LeNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/20
 */
public class UserSubscribeMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSubscribeMessageHandler.class);

    @Override
    Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void handle(LeMessage message) {
        if (null == message || null == message.getEntityId() || null == message.getContent() || null == message.getActionType()) {
            return;
        }

        long entityId = LeNumberUtils.toLong(message.getEntityId());
        TSubType subType = getSubType(entityId);
        if (null == subType) {
            return;
        }
        ActionType actionType = message.getActionType();
        String uid = message.getContent().getUid();
        Boolean result = false;
        try {
            if (ActionType.ADD.equals(actionType)) {
                result = UserSubscribeApiClient.subscribe(uid, subType, String.valueOf(entityId));
            } else if (ActionType.DELETE.equals(actionType)) {
                result = UserSubscribeApiClient.unSubscribe(uid, subType, String.valueOf(entityId));
            }
        } catch (Exception e) {
            LOGGER.info("{}", e.getMessage(), e);
        }

        LOGGER.info("process user subscribe uid : {}, entity id : {}, subtype : {}, result : {}", uid, entityId, subType, result);
    }

    private TSubType getSubType(long entityId) {
        TSubType subType = null;
        switch (LeIdApis.checkIdTye(entityId)) {
            case EPISODE:
            case MATCH:
                subType = TSubType.MATCH;
                break;
            case PLAYER:
                subType = TSubType.PLAYER;
                break;
            case TEAM:
                subType = TSubType.TEAM;
                break;
        }
        return subType;
    }
}
