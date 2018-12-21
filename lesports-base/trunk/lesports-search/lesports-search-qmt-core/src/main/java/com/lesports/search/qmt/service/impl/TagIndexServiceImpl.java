package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.config.client.QmtConfigTagInternalApis;
import com.lesports.qmt.config.model.Tag;
import com.lesports.search.qmt.index.config.TagIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.TagIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.TagIndexService;

@Service
public class TagIndexServiceImpl extends AbstractSearchService<TagIndex, Long> implements TagIndexService {

	@Resource
	private TagIndexRepository tagIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected TagIndex getEntityFromRpc(Long id) {
		Tag tag = QmtConfigTagInternalApis.getTagById(id);
		if (null == tag) {
			return null;
		}
		TagIndex tagIndex = buildIndex(new TagIndex(), tag);
		return tagIndex;
	}

	@Override
	protected TagIndex doFindOne(Long aLong) {
		return tagIndexRepository.findOne(aLong);
	}

	@Override
	public boolean save(TagIndex entity) {
		tagIndexRepository.save(entity);
		return true;
	}

	@Override
	protected List<TagIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtConfigTagInternalApis.getTagsByIds(ids));
	}
}
