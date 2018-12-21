package com.lesports.bole.repository;

import com.lesports.bole.index.MatchIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface MatchIndexRepository extends ElasticsearchRepository<MatchIndex, Long> {
}
