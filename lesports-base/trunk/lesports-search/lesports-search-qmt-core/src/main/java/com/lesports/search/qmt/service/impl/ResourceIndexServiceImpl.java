package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcResourceInternalApis;
import com.lesports.qmt.sbc.model.QmtResource;
import com.lesports.search.qmt.index.sbc.ResourceIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.ResourceIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.ResourceIndexService;

/**
 * @author sunyue7
 */
@Service
public class ResourceIndexServiceImpl extends AbstractSearchService<ResourceIndex, Long>
		implements ResourceIndexService {

	@Resource
	private ResourceIndexRepository resourceIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected ResourceIndex getEntityFromRpc(Long id) {
		QmtResource resource = QmtSbcResourceInternalApis.getResourceById(id);
		if (resource == null) {
			return null;
		}
		ResourceIndex resourceIndex = buildIndex(new ResourceIndex(), resource);
		return resourceIndex;
	}

	@Override
	protected ResourceIndex doFindOne(Long id) {
		return resourceIndexRepository.findOne(id);
	}

	@Override
	public boolean save(ResourceIndex entity) {
		resourceIndexRepository.save(entity);
		return true;
	}

	@Override
	protected List<ResourceIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcResourceInternalApis.getResourceListByIds(ids));
	}
}
