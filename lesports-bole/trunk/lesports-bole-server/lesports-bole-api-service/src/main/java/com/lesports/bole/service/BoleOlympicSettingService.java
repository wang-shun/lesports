package com.lesports.bole.service;

import com.lesports.bole.api.vo.TOlympicLiveConfigSet;
import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.service.LeCrudService;

import java.util.List;

/**
 * User: ellios
 * Time: 15-6-5 : 上午1:04
 */
public interface BoleOlympicSettingService extends LeCrudService<BoleOlympicSettingDataSet, String> {
    /**
     * 通过项目小项和配置名称获取配置列表
     * @param id
     * @param seettingDataType
     * @return
     */
    List<String> findConfigList(Long id, String seettingDataType);

}
