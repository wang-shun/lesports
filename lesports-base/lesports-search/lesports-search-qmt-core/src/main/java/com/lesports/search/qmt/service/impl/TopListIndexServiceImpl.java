package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbd.SbdTopListInternalApis;
import com.lesports.qmt.sbd.model.TopList;
import com.lesports.search.qmt.index.sbd.TopListIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.TopListIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.TopListIndexService;

/**
 * @author sunyue7
 */
@Service
public class TopListIndexServiceImpl extends AbstractSearchService<TopListIndex, Long> implements TopListIndexService {

	@Resource
	private TopListIndexRepository topListIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected TopListIndex getEntityFromRpc(Long id) {
		TopList topList = SbdTopListInternalApis.getTopListById(id);
		if (topList == null) {
			return null;
		}
		TopListIndex topListIndex = buildIndex(new TopListIndex(), topList);
		return topListIndex;
	}

	@Override
	protected TopListIndex doFindOne(Long id) {
		return topListIndexRepository.findOne(id);
	}

	@Override
	public boolean save(TopListIndex entity) {
		topListIndexRepository.save(entity);
		return true;
	}

	@Override
	protected List<TopListIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(SbdTopListInternalApis.getTopListsByIds(ids));
	}

}
