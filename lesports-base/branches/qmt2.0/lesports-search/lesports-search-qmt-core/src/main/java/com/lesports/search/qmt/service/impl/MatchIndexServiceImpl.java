package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.search.qmt.index.sbd.MatchIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.MatchIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.MatchIndexService;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Service
public class MatchIndexServiceImpl extends AbstractSearchService<MatchIndex, Long> implements MatchIndexService {

	@Resource
	private MatchIndexRepository matchIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected MatchIndex getEntityFromRpc(Long id) {
		Match tMatch = SbdMatchInternalApis.getMatchById(id);
		if (null == tMatch) {
			return null;
		}
		MatchIndex matchIndex = buildIndex(new MatchIndex(), tMatch);
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
	protected List<MatchIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(SbdMatchInternalApis.getMatchesByIds(ids));
	}
}
