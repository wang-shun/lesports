package com.lesports.crawler.repository;

import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.repository.LeCrudRepository;

import java.util.List;

/**
 * 源新闻CRUD
 * 
 * @author denghui
 *
 */
public interface SourceNewsRepository extends LeCrudRepository<SourceNews, Long> {

    /**
     * 获取某条新闻
     * 
     * @param source
     * @param sourceId
     * @return
     */
    SourceNews getBySourceAndSourceId(Source source, String sourceId);

    /**
     * 获取最近一天的新闻
     * 
     * @param source
     * @return
     */
    List<SourceNews> getInLastDay(Source source);
}
