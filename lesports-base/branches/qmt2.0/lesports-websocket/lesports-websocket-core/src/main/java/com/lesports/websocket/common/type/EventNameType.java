package com.lesports.websocket.common.type;

/**
 * Created by zhangdeqiang on 2016/9/12.
 */
public enum EventNameType {
    MESSAGE("MESSAGE"), //消息
    JOIN("JOIN"), //join namespace（room）
    LEAVE("LEAVE"),
    ;

    private final String value;

    EventNameType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventNameType findByValue(String value) {
        switch (value) {
            case "MESSAGE":
                return MESSAGE;
            case "JOIN":
                return JOIN;
            case "LEAVE":
                return LEAVE;
            default:
                return null;
        }
    }
}
