/**
 * 
 */
package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.transcode.client.TranscodeInternalApis;
import com.lesports.qmt.transcode.model.VideoTranscodeTask;
import com.lesports.search.qmt.index.transcode.VideoTranscodeTaskIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.VideoTranscodeTaskIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.VideoTranscodeTaskIndexService;

/**
 * @author sunyue7
 *
 */
@Service
public class VideoTranscodeTaskIndexServiceImpl extends AbstractSearchService<VideoTranscodeTaskIndex, Long>
		implements VideoTranscodeTaskIndexService {

	@Resource
	private VideoTranscodeTaskIndexRepository videoTranscodeTaskIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lesports.service.LeCrudService#save(java.lang.Object)
	 */
	@Override
	public boolean save(VideoTranscodeTaskIndex entity) {
		videoTranscodeTaskIndexRepository.save(entity);
		return true;
	}

	@Override
	protected VideoTranscodeTaskIndex getEntityFromRpc(Long id) {
		VideoTranscodeTask entity = TranscodeInternalApis.getEntityById(id, VideoTranscodeTask.class);
		if (null == entity) {
			return null;
		}
		VideoTranscodeTaskIndex index = buildIndex(new VideoTranscodeTaskIndex(), entity);
		return index;
	}

	@Override
	protected VideoTranscodeTaskIndex doFindOne(Long id) {
		return videoTranscodeTaskIndexRepository.findOne(id);
	}

	@Override
	protected List<VideoTranscodeTaskIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(TranscodeInternalApis.getEntitiesByIds(ids, VideoTranscodeTask.class));
	}

}
