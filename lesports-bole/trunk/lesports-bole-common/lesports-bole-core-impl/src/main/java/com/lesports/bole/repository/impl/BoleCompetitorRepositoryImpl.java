package com.lesports.bole.repository.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitorRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
@Repository
public class BoleCompetitorRepositoryImpl extends AbstractMongoRepository<BoleCompetitor, Long> implements BoleCompetitorRepository {
    private Logger LOGGER = LoggerFactory.getLogger(BoleCompetitorRepositoryImpl.class);

    @Override
    protected Class<BoleCompetitor> getEntityType() {
        return BoleCompetitor.class;
    }

    @Override
    protected Long getId(BoleCompetitor boleCompetitor) {
        return boleCompetitor.getId();
    }

    @Override
    public BoleCompetitor findOneBySmsId(long smsId) {
        Query query = new Query(Criteria.where("sms_id").is(smsId));
        return findOneByQuery(query);
    }

    @Override
    public BoleCompetitor matchOne(BoleCompetitorType type, String key, String gameFName) {
        // 只当不按大项查找不能确定时，才按大项查找
        BoleCompetitor competitor = doMatchOne(type, key, null);
        // 如有大项则检查
        if (competitor != null && !Strings.isNullOrEmpty(competitor.getGameName()) && !Strings.isNullOrEmpty(gameFName)
                && !gameFName.equals(competitor.getGameName())) {
            competitor = null;
        }
        
        if (competitor == null && !Strings.isNullOrEmpty(gameFName)) {
            LOGGER.info("use gameFName");
            competitor = doMatchOne(type, key, gameFName);
        }
        return competitor;
    }

    private BoleCompetitor doMatchOne(BoleCompetitorType type, String key, String gameFName) {
        String[] targets = { "nickname", "name" };
        for (String target : targets) {
            Query isQuery = query(where("type").is(type));
            isQuery.addCriteria(where(target).is(key));
            isQuery.addCriteria(where("bole_status").ne(BoleStatus.INVALID));
            // 暂不支持gameFType
            if (gameFName != null) {
                isQuery.addCriteria(where("game_name").is(gameFName));
            }
            long isCount = countByQuery(isQuery);
            if (isCount == 1) {
                return findOneByQuery(isQuery);
            } else if (isCount == 0) {
                Query regexQuery = query(where("type").is(type));
                regexQuery.addCriteria(where(target).regex(key));
                regexQuery.addCriteria(where("bole_status").ne(BoleStatus.INVALID));
                if (gameFName != null) {
                    regexQuery.addCriteria(where("game_name").is(gameFName));
                }
                long regexCount = countByQuery(isQuery);
                if (regexCount == 1) {
                    return findOneByQuery(regexQuery);
                } else {
                    LOGGER.info("match regex failed, target {}, type {}, key {}, gameFName {}, count {}", target, type, key, gameFName, regexCount);
                }
            } else {
                LOGGER.info("match failed, target {}, type {}, key {}, gameFName {}, count {}", target, type, key, gameFName, isCount);
            }
        }

        return null;
    }
}
