package com.lesports.bole.service;

import java.util.List;

import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleLiveStatus;

/**
 * BoleLiveService
 * 
 * @author denghui
 *
 */
public interface BoleLiveService {
    /**
     * 通过比赛id查找直播信息
     * 
     * @param id
     * @return
     */
    List<BoleLive> getByMatchId(long matchId);

    /**
     * 更新Live状态
     * @param id
     * @param status
     * @return
     */
    BoleLive updateStatus(long id, BoleLiveStatus status);
}
