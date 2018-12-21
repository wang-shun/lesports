package com.lesports.sms.data.mq;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: EventMessage
 * @Description: 消息
 * @author xlz
 * @date 2014-7-23 下午02:05:22
 */
public class EventMessage {

    public static String CODE_MATCH = "match";
    public static String CODE_TEAM = "team";
    public static String CODE_PLAYER = "player";
    public static String CODE_VIDEO = "video";
    public static String CODE_STAR = "star";

    public static String ACTION = "action";
    public static String ID = "id";

    public static String ACTION_ADD = "add";
    public static String ACTION_UPDATE = "update";
    private static Map<String, EventMessage> instanceMap;
    static {
        instanceMap = new HashMap<String, EventMessage>();
        instanceMap.put(CODE_MATCH, new EventMessage(CODE_MATCH));
        instanceMap.put(CODE_VIDEO, new EventMessage(CODE_VIDEO));
    }

    public static EventMessage getInstance(String code){
        if(instanceMap != null){
            return instanceMap.get(code);
        }
        return null;
    }
    private String code;//消息名称
    private Map<String, Object> messageInfo = new HashMap<String, Object>();//消息内容
    public EventMessage(String code, Map<String, Object> messageInfo) {
        super();
        this.code = code;
        this.messageInfo = messageInfo;
    }

    public EventMessage() {
        super();
    }

    public EventMessage(String code) {
        super();
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(Map<String, Object> messageInfo) {
        this.messageInfo = messageInfo;
    }

}
