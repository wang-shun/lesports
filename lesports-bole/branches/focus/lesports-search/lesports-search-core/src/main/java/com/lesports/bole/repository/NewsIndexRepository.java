package com.lesports.bole.repository;

import com.lesports.bole.index.NewsIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface NewsIndexRepository extends ElasticsearchRepository<NewsIndex, Long> {
}
