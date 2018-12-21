package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.EpisodeIndex;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
public interface EpisodeIndexRepository extends ElasticsearchRepository<EpisodeIndex, Long> {
}
