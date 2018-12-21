package com.lesports.bole.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.bole.model.BoleStatus;
import com.lesports.model.PageResult;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleCompetitorService {
    /**
     * 保存来自sms的伯乐数据
     * @param id
     * @return
     */
    boolean saveSms(long id);

    /**
     * 通过id查找伯乐对阵
     * @param id
     * @return
     */
    TBCompetitor getTBCompetitorById(long id);

    /**
     * 通过sms id查找伯乐对阵
     * @param id
     * @return
     */
    TBCompetitor getTBCompetitorBySmsId(long id);

    /**
     * 获取对阵列表
     * @param pageable
     * @return
     */
    PageResult<BoleCompetitor> getCompetitors(BoleCompetitorType type, Pageable pageable);
    
    /**
     * 通过id查找对阵
     * @param id
     * @return
     */
    BoleCompetitor getById(long id);
    
    /**
     * 处理CREATED状态
     * @param id
     * @return
     */
    BoleCompetitor updateStatus(long id, BoleStatus status, BoleCompetitorType boleCompetitorType);

    /**
     * 修改对应的SMS实体
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
     * 查询可导出的对阵
     * @param name
     * @return
     */
    List<BoleCompetitor> getExportable(String name);

    /**
     * 导出到同一个SMS
     * @param ids
     * @param type 
     * @return
     */
    List<BoleCompetitor> export(List<Long> ids, String type);
}
