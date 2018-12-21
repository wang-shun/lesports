/**
 * 
 */
package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.transcode.client.TranscodeInternalApis;
import com.lesports.qmt.transcode.model.LiveToVideoTask;
import com.lesports.search.qmt.index.transcode.LiveToVideoTaskIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.LiveToVideoTaskIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.LiveToVideoTaskIndexService;

/**
 * @author sunyue7
 *
 */
@Service
public class LiveToVideoTaskIndexServiceImpl extends AbstractSearchService<LiveToVideoTaskIndex, Long>
		implements LiveToVideoTaskIndexService {

	@Resource
	private LiveToVideoTaskIndexRepository liveToVideoTaskIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lesports.service.LeCrudService#save(java.lang.Object)
	 */
	@Override
	public boolean save(LiveToVideoTaskIndex entity) {
		liveToVideoTaskIndexRepository.save(entity);
		return true;
	}

	@Override
	protected LiveToVideoTaskIndex getEntityFromRpc(Long id) {
		LiveToVideoTask entity = TranscodeInternalApis.getEntityById(id, LiveToVideoTask.class);
		if (null == entity) {
			return null;
		}
		LiveToVideoTaskIndex index = buildIndex(new LiveToVideoTaskIndex(), entity);
		return index;
	}

	@Override
	protected LiveToVideoTaskIndex doFindOne(Long id) {
		return liveToVideoTaskIndexRepository.findOne(id);
	}

	@Override
	protected List<LiveToVideoTaskIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(TranscodeInternalApis.getEntitiesByIds(ids, LiveToVideoTask.class));
	}

}
