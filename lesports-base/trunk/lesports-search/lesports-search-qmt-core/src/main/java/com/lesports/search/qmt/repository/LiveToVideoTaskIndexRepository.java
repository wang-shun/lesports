package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.transcode.LiveToVideoTaskIndex;

/**
 * @author sunyue7
 *
 */
public interface LiveToVideoTaskIndexRepository extends ElasticsearchRepository<LiveToVideoTaskIndex, Long> {
}
