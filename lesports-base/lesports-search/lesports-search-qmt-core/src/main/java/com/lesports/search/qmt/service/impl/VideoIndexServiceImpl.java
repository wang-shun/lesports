package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcVideoInternalApis;
import com.lesports.qmt.sbc.model.Video;
import com.lesports.search.qmt.index.sbc.VideoIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.VideoIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.VideoIndexService;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Service
public class VideoIndexServiceImpl extends AbstractSearchService<VideoIndex, Long> implements VideoIndexService {

	@Resource
	private VideoIndexRepository videoIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected VideoIndex getEntityFromRpc(Long idLong) {
		Video tVideo = QmtSbcVideoInternalApis.getVideoById(idLong);
		if (null == tVideo) {
			return null;
		}
		VideoIndex videoIndex = buildIndex(new VideoIndex(), tVideo);
		return videoIndex;
	}

	@Override
	public boolean save(VideoIndex entity) {
		videoIndexRepository.save(entity);
		return true;
	}

	@Override
	protected VideoIndex doFindOne(Long aLong) {
		return videoIndexRepository.findOne(aLong);
	}

	@Override
	protected List<VideoIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcVideoInternalApis.getVideoByIds(ids));
	}
}
