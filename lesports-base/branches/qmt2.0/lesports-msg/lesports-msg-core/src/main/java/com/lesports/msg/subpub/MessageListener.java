package com.lesports.msg.subpub;

/**
 * lesports-base.
 * 监听某个room的消息
 *
 * @author pangchuanxiao
 * @since 2016/9/21
 */
public interface MessageListener {
    /**
     * 消息监听方法
     *
     * @param room 对应roomid
     * @param message 消息体
     */
    void onMessage(String room, SubPubMessage message);
}
