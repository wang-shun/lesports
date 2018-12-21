package com.lesports.msg.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/31
 */
public class MessageContent {
    private MessageEvent messageEvent;
    //action的数值
    private Integer value;
    //用户id
    private String uid;
    //用户积分接口，成功回调地址
    private String successCallback;
    //用户积分接口，失败回调地址
    private String failureCallback;
    //用户积分接口，行为描述
    private String desc;
    //消息内容体
    private Map<String, String> msgBody;
    private Set<Field> fields;
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public MessageContent addField(Field field) {
        if (null == fields) {
            fields = new HashSet<>();
        }
        if (null != field) {
            fields.add(field);
        }
        return this;
    }

    public MessageContent addToMsgBody(String key, String value) {
        if (null == msgBody) {
            msgBody = new HashMap<>();
        }
        msgBody.put(key, value);
        return this;
    }

    public String getFromMsgBody(String key) {
        if (null == msgBody) {
            return null;
        }
        return msgBody.get(key);
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSuccessCallback() {
        return successCallback;
    }

    public void setSuccessCallback(String successCallback) {
        this.successCallback = successCallback;
    }

    public String getUid() {
        return uid;
    }

    public String getFailureCallback() {
        return failureCallback;
    }

    public void setFailureCallback(String failureCallback) {
        this.failureCallback = failureCallback;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public MessageContent() {
    }

    public MessageContent(MessageEvent messageEvent) {
        this.messageEvent = messageEvent;
    }

    public MessageEvent getMessageEvent() {
        return messageEvent;
    }

    public MessageContent(Map<String, String> msgBody) {
        this.msgBody = msgBody;
    }

    public void setMessageEvent(MessageEvent messageEvent) {
        this.messageEvent = messageEvent;
    }

    public Map<String, String> getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(Map<String, String> msgBody) {
        this.msgBody = msgBody;
    }
}
