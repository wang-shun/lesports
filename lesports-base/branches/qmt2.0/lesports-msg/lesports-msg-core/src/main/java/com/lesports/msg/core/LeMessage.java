package com.lesports.msg.core;

import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;

/**
 * User: ellios
 * Time: 15-6-28 : 下午9:57
 */
public class LeMessage {

    private String messageId;
    private Long entityId;
    private IdType idType;
    private ActionType actionType;
    private MessageSource source;
    private MessageContent content;
    private String entityName;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public MessageSource getSource() {
        return source;
    }

    public void setSource(MessageSource source) {
        this.source = source;
    }


    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * do not remove. JSON.parseObject will use default constructor to parse object.
     */
    public LeMessage() {
    }

    public LeMessage(ActionType actionType, MessageSource source, MessageContent content) {
        this.actionType = actionType;
        this.source = source;
        this.content = content;
    }

    public LeMessage(Long entityId, ActionType actionType, MessageSource source, MessageContent content) {
        this.entityId = entityId;
        this.idType = LeIdApis.checkIdTye(entityId);
        this.actionType = actionType;
        this.source = source;
        this.content = content;
    }

    public LeMessage(Long entityId, ActionType actionType, MessageSource source, MessageContent content, IdType idType) {
        this.entityId = entityId;
        this.idType = idType;
        this.actionType = actionType;
        this.source = source;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "entityId=" + entityId +
                ", idType=" + idType +
                ", actionType=" + actionType +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

}
