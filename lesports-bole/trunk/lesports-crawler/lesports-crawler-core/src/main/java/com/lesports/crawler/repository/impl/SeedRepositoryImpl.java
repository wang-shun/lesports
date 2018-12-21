package com.lesports.crawler.repository.impl;

import com.lesports.crawler.model.SeedRequest;
import com.lesports.crawler.repository.SeedRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Repository
public class SeedRepositoryImpl extends AbstractMongoRepository<SeedRequest, String> implements SeedRepository {
    @Override
    protected Class<SeedRequest> getEntityType() {
        return SeedRequest.class;
    }

    @Override
    protected String getId(SeedRequest entity) {
        return entity.getId();
    }

    @Override
    public List<SeedRequest> getAll() {
        Query query = new Query(Criteria.where("effect").is(true));
        return findByQuery(query);
    }
}
