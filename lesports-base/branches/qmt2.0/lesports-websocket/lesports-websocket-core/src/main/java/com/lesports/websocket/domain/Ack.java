package com.lesports.websocket.domain;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/9/19
 */
public class Ack {
    private String result;

    public Ack(boolean success) {
        if (success) {
            this.result = "success";
        } else {
            this.result = "fail";
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
