package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.VideoIndex;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface VideoIndexRepository extends ElasticsearchRepository<VideoIndex, Long> {
}
