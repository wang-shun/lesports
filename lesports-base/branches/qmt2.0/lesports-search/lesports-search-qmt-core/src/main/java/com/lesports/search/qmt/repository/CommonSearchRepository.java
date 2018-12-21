package com.lesports.search.qmt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/12
 */
public interface CommonSearchRepository {

	<T> Page<T> findBySearchQuery(SearchQuery searchQuery, Class<T> clazz);

	Boolean saveBulk(List<IndexQuery> list);
}
