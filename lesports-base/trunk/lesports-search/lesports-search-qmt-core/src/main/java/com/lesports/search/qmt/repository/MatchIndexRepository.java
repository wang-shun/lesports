package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbd.MatchIndex;


/**
 * @author sunyue7
 *
 */
public interface MatchIndexRepository extends ElasticsearchRepository<MatchIndex, Long> {
}
