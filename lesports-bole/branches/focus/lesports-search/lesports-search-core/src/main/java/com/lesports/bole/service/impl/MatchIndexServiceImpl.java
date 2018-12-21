package com.lesports.bole.service.impl;

import com.google.common.collect.Lists;
import com.lesports.bole.index.MatchIndex;
import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.repository.MatchIndexRepository;
import com.lesports.bole.service.AbstractSearchService;
import com.lesports.bole.service.MatchIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Match;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Service
public class MatchIndexServiceImpl extends AbstractSearchService<MatchIndex, Long> implements MatchIndexService {
    @Resource
    private MatchIndexRepository matchIndexRepository;
	@Resource
	private CommonSearchRepository commonSearchRepository;

    @Override
    protected MatchIndex getEntityFromRpc(Long id) {
        Match tMatch = SbdsInternalApis.getMatchById(id);
        if (null == tMatch) {
            return null;
        }
        MatchIndex matchIndex = new MatchIndex();
        matchIndex.setId(tMatch.getId());
        matchIndex.setName(tMatch.getName());
		matchIndex.setStartTime(tMatch.getStartTime());
		if (CollectionUtils.isNotEmpty(tMatch.getAllowCountries())) {
			matchIndex.setAllowCountries(Lists.transform(tMatch.getAllowCountries(), COUNTRY_CODE_FUNCTION));
		}
		matchIndex.setCid(tMatch.getCid());
		matchIndex.setCsid(tMatch.getCsid());
		matchIndex.setDeleted(tMatch.getDeleted());
        matchIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
		if (CollectionUtils.isNotEmpty(tMatch.getMultiLangNames())) {
			matchIndex.setMultiLangNames2(Lists.transform(tMatch.getMultiLangNames(), LANG_STRING_FUNCTION));
		}
        return matchIndex;
    }

    @Override
    public boolean save(MatchIndex entity) {
        matchIndexRepository.save(entity);
        return true;
    }

    @Override
    protected MatchIndex doFindOne(Long aLong) {
        return matchIndexRepository.findOne(aLong);
    }

	@Override
	public PageResult<MatchIndex> findByParams(long id, String name, Long cid, Long csid, List<String> startTime, Integer countryCode, int page, int count) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		boolQueryBuilder.must(new TermQueryBuilder("deleted", false));
		if (id > 0){
			boolQueryBuilder.must(new TermQueryBuilder("id", id));
		}
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(new NestedQueryBuilder("multiLangNames2", new MatchQueryBuilder("multiLangNames2.value", name).type(MatchQueryBuilder.Type.PHRASE)));
		}
		if (null != cid) {
			boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
		}
		if (null != csid) {
			boolQueryBuilder.must(new TermQueryBuilder("csid", csid));
		}
		if (null != countryCode) {
			boolQueryBuilder.must(new TermQueryBuilder("allowCountries", countryCode));
		}
		if (CollectionUtils.isNotEmpty(startTime)) {
			if (startTime.size() == 2) {
				if (null != startTime.get(0)) {
					boolQueryBuilder.must(new RangeQueryBuilder("startTime").gte(startTime.get(0)).lt(startTime.get(1)));
				} else {
					boolQueryBuilder.must(new RangeQueryBuilder("startTime").lt(startTime.get(1)));
				}
			} else {
				boolQueryBuilder.must(new RangeQueryBuilder("startTime").gte(startTime.get(0)));
			}
		}
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
				.withQuery(boolQueryBuilder)
				.withPageable(new PageRequest(page, count, new Sort(Sort.Direction.DESC, "startTime")));

		Page<MatchIndex> res = commonSearchRepository.findBySearchQuery(nativeSearchQueryBuilder.build(), MatchIndex.class);
		return new PageResult<>(res);
	}
}
