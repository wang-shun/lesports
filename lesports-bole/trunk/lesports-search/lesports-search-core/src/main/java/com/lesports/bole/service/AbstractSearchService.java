package com.lesports.bole.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.bole.index.SearchIndex;
import com.lesports.sms.api.common.Platform;
import com.lesports.sms.api.common.TvLicence;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public abstract class AbstractSearchService<T extends SearchIndex, ID extends Serializable> implements BoleSearchCrudService<T, ID> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSearchService.class);

    protected Function<LangString, com.lesports.bole.index.LangString> LANG_STRING_FUNCTION = new Function<LangString, com.lesports.bole.index.LangString>() {
        @Nullable
        @Override
        public com.lesports.bole.index.LangString apply(@Nullable LangString input) {
            com.lesports.bole.index.LangString langString = new com.lesports.bole.index.LangString();
            langString.setLanguage(input.getLanguage().getValue());
            langString.setValue(input.getValue());
            return langString;
        }
    };
    protected Function<CountryCode, Integer> COUNTRY_CODE_FUNCTION = new Function<CountryCode, Integer>() {
        @Nullable
        @Override
        public Integer apply(@Nullable CountryCode input) {
            return input.getValue();
        }
    };
    protected static Function<Platform, Integer> PLATFORM_FUNCTION = new Function<Platform, Integer>() {
        @Nullable
        @Override
        public Integer apply(Platform input) {
            return input.getValue();
        }
    };

    protected static Function<TvLicence, Integer> TV_LICENCE_FUNCTION = new Function<TvLicence, Integer>() {
        @Nullable
        @Override
        public Integer apply(TvLicence input) {
            return input.getValue();
        }
    };

    @Override
    public T findOne(ID id) {
        return doFindOne(id);
    }

    @Override
    public boolean save(ID id) {
        T entity = getEntityFromRpc(id);
        if (null == entity) {
            return false;
        }
        return save(entity);
    }

    @Override
    public List<IndexQuery> getBulkData(List<ID> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<IndexQuery> list = Lists.newArrayList();
        for (ID id : ids) {
            T entity = getEntityFromRpc(id);
            if (null == entity) {
                LOG.info("getBulkData getEntityFromRpc is null.id:{}", id);
                continue;
            }
            IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(id)).withObject(entity).build();
            list.add(indexQuery);
        }
        return list;
    }

    @Override
    public boolean delete(ID id) {
        T episodeIndex = doFindOne(id);
        if (null == episodeIndex) {
            return true;
        }
        episodeIndex.setDeleted(true);
        return save(episodeIndex);
    }

    protected abstract T getEntityFromRpc(ID id);


    protected abstract T doFindOne(ID id);
}
