package com.lesports.bole.repository;

import com.lesports.bole.index.VideoIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface VideoIndexRepository extends ElasticsearchRepository<VideoIndex, Long> {
}
