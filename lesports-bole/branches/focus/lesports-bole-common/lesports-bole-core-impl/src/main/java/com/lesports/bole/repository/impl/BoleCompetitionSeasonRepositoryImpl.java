package com.lesports.bole.repository.impl;

import com.lesports.bole.model.BoleCompetitionSeason;
import com.lesports.bole.repository.BoleCompetitionSeasonRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Repository
public class BoleCompetitionSeasonRepositoryImpl extends AbstractMongoRepository<BoleCompetitionSeason, Long> implements BoleCompetitionSeasonRepository {
    @Override
    protected Class<BoleCompetitionSeason> getEntityType() {
        return BoleCompetitionSeason.class;
    }

    @Override
    protected Long getId(BoleCompetitionSeason boleCompetitionSeason) {
        return boleCompetitionSeason.getId();
    }

    @Override
    public BoleCompetitionSeason getLatestByCid(Long cid) {
      Query q = query(where("cid").is(cid));
      q.with(new Sort(Sort.Direction.DESC, "start_time"));
      return findOneByQuery(q);
    }

    @Override
    public BoleCompetitionSeason findOneBySmsId(long smsId) {
        Query query = new Query(Criteria.where("sms_id").is(smsId));
        return findOneByQuery(query);
    }
}
