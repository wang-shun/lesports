package com.lesports.bole.repository.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.bole.repository.BoleOlympicConfigRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by qiaohongxin on 2016/2/18.
 */
@Service("olympicConfigRepository")
public class BoleOlympicConfigRepositoryImpl extends AbstractMongoRepository<BoleOlympicsLiveConfigSet, Long> implements BoleOlympicConfigRepository {
    private static final Logger LOG = LoggerFactory.getLogger(BoleOlympicConfigRepositoryImpl.class);

    @Override
    protected Class<BoleOlympicsLiveConfigSet> getEntityType() {
        return BoleOlympicsLiveConfigSet.class;
    }

    @Override
    protected Long getId(BoleOlympicsLiveConfigSet entity) {
        return entity.getId();
    }


}