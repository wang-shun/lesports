package com.lesports.bole.repository;

import java.util.List;

import com.lesports.bole.model.BoleLive;
import com.lesports.mongo.repository.MongoCrudRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleLiveRepository extends MongoCrudRepository<BoleLive, Long> {

    /**
     * 查询某场比赛的直播信息，source为空查询所有来源
     * 
     * @param matchId
     * @param source
     * @return
     */
    List<BoleLive> getByMatchIdAndSource(Long matchId, String source);

    /**
     * 查询某场比赛下某个直播信息
     * @param matchId
     * @param url
     * @return
     */
    BoleLive getByMatchIdAndUrl(Long matchId, String url);
    
    /**
     * 查询某赛事的所有直播信息站点
     * 
     * @param id
     * @return
     */
    List<String> getSitesByCid(long cid);
}
