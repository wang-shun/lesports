package com.lesports.bole.repository;

import com.lesports.bole.model.BoleMatch;
import com.lesports.mongo.repository.MongoCrudRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleMatchRepository extends MongoCrudRepository<BoleMatch, Long> {
    /**
     * 通过sms id查找一个实体
     * @param smsId
     * @return
     */
    BoleMatch findOneBySmsId(long smsId);
  
  /**
   * 根据比赛日期和比赛双方匹配比赛
   * @param startDate
   * @param com1
   * @param com2
   * @return
   */
  BoleMatch matchByStartTimeAndCompetitors(String startDate, long com1, long com2); 

}
