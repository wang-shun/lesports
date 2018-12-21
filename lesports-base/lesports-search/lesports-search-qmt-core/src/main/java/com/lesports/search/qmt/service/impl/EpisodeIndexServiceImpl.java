package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcEpisodeInternalApis;
import com.lesports.qmt.sbc.model.Episode;
import com.lesports.search.qmt.index.sbc.EpisodeIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.EpisodeIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.EpisodeIndexService;

@Service
public class EpisodeIndexServiceImpl extends AbstractSearchService<EpisodeIndex, Long> implements EpisodeIndexService {

	@Resource
	private EpisodeIndexRepository episodeIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected EpisodeIndex getEntityFromRpc(Long id) {
		Episode tComboEpisode = QmtSbcEpisodeInternalApis.getEpisodeById(id);
		if (null == tComboEpisode) {
			return null;
		}
		EpisodeIndex episodeIndex = buildIndex(new EpisodeIndex(), tComboEpisode);
		return episodeIndex;
	}

	@Override
	public boolean save(EpisodeIndex entity) {
		episodeIndexRepository.save(entity);
		return true;
	}

	@Override
	protected EpisodeIndex doFindOne(Long aLong) {
		return episodeIndexRepository.findOne(aLong);
	}

	@Override
	protected List<EpisodeIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcEpisodeInternalApis.getEpisodesByIds(ids));
	}
}
