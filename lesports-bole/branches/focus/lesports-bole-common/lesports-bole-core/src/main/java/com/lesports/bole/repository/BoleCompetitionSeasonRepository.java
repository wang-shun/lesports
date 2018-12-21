package com.lesports.bole.repository;

import com.lesports.bole.model.BoleCompetitionSeason;
import com.lesports.mongo.repository.MongoCrudRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleCompetitionSeasonRepository extends MongoCrudRepository<BoleCompetitionSeason, Long> {
    /**
     * 通过sms id查找一个实体
     * @param smsId
     * @return
     */
    BoleCompetitionSeason findOneBySmsId(long smsId);


  /**
   * 获取某赛事的最新赛季
   * @param cid
   * @return
   */
  BoleCompetitionSeason getLatestByCid(Long cid);
}
