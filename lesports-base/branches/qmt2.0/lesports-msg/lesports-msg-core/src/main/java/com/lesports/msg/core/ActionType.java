package com.lesports.msg.core;

/**
 * Created by lufei1 on 2015/7/1.
 */
public enum ActionType {
    ADD(1),
    UPDATE(2),
    DELETE(3),
    LIVE_PLATFORM_UPDATE(4),
    SYNC_TO_CLOUD_UPDATE(5),
	DELETE_ALL(6),
    DELETE_CACHE(7);
    private int value;

    ActionType(int value) {
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
