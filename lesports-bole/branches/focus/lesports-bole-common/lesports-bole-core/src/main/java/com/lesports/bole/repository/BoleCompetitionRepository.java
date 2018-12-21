package com.lesports.bole.repository;

import com.lesports.bole.model.BoleCompetition;
import com.lesports.mongo.repository.MongoCrudRepository;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleCompetitionRepository extends MongoCrudRepository<BoleCompetition, Long> {
    /**
     * 通过sms id查找一个实体
     * 
     * @param smsId
     * @return
     */
    BoleCompetition findOneBySmsId(long smsId);

    /**
     * 根据赛事缩写匹配赛事, 只匹配非CREATED状态的记录
     * 
     * @param abbreviation
     * @param pageable
     * @return
     */
    BoleCompetition matchByAbbreviation(String abbreviation);

    /**
     * 根据名称查询赛事
     * 
     * @param name
     * @return
     */
    BoleCompetition getByName(String name);

}
