package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.AlbumIndex;


/**
 * @author sunyue7
 *
 */
public interface AlbumIndexRepository extends ElasticsearchRepository<AlbumIndex, Long> {
}
