package com.lesports.websocket.domain;

import java.io.Serializable;

/**
 * 与页面交互的序列化业务数据
 * Created by zhangdeqiang on 2016/9/12.
 */
public class MessageData<T> implements Serializable {
    private static final long serialVersionUID = -3879483493790849409L;

    //图文直播业务的roomId为mid
    private String roomId;
    private T data;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
