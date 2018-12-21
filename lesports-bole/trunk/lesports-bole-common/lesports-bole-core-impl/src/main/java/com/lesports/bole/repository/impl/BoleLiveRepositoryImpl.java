package com.lesports.bole.repository.impl;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.lesports.bole.model.BoleLive;
import com.lesports.bole.repository.BoleLiveRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Repository
public class BoleLiveRepositoryImpl extends AbstractMongoRepository<BoleLive, Long> implements BoleLiveRepository {
    @Override
    protected Class<BoleLive> getEntityType() {
        return BoleLive.class;
    }

    @Override
    protected Long getId(BoleLive boleLive) {
        return boleLive.getId();
    }

    @Override
    public List<BoleLive> getByMatchIdAndSource(Long matchId, String source) {
        Query query = new Query(Criteria.where("match_id").is(matchId));
        if (!Strings.isNullOrEmpty(source)) {
            query.addCriteria(Criteria.where("source").is(source));
        }
        return findByQuery(query);
    }

    @Override
    public List<String> getSitesByCid(long cid) {
        Query query = new Query(Criteria.where("cid").is(cid));
        return distinct("site", query, String.class);
    }

    @Override
    public BoleLive getByMatchIdAndUrl(Long matchId, String url) {
        Query query = new Query(Criteria.where("match_id").is(matchId));
        query.addCriteria(Criteria.where("url").is(url));
        return findOneByQuery(query);
    }

}
