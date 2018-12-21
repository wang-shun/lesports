package com.lesports.websocket.common.type;

/**
 * Created by zhangdeqiang on 2016/9/12.
 */
public enum DataType {
    CHAT("CHAT"),
    LIKE("LIKE");

    private final String value;

    DataType(String value) {
        this.value = value;
    }

    public static DataType findByValue(String value) {
        switch (value) {
            case "CHAT":
                return CHAT;
            case "LIKE":
                return LIKE;
            default:
                return null;
        }
    }
}
