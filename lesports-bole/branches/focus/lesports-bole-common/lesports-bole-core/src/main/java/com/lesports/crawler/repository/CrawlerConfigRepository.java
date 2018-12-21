package com.lesports.crawler.repository;

import java.util.List;

import com.lesports.crawler.model.config.CrawlerConfig;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.Content;
import com.lesports.repository.LeCrudRepository;

/**
 * 爬虫配置
 * 
 * @author denghui
 *
 */
public interface CrawlerConfigRepository extends LeCrudRepository<CrawlerConfig, String> {

    /**
     * 根据源和内容查找配置
     * 
     * @param source
     * @param content
     * @return
     */
    CrawlerConfig getConfig(Source source, Content content);

    /**
     * 获取所有配置
     * 
     * @return
     */
    List<CrawlerConfig> getAllConfigs();
}
