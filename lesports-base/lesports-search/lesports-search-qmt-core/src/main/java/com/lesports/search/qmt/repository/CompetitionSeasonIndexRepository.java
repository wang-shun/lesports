package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbd.CompetitionSeasonIndex;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
public interface CompetitionSeasonIndexRepository extends ElasticsearchRepository<CompetitionSeasonIndex, Long> {
}
