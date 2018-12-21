package com.lesports.bole.repository.impl;

import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleMatchRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Repository
public class BoleMatchRepositoryImpl extends AbstractMongoRepository<BoleMatch, Long> implements BoleMatchRepository {
    @Override
    protected Class<BoleMatch> getEntityType() {
        return BoleMatch.class;
    }

    @Override
    protected Long getId(BoleMatch boleMatch) {
        return boleMatch.getId();
    }

    @Override
    public BoleMatch findOneBySmsId(long smsId) {
        Query query = new Query(Criteria.where("sms_id").is(smsId));
        return findOneByQuery(query);
    }

    @Override
    public BoleMatch matchByStartTimeAndCompetitors(String startTime, long com1, long com2) {
        String date = StringUtils.substring(startTime, 0, 8);
        Query query = new Query(where("start_time").regex("^" + date));
        query.addCriteria(where("competitors").all(com1, com2));
        query.addCriteria(where("bole_status").ne(BoleStatus.INVALID));
        return findOneByQuery(query);
    }

}
