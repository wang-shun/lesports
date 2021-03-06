package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.transcode.VideoTranscodeTaskIndex;

/**
 * @author sunyue7
 *
 */
public interface VideoTranscodeTaskIndexRepository extends ElasticsearchRepository<VideoTranscodeTaskIndex, Long> {
}
