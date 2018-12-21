package com.lesports.bole.service.impl;

import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.service.CommonSearchService;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

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
