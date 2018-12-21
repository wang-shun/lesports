package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbd.SbdCompetitionInternalApis;
import com.lesports.qmt.sbd.model.Competition;
import com.lesports.search.qmt.index.sbd.CompetitionIndex;
import com.lesports.search.qmt.repository.CompetitionIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.CompetitionIndexService;

@Service
public class CompetitionIndexServiceImpl extends AbstractSearchService<CompetitionIndex, Long>
		implements CompetitionIndexService {

	@Resource
	private CompetitionIndexRepository competitionIndexRepository;

	@Override
	protected CompetitionIndex getEntityFromRpc(Long idLong) {
		Competition competition = SbdCompetitionInternalApis.getCompetitionById(idLong);
		if (null == competition) {
			return null;
		}
		return buildIndex(new CompetitionIndex(), competition);
	}

	@Override
	public boolean save(CompetitionIndex entity) {
		competitionIndexRepository.save(entity);
		return true;
	}

	@Override
	protected CompetitionIndex doFindOne(Long aLong) {
		return competitionIndexRepository.findOne(aLong);
	}

	@Override
	protected List<CompetitionIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(SbdCompetitionInternalApis.getCompetitionsByIds(ids));
	}
}
