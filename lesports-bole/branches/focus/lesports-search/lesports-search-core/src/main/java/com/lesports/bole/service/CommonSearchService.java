package com.lesports.bole.service;

import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.List;

/**
 * Created by yangyu on 2016/5/19.
 */
public interface CommonSearchService {
	Boolean saveBulk(List<IndexQuery> list);
}
