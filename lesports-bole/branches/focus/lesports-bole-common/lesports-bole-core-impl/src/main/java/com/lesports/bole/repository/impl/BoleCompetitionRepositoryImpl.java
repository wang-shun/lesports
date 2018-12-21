package com.lesports.bole.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Repository
public class BoleCompetitionRepositoryImpl extends AbstractMongoRepository<BoleCompetition, Long> implements BoleCompetitionRepository {
    private Logger LOGGER = LoggerFactory.getLogger(BoleCompetitionRepositoryImpl.class);

    @Override
    protected Class<BoleCompetition> getEntityType() {
        return BoleCompetition.class;
    }

    @Override
    protected Long getId(BoleCompetition boleCompetition) {
        return boleCompetition.getId();
    }

    @Override
    public BoleCompetition findOneBySmsId(long smsId) {
        Query query = new Query(Criteria.where("sms_id").is(smsId));
        return findOneByQuery(query);
    }

    @Override
    public BoleCompetition matchByAbbreviation(String abbreviation) {
        Query isQuery = query(where("abbreviation").is(abbreviation));
        isQuery.addCriteria(where("bole_status").ne(BoleStatus.INVALID));
        long isCount = countByQuery(isQuery);
        if (isCount == 1) {
            return findOneByQuery(isQuery);
        } else if (isCount == 0) {
            Query regexQuery = query(where("abbreviation").regex(abbreviation));
            regexQuery.addCriteria(where("bole_status").ne(BoleStatus.INVALID));
            long regexCount = countByQuery(isQuery);
            if (regexCount == 1) {
                return findOneByQuery(regexQuery);
            } else {
                LOGGER.info("match regex failed abbreviation {}, count {}", abbreviation, regexCount);
            }
        } else {
            LOGGER.info("match failed abbreviation {}, count {}", abbreviation, isCount);
        }

        return null;
    }

    @Override
    public BoleCompetition getByName(String name) {
        Query query = query(where("name").is(name));
        return findOneByQuery(query);
    }
}
