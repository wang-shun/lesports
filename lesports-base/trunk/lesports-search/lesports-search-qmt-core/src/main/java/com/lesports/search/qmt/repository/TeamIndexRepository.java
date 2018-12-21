package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbd.TeamIndex;


/**
 * @author sunyue7
 *
 */
public interface TeamIndexRepository extends ElasticsearchRepository<TeamIndex, Long> {
}
