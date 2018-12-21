package com.lesports.bole.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.lesports.bole.api.vo.TBLive;
import com.lesports.bole.api.vo.TBMatch;
import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.model.BoleStatus;
import com.lesports.model.PageResult;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleMatchService {
    boolean saveSms(long id);

    /**
     * 通过id查找伯乐赛程
     * @param id
     * @return
     */
    TBMatch getTBMatchById(long id);

    /**
     * 通过sms id查找伯乐赛程
     * @param id
     * @return
     */
    TBMatch getTBMatchBySmsId(long id);

    /**
     * 获取比赛列表
     * @param pageable
     * @return
     */
    PageResult<BoleMatch> getMatches(Pageable pageable);
    
    /**
     * 通过id查找比赛
     * @param id
     * @return
     */
    BoleMatch getById(long id);
    
    /**
     * 处理CREATED状态
     * @param id
     * @return
     */
    BoleMatch updateStatus(long id, BoleStatus status);

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

    List<TBLive> getTLivesBySmsMatchId(long smsId);
    
    /**
     * 查询可导出的比赛
     * @param startTime
     * @param com1
     * @param com2
     * @return
     */
    List<BoleMatch> getExportable(String startTime, long com1, long com2);

    /**
     * 导出到同一个SMS
     * @param ids
     * @return
     */
    List<BoleMatch> export(List<Long> ids);
}
