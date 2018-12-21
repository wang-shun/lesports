package com.lesports.search.qmt.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

import com.lesports.search.qmt.repository.CommonSearchRepository;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Repository
public class CommonSearchRepositoryImpl<T, ID extends Serializable> implements CommonSearchRepository {

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public <T> Page<T> findBySearchQuery(SearchQuery searchQuery, Class<T> clazz) {
		return elasticsearchTemplate.queryForPage(searchQuery, clazz);
	}

	@Override
	public Boolean saveBulk(List<IndexQuery> list) {
		elasticsearchTemplate.bulkIndex(list);
		return true;
	}

}
