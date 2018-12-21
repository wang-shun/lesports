package com.lesports.websocket.common.type;

/**
 * Created by zhangdeqiang on 2016/9/12.
 */
public enum NamespaceType {
    TEXT_LIVE("tlive"),
    ;

    private final String value;

    NamespaceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NamespaceType findByValue(String value) {
        switch (value) {
            case "TEXT_LIVE":
                return TEXT_LIVE;
            default:
                return null;
        }
    }
}
