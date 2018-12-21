package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.search.qmt.index.config.DictEntryIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.DictEntryIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.DictEntryIndexService;

@Service
public class DictEntryIndexServiceImpl extends AbstractSearchService<DictEntryIndex, Long>
		implements DictEntryIndexService {

	@Resource
	private DictEntryIndexRepository dictEntryIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected DictEntryIndex getEntityFromRpc(Long id) {
		DictEntry dict = QmtConfigDictInternalApis.getDictById(id);
		if (null == dict) {
			return null;
		}
		DictEntryIndex dictEntryIndex = buildIndex(new DictEntryIndex(), dict);
		return dictEntryIndex;
	}

	@Override
	protected DictEntryIndex doFindOne(Long aLong) {
		return dictEntryIndexRepository.findOne(aLong);
	}

	@Override
	public boolean save(DictEntryIndex entity) {
		dictEntryIndexRepository.save(entity);
		return true;
	}

	@Override
	protected List<DictEntryIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtConfigDictInternalApis.getDictsByIds(ids));
	}
}
