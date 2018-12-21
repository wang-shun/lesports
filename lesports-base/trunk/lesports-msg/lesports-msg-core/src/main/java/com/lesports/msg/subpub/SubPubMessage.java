package com.lesports.msg.subpub;

/**
 * lesports-base.
 * 订阅发布的消息
 *
 * @author pangchuanxiao
 * @since 2016/9/21
 */
public class SubPubMessage {
    private String event;
    private String data;

    public SubPubMessage() {
    }

    public SubPubMessage(String event, String data) {
        this.event = event;
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
