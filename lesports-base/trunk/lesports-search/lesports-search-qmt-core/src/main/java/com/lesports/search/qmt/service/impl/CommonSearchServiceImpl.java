package com.lesports.search.qmt.service.impl;

import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.service.CommonSearchService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangyu on 2016/5/19.
 */
@Service
public class CommonSearchServiceImpl implements CommonSearchService {

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	public Boolean saveBulk(List<IndexQuery> list) {
		return commonSearchRepository.saveBulk(list);
	}
}
