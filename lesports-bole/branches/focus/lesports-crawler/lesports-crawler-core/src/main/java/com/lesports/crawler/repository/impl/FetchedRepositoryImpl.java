package com.lesports.crawler.repository.impl;

import com.lesports.crawler.model.FetchedRequest;
import com.lesports.crawler.repository.FetchedRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Repository
public class FetchedRepositoryImpl extends AbstractMongoRepository<FetchedRequest, String> implements FetchedRepository {

    @Override
    protected Class<FetchedRequest> getEntityType() {
        return FetchedRequest.class;
    }

    @Override
    protected String getId(FetchedRequest entity) {
        return entity.getId();
    }
}
