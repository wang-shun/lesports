package com.lesports.bole.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.repository.EpisodeIndexRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.EpisodeIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.Episode;
import com.lesports.utils.LeDateUtils;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Service
public class EpisodeIndexServiceImpl extends AbstractSearchService<EpisodeIndex, Long> implements EpisodeIndexService {
	@Resource
	private EpisodeIndexRepository episodeIndexRepository;
	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected EpisodeIndex getEntityFromRpc(Long id) {
		Episode tComboEpisode = SopsInternalApis.getEpisodeById(id);
		if (null == tComboEpisode) {
			return null;
		}
		return buildIndex(tComboEpisode);
	}

	public static EpisodeIndex buildIndex(Episode episode) {
		EpisodeIndex episodeIndex = new EpisodeIndex();
		episodeIndex.setId(episode.getId());
		episodeIndex.setName(episode.getName());
		episodeIndex.setDeleted(episode.getDeleted());
		episodeIndex.setAid(episode.getAid());
		episodeIndex.setHasLive(episode.getHasLive());
		episodeIndex.setStartTime(episode.getStartTime());
		episodeIndex.setIsLephoneChannelMatch(episode.getIsLephoneChannelMatch());
		episodeIndex.setIsLephoneMatch(episode.getIsLephoneMatch());
		episodeIndex.setMid(episode.getMid());
		episodeIndex.setTagIds(episode.getTagIds());
		episodeIndex.setCompetitorIds(episode.getCompetitorIds());
		episodeIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
		episodeIndex.setStatus(
				episode.getStatus() != null ? episode.getStatus().getValue() : LiveShowStatus.NO_LIVE.getValue());
		episodeIndex.setTextLiveStatus(episode.getTextLiveStatus() != null ? episode.getTextLiveStatus().getValue()
				: LiveShowStatus.NO_LIVE.getValue());
		if (CollectionUtils.isNotEmpty(episode.getLivePlatforms())) {
			episodeIndex.setLivePlatforms(
					Lists.transform(Lists.newArrayList(episode.getLivePlatforms()), PLATFORM_FUNCTION));
		}
		if (null != episode.getAllowCountry()) {
			episodeIndex.setAllowCountry(episode.getAllowCountry().getValue());
		}
		episodeIndex.setOnline(episode.getOnline());
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
	public PageResult<EpisodeIndex> findByParams(long id, String name, Long mid, Long aid, Integer type,
			Boolean hasLive, List<String> startTime, Integer countryCode, Integer languageCode, int page, int count,
			Boolean isLephoneChannelMatch, Boolean isLephoneMatch) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		boolQueryBuilder.must(new TermQueryBuilder("deleted", false));
		if (id > 0) {
			boolQueryBuilder.must(new TermQueryBuilder("id", id));
		}
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(new MatchQueryBuilder("name", name).type(MatchQueryBuilder.Type.PHRASE));
		}
		if (null != mid) {
			boolQueryBuilder.must(new TermQueryBuilder("mid", mid));
		}
		if (null != aid) {
			boolQueryBuilder.must(new TermQueryBuilder("aid", aid));
		}
		if (null != type) {
			boolQueryBuilder.must(new TermQueryBuilder("type", type));
		}
		if (null != type) {
			boolQueryBuilder.must(new TermQueryBuilder("hasLive", hasLive));
		}
		if (null != countryCode) {
			boolQueryBuilder.must(new TermQueryBuilder("allowCountry", countryCode));
		}
		if (null != languageCode) {
			boolQueryBuilder.must(new TermQueryBuilder("languageCode", languageCode));
		}
		if (CollectionUtils.isNotEmpty(startTime)) {
			if (startTime.size() == 2) {
				if (null != startTime.get(0)) {
					boolQueryBuilder
							.must(new RangeQueryBuilder("startTime").gte(startTime.get(0)).lt(startTime.get(1)));
				} else {
					boolQueryBuilder.must(new RangeQueryBuilder("startTime").lt(startTime.get(1)));
				}
			} else {
				boolQueryBuilder.must(new RangeQueryBuilder("startTime").gte(startTime.get(0)));
			}
		}
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
				.withPageable(new PageRequest(page, count, new Sort(Sort.Direction.DESC, "startTime")));

		Page<EpisodeIndex> res = commonSearchRepository.findBySearchQuery(nativeSearchQueryBuilder.build(),
				EpisodeIndex.class);
		return new PageResult<>(res);
	}
}
