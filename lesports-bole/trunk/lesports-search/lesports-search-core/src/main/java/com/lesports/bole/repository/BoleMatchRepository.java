package com.lesports.bole.repository;

import com.lesports.bole.index.BoleMatchIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/5
 */
public interface BoleMatchRepository extends ElasticsearchRepository<BoleMatchIndex, Long> {
}
