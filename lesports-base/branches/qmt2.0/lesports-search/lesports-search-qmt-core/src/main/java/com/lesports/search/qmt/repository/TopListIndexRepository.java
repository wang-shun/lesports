package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbd.TopListIndex;


/**
 * @author sunyue7
 *
 */
public interface TopListIndexRepository extends ElasticsearchRepository<TopListIndex, Long> {
}
