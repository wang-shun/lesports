package com.lesports.search.qmt.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lesports.search.qmt.index.sbc.NewsIndex;


/**
 * @author sunyue7
 *
 */
public interface NewsIndexRepository extends ElasticsearchRepository<NewsIndex, Long> {
}
