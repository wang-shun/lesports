package com.lesports.bole.repository;

import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.mongo.repository.MongoCrudRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleCompetitorRepository extends MongoCrudRepository<BoleCompetitor, Long> {
    /**
     * 通过sms id查找一个实体
     * 
     * @param smsId
     * @return
     */
    BoleCompetitor findOneBySmsId(long smsId);

    /**
     * 按类型根据条件匹配, 只匹配非CREATED状态的记录
     * 
     * @param type
     * @param nickname
     * @param gameFName
     * @return
     */
    BoleCompetitor matchOne(BoleCompetitorType type, String nickname, String gameFName);
}
