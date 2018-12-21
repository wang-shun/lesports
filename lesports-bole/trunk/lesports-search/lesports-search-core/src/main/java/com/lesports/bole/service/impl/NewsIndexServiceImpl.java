package com.lesports.bole.service.impl;

import com.google.common.collect.Lists;
import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.repository.NewsIndexRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.NewsIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.News;
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

import javax.annotation.Resource;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Service
public class NewsIndexServiceImpl extends AbstractSearchService<NewsIndex, Long> implements NewsIndexService {
	@Resource
	private NewsIndexRepository newsIndexRepository;
	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected NewsIndex getEntityFromRpc(Long idLong) {
		News news = SopsInternalApis.getNewsById(idLong);
		if (null == news) {
			return null;
		}
		return buildIndex(news);
	}

	public static NewsIndex buildIndex(News news) {
		NewsIndex newsIndex = new NewsIndex();
		newsIndex.setId(news.getId());
		newsIndex.setName(news.getName());
		newsIndex.setUpdateAt(news.getUpdateAt());
		newsIndex.setMids(Lists.newArrayList(news.getMids()));
		newsIndex.setTagIds(news.getTagIds());
		newsIndex.setTids(news.getTids());
		if (null != news.getType()) {
			newsIndex.setNewsType(news.getType().getValue());
		}
		if (null != news.getOnline()) {
			newsIndex.setOnlineStatus(news.getOnline().getValue());
		}
		if (null != news.getAllowCountry()) {
			newsIndex.setAllowCountry(news.getAllowCountry().getValue());
		}
		if (null != news.getLanguageCode()) {
			newsIndex.setLanguageCode(news.getLanguageCode().getValue());
		}
		if (CollectionUtils.isNotEmpty(news.getSupportLicences())) {
			newsIndex.setSupportLicences(
					Lists.transform(Lists.newArrayList(news.getSupportLicences()), TV_LICENCE_FUNCTION));
		}
		newsIndex.setPublishAt(news.getPublishAt());
		newsIndex.setDeleted(news.getDeleted());
		if (CollectionUtils.isNotEmpty(news.getPlatforms())) {
			newsIndex.setPlatforms(Lists.transform(Lists.newArrayList(news.getPlatforms()), PLATFORM_FUNCTION));
		}
		return newsIndex;
	}

	@Override
	public boolean save(NewsIndex entity) {
		newsIndexRepository.save(entity);
		return true;
	}

	@Override
	protected NewsIndex doFindOne(Long aLong) {
		return newsIndexRepository.findOne(aLong);
	}

	@Override
	public PageResult<NewsIndex> findByParams(long id, String name, Integer newsType, Integer onlineStatus, Integer mid,
			Integer platform, Integer supportLicence, List<String> publishAt, Integer countryCode, Integer languageCode,
			int page, int count) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		boolQueryBuilder.must(new TermQueryBuilder("deleted", false));
		if (id > 0) {
			boolQueryBuilder.must(new TermQueryBuilder("id", id));
		}
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(new MatchQueryBuilder("name", name).type(MatchQueryBuilder.Type.PHRASE));
		}
		if (null != newsType) {
			boolQueryBuilder.must(new TermQueryBuilder("newsType", newsType));
		}
		if (null != onlineStatus) {
			boolQueryBuilder.must(new TermQueryBuilder("onlineStatus", onlineStatus));
		}
		if (null != mid) {
			boolQueryBuilder.must(new TermQueryBuilder("mids", mid));
		}
		if (null != platform) {
			boolQueryBuilder.must(new TermQueryBuilder("platforms", platform));
		}
		if (null != countryCode) {
			boolQueryBuilder.must(new TermQueryBuilder("allowCountry", countryCode));
		}
		if (null != languageCode) {
			boolQueryBuilder.must(new TermQueryBuilder("languageCode", languageCode));
		}
		if (null != supportLicence) {
			boolQueryBuilder.must(new TermQueryBuilder("supportLicences", supportLicence));
		}
		if (CollectionUtils.isNotEmpty(publishAt)) {
			if (publishAt.size() == 2) {
				if (null != publishAt.get(0)) {
					boolQueryBuilder
							.must(new RangeQueryBuilder("publishAt").gte(publishAt.get(0)).lt(publishAt.get(1)));
				} else {
					boolQueryBuilder.must(new RangeQueryBuilder("publishAt").lt(publishAt.get(1)));
				}
			} else {
				boolQueryBuilder.must(new RangeQueryBuilder("publishAt").gte(publishAt.get(0)));
			}
		}
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
				.withPageable(new PageRequest(page, count, new Sort(Sort.Direction.DESC, "publishAt")));

		Page<NewsIndex> res = commonSearchRepository.findBySearchQuery(nativeSearchQueryBuilder.build(),
				NewsIndex.class);
		return new PageResult<>(res);
	}

}
