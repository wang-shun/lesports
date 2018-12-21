package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.ProgramAlbumIndex;

/**
 * @author sunyue7
 *
 */
public interface ProgramAlbumIndexRepository extends ElasticsearchRepository<ProgramAlbumIndex, Long> {
}
