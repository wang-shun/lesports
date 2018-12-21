package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbd.CompetitionIndex;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
public interface CompetitionIndexRepository extends ElasticsearchRepository<CompetitionIndex, Long> {
}
