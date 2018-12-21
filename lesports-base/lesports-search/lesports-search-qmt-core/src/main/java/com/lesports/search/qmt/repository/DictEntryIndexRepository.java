package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.config.DictEntryIndex;


/**
 * @author sunyue7
 *
 */
public interface DictEntryIndexRepository extends ElasticsearchRepository<DictEntryIndex, Long> {
}
