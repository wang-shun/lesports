package com.lesports.crawler.repository;

import java.util.List;

import com.lesports.crawler.model.config.OutputConfig;
import com.lesports.crawler.model.source.Content;
import com.lesports.repository.LeCrudRepository;

/**
 * 输出配置
 * 
 * @author denghui
 *
 */
public interface OutputConfigRepository extends LeCrudRepository<OutputConfig, String> {

    /**
     * 根据站点和内容查找配置
     * 
     * @param site
     * @param content
     * @return
     */
    OutputConfig getConfig(String site, Content content);

    /**
     * 获取所有配置
     * 
     * @return
     */
    List<OutputConfig> getAllConfigs();
}
