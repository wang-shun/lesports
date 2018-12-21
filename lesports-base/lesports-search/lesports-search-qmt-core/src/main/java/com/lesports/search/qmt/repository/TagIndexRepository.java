package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.config.TagIndex;


/**
 * @author sunyue7
 *
 */
public interface TagIndexRepository extends ElasticsearchRepository<TagIndex, Long> {
}
