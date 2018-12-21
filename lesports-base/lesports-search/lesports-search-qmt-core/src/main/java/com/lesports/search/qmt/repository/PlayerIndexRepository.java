package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbd.PlayerIndex;


/**
 * @author sunyue7
 *
 */
public interface PlayerIndexRepository extends ElasticsearchRepository<PlayerIndex, Long> {
}
