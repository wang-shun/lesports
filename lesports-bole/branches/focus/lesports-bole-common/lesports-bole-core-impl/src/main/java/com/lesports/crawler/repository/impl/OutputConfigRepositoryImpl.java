package com.lesports.crawler.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lesports.crawler.model.config.OutputConfig;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.repository.OutputConfigRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;

/**
 * 爬虫配置
 * 
 * @author denghui
 *
 */
@Repository
public class OutputConfigRepositoryImpl extends AbstractMongoRepository<OutputConfig, String>
        implements OutputConfigRepository {

    @Override
    public OutputConfig getConfig(String site, Content content) {
        Query query = new Query(where("site").is(site));
        query.addCriteria(where("content").is(content.toString()));
        query.addCriteria(where("deleted").is(false));
        return findOneByQuery(query);
    }

    @Override
    public List<OutputConfig> getAllConfigs() {
        Query query = new Query(where("deleted").is(false));
        List<OutputConfig> configs = findByQuery(query);
        
        Collections.sort(configs, new Comparator<OutputConfig>() {

            @Override
            public int compare(OutputConfig o1, OutputConfig o2) {
                if (o1.getPriority() == null && o2.getPriority() != null) {
                    return 1;
                } else if (o1.getPriority() != null && o2.getPriority() == null) {
                    return -1;
                } else if (o1.getPriority() == null && o2.getPriority() == null) {
                    return o1.getSite().compareTo(o2.getSite());
                }
                
                return o1.getPriority().compareTo(o2.getPriority());
            }
            
        });
        return configs;
    }

    @Override
    protected Class<OutputConfig> getEntityType() {
        return OutputConfig.class;
    }

    @Override
    protected String getId(OutputConfig arg0) {
        return arg0.getId();
    }
}
