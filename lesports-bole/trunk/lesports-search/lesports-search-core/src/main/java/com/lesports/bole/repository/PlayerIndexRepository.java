package com.lesports.bole.repository;

import com.lesports.bole.index.PlayerIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by yangyu on 16/8/3.
 */
public interface PlayerIndexRepository extends ElasticsearchRepository<PlayerIndex, Long> {
}
