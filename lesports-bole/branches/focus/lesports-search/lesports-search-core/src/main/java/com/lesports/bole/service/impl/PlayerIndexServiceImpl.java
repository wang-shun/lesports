package com.lesports.bole.service.impl;

import com.google.common.collect.Lists;
import com.lesports.bole.index.PlayerIndex;
import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.repository.PlayerIndexRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.PlayerIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Player;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by yangyu on 16/8/3.
 */
@Service
public class PlayerIndexServiceImpl extends AbstractSearchService<PlayerIndex, Long> implements PlayerIndexService{

	private static final Logger LOG = LoggerFactory.getLogger(PlayerIndexServiceImpl.class);

	@Resource
	private PlayerIndexRepository playerIndexRepository;
	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected PlayerIndex getEntityFromRpc(Long id) {
		Player player = SbdsInternalApis.getPlayerById(id);
		if (player == null){
			return null;
		}
		PlayerIndex playerIndex = new PlayerIndex();
		playerIndex.setId(player.getId());
		playerIndex.setName(player.getName());
		playerIndex.setDeleted(player.getDeleted());
		playerIndex.setCids(player.getCids());
		playerIndex.setEnglishName(player.getEnglishName());
		playerIndex.setGameFType(player.getGameFType());
		if (CollectionUtils.isNotEmpty(player.getAllowCountries())) {
			playerIndex.setAllowCountries(Lists.transform(player.getAllowCountries(), COUNTRY_CODE_FUNCTION));
		}
		playerIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
		if (CollectionUtils.isNotEmpty(player.getMultiLangNames())) {
			playerIndex.setMultiLangNames2(Lists.transform(player.getMultiLangNames(), LANG_STRING_FUNCTION));
		}
		return playerIndex;
	}

	@Override
	protected PlayerIndex doFindOne(Long id) {
		return playerIndexRepository.findOne(id);
	}

	@Override
	public boolean save(PlayerIndex entity) {
		playerIndexRepository.save(entity);
		return true;
	}

	@Override
	public PageResult<PlayerIndex> findByParams(long id, String name, String englishName, Long cid, Long gameFType, Integer countryCode, int page, int count) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		boolQueryBuilder.must(new TermQueryBuilder("deleted", false));
		if (id > 0){
			boolQueryBuilder.must(new TermQueryBuilder("id", id));
		}
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(new NestedQueryBuilder("multiLangNames2", new MatchQueryBuilder("multiLangNames2.value", name).type(MatchQueryBuilder.Type.PHRASE)));
		}
		if (StringUtils.isNotEmpty(englishName)) {
			boolQueryBuilder.must(new MatchQueryBuilder("englishName", englishName).type(MatchQueryBuilder.Type.PHRASE));
		}
		if (null != cid) {
			boolQueryBuilder.must(new TermQueryBuilder("cids", cid));
		}
		if (null != countryCode) {
			boolQueryBuilder.must(new TermQueryBuilder("allowCountries", countryCode));
		}
		if (null != gameFType) {
			boolQueryBuilder.must(new TermQueryBuilder("gameFType", gameFType));
		}
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
				.withQuery(boolQueryBuilder)
				.withPageable(new PageRequest(page, count, new Sort(Sort.Direction.DESC, "updateAt")));
		Page<PlayerIndex> res = commonSearchRepository.findBySearchQuery(nativeSearchQueryBuilder.build(), PlayerIndex.class);
		return new PageResult<>(res);
	}
}
