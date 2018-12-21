package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.ResourceIndex;


/**
 * @author sunyue7
 *
 */
public interface ResourceIndexRepository extends ElasticsearchRepository<ResourceIndex, Long> {
}
