package com.lesports.crawler.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lesports.crawler.model.config.CrawlerConfig;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.repository.CrawlerConfigRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;

/**
 * 爬虫配置
 * 
 * @author denghui
 *
 */
@Repository
public class CrawlerConfigRepositoryImpl extends AbstractMongoRepository<CrawlerConfig, String>
        implements CrawlerConfigRepository {

    @Override
    public CrawlerConfig getConfig(Source source, Content content) {
        Query query = new Query(where("source").is(source.toString()));
        query.addCriteria(where("content").is(content.toString()));
        return findOneByQuery(query);
    }

    @Override
    public List<CrawlerConfig> getAllConfigs() {
        return findAll();
    }

    @Override
    protected Class<CrawlerConfig> getEntityType() {
        return CrawlerConfig.class;
    }

    @Override
    protected String getId(CrawlerConfig arg0) {
        return arg0.getId();
    }
}
