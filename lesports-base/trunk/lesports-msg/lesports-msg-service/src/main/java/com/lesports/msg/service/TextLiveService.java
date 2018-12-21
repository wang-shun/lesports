package com.lesports.msg.service;

import java.util.Map;

/**
 * Created by lufei1 on 2015/10/15.
 */
public interface TextLiveService {

    /**
     * 删除tLive 分页消息缓存
     *
     * @param paramMap
     * @return
     */
    boolean deleteTextLiveApiCache(Map<String, String> paramMap);


    /**
     * 删除tLive 分页消息缓存
     *
     * @param textLiveId
     * @return
     */
    boolean deleteTextLiveApiCache(long textLiveId);
}
