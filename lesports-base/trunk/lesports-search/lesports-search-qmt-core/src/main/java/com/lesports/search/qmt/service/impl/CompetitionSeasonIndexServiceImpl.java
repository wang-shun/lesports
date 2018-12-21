package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbd.SbdCompetitionSeasonInternalApis;
import com.lesports.qmt.sbd.model.CompetitionSeason;
import com.lesports.search.qmt.index.sbd.CompetitionSeasonIndex;
import com.lesports.search.qmt.repository.CompetitionSeasonIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.CompetitionSeasonIndexService;

@Service
public class CompetitionSeasonIndexServiceImpl extends AbstractSearchService<CompetitionSeasonIndex, Long>
		implements CompetitionSeasonIndexService {

	@Resource
	private CompetitionSeasonIndexRepository competitionSeasonIndexRepository;

	@Override
	protected CompetitionSeasonIndex getEntityFromRpc(Long idLong) {
		CompetitionSeason competitionSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(idLong);
		if (null == competitionSeason) {
			return null;
		}
		return buildIndex(new CompetitionSeasonIndex(), competitionSeason);
	}

	@Override
	public boolean save(CompetitionSeasonIndex entity) {
		competitionSeasonIndexRepository.save(entity);
		return true;
	}

	@Override
	protected CompetitionSeasonIndex doFindOne(Long aLong) {
		return competitionSeasonIndexRepository.findOne(aLong);
	}

	@Override
	protected List<CompetitionSeasonIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(SbdCompetitionSeasonInternalApis.getCompetitionSeasonsByIds(ids));
	}
}
