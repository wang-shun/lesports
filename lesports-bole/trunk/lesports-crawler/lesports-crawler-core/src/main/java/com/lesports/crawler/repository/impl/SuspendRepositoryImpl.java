package com.lesports.crawler.repository.impl;

import com.lesports.crawler.model.SuspendRequest;
import com.lesports.crawler.repository.SuspendRepository;
import com.lesports.crawler.utils.Constants;
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
public class SuspendRepositoryImpl extends AbstractMongoRepository<SuspendRequest, String> implements SuspendRepository {

    @Override
    protected Class<SuspendRequest> getEntityType() {
        return SuspendRequest.class;
    }

    @Override
    protected String getId(SuspendRequest entity) {
        return entity.getId();
    }

    @Override
    public List<SuspendRequest> getSuspendRequestsFrom(String startTime) {
        Query query = new Query(Criteria.where("updateAt").gt(startTime));
        query.addCriteria(Criteria.where("failureCount").lt(Constants.SUSPEND_TASK_RETRY_COUNT));
        return findByQuery(query);
    }
}
