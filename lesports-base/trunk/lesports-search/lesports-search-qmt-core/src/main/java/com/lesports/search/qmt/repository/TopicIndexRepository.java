package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.TopicIndex;


/**
 * @author sunyue7
 *
 */
public interface TopicIndexRepository extends ElasticsearchRepository<TopicIndex, Long> {
}
