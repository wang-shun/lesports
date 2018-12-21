package com.lesports.bole.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleStatus;
import com.lesports.model.PageResult;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleCompetitionService {
    boolean saveSms(long id);

    /**
     * 通过id查找伯乐赛事
     * @param id
     * @return
     */
    TBCompetition getTBCompetitionById(long id);

    /**
     * 通过sms id查找伯乐赛事
     * @param id
     * @return
     */
    TBCompetition getTBCompetitionBySmsId(long id);
    /**
     * 通过sms id查找伯乐赛事
     * @param id
     * @return
     */
    BoleCompetition getBCompetitionBySmsId(long id);

    /**
     * 获取赛事列表
     * @param pageable
     * @return
     */
    PageResult<BoleCompetition> getCompetitions(Pageable pageable);

    /**
     * 通过id查找伯乐赛事
     * @param id
     * @return
     */
    BoleCompetition getById(long id);

    /**
     * 处理CREATED状态
     * @param id
     * @return
     */
    BoleCompetition updateStatus(long id, BoleStatus status);

    /**
     * 关联SMS
     * @param id
     * @param attachTo
     * @return
     */
    boolean attachSms(long id, long attachTo);
    
    /**
     * 取消关联
     * @param id
     * @return
     */
    boolean cancelAttachSms(long id);
    
    /**
     * 查询可导出的赛事
     * @param name
     * @return
     */
    List<BoleCompetition> getExportable(String name);

    /**
     * 导出到同一个SMS
     * @param ids
     * @return
     */
    List<BoleCompetition> export(List<Long> ids);
}
