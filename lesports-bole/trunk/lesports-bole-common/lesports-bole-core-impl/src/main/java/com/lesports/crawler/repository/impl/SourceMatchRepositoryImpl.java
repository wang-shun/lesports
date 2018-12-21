package com.lesports.crawler.repository.impl;

import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.repository.SourceMatchRepository;
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
public class SourceMatchRepositoryImpl extends AbstractMongoRepository<SourceMatch, String> implements SourceMatchRepository {

    @Override
    protected Class<SourceMatch> getEntityType() {
        return SourceMatch.class;
    }

    @Override
    protected String getId(SourceMatch entity) {
        return entity.getId();
    }

    @Override
    public SourceMatch getByStartTimeAndCompetitors(String startTime, List<String> competitors) {
        Query query = new Query(Criteria.where("start_time").is(startTime));
        query.addCriteria(Criteria.where("competitors").in(competitors));
        return findOneByQuery(query);
    }

    @Override
    public SourceMatch getBySourceAndSourceId(Source source, String sourceId) {
        Query query = new Query(Criteria.where("source").is(source.toString()));
        query.addCriteria(Criteria.where("source_id").in(sourceId));
        return findOneByQuery(query);
    }
}
