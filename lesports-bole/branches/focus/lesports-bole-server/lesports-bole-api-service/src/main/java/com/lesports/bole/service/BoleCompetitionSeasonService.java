package com.lesports.bole.service;

import org.springframework.data.domain.Pageable;

import com.lesports.bole.api.vo.TBCompetitionSeason;
import com.lesports.bole.model.BoleCompetitionSeason;
import com.lesports.model.PageResult;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleCompetitionSeasonService {
    boolean saveSms(long id);

    /**
     * 通过id查找伯乐赛季
     * @param id
     * @return
     */
    TBCompetitionSeason getTBCompetitionSeasonById(long id);

    /**
     * 通过sms id查找伯乐赛季
     * @param id
     * @return
     */
    TBCompetitionSeason getTBCompetitionSeasonBySmsId(long id);

    /**
     * 获取赛季列表
     * @param pageable
     * @return
     */
    PageResult<BoleCompetitionSeason> getCompetitionSeasons(Pageable pageable);
    
    /**
     * 通过id查找伯乐赛季
     * @param id
     * @return
     */
    BoleCompetitionSeason getById(long id);
}
