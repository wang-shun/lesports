package com.lesports.bole.repository.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.repository.BoleOlympicConfigRepository;
import com.lesports.bole.repository.BoleOlympicSettingDataRepository;
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
@Service("olympicSettingRepository")
public class BoleOlympicSettingDataRepositoryImpl extends AbstractMongoRepository<BoleOlympicSettingDataSet, String> implements BoleOlympicSettingDataRepository {
    private static final Logger LOG = LoggerFactory.getLogger(BoleOlympicSettingDataRepositoryImpl.class);

    @Override
    protected Class<BoleOlympicSettingDataSet> getEntityType() {
        return BoleOlympicSettingDataSet.class;
    }

    @Override
    protected String getId(BoleOlympicSettingDataSet entity) {
        return entity.getId();
    }


}