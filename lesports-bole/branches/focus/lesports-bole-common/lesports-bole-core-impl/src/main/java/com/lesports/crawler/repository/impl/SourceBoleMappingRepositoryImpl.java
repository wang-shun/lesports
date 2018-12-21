package com.lesports.crawler.repository.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.repository.BoleCompetitorRepository;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceBoleMapping;
import com.lesports.crawler.model.source.SourceValueType;
import com.lesports.crawler.repository.SourceBoleMappingRepository;
import com.lesports.crawler.repository.SourceMatchRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;

/**
 * SourceBoleMapping接口实现
 * 
 * @author denghui
 *
 */
@Repository
public class SourceBoleMappingRepositoryImpl
        extends AbstractMongoRepository<SourceBoleMapping, String>
        implements SourceBoleMappingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceBoleMappingRepositoryImpl.class);

    @Resource
    private BoleCompetitorRepository boleCompetitorRepository;
    @Resource
    private SourceMatchRepository sourceMatchRepository;

    @Override
    public SourceBoleMapping get(long boleId) {
        Query query = new Query(Criteria.where("bole_id").is(boleId));
        return findOneByQuery(query);
    }

    @Override
    public SourceBoleMapping get(Source source, SourceValueType type, String value, String gameFName) {
        Query query = new Query(Criteria.where("source").is(source.toString()));
        query.addCriteria(Criteria.where("source_value_type").is(type));
        query.addCriteria(Criteria.where("source_value").is(value));
        List<SourceBoleMapping> mappings = findByQuery(query);
        if (mappings.isEmpty()) {
            return null;
        } else if (type != SourceValueType.COMPETITOR || Strings.isNullOrEmpty(gameFName)) {
            return mappings.get(0);
        } else {
            SourceBoleMapping candidate = null;
            // 对阵同名的情况可能存在多个mapping, 返回一个相同大项的
            for (SourceBoleMapping mapping : mappings) {
                BoleCompetitor competitor = boleCompetitorRepository.findOne(mapping.getBoleId());
                if (!Strings.isNullOrEmpty(competitor.getGameName())) {
                    if (gameFName.equals(competitor.getGameName())) {
                        return mapping;
                    }
                } else {
                    candidate = mapping;
                }
            }
            if (candidate == null) {
              LOGGER.info("can not determine mapping for source:{}, type:{}, value:{}, gameFName:{}", source, type, value, gameFName);
            }
            return candidate;
        }
    }

    @Override
    protected Class<SourceBoleMapping> getEntityType() {
        return SourceBoleMapping.class;
    }

    @Override
    protected String getId(SourceBoleMapping entity) {
        return entity.getId();
    }

}
