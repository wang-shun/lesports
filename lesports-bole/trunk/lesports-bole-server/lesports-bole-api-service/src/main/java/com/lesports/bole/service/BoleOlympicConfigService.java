package com.lesports.bole.service;

import com.lesports.bole.api.vo.TOlympicLiveConfigSet;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.service.LeCrudService;

import java.util.List;

/**
 * User: ellios
 * Time: 15-6-5 : 上午1:04
 */
public interface BoleOlympicConfigService extends LeCrudService<BoleOlympicsLiveConfigSet, Long> {




    /**
     * 通过项目小项和配置名称获取配置列表
     * @param id
     * @param configName
     * @return
     */
    List<BoleOlympicsLiveConfigSet.OlympicsConfig> findConfigList(long id, String configName);

    /**
     * 通过项目小项获取配置集
     * @param gameSType
     * @return
     */
    TOlympicLiveConfigSet getConfig(long gameSType);

    /**
     *
     * @param gameSType
     * @param configName
     * @param config
     * @return
     */
    boolean saveConfig(long gameSType, String configName, BoleOlympicsLiveConfigSet.OlympicsConfig config);

    /**
     *
     * @param gameSType
     * @param configName
     * @param key
     * @return
     */
    boolean deleteConfig(long gameSType, String configName, String key);
}
