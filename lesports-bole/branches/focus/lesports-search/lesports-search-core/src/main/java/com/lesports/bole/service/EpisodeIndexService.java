package com.lesports.bole.service;

import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.utils.PageResult;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface EpisodeIndexService extends BoleSearchCrudService<EpisodeIndex, Long> {

	PageResult<EpisodeIndex> findByParams(long id, String name, Long mid, Long aid, Integer type, Boolean hasLive,
			List<String> startTime, Integer countryCode, Integer languageCode, int page, int count,
			Boolean isLephoneChannelMatch, Boolean isLephoneMatch);
}
