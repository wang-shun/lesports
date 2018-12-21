package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbd.SbdTeamInternalApis;
import com.lesports.qmt.sbd.model.Team;
import com.lesports.search.qmt.index.sbd.TeamIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.TeamIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.TeamIndexService;

/**
 * @author sunyue7
 */
@Service
public class TeamIndexServiceImpl extends AbstractSearchService<TeamIndex, Long> implements TeamIndexService {

	@Resource
	private TeamIndexRepository teamIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected TeamIndex getEntityFromRpc(Long id) {
		Team team = SbdTeamInternalApis.getTeamById(id);
		if (team == null) {
			return null;
		}
		TeamIndex teamIndex = buildIndex(new TeamIndex(), team);
		return teamIndex;
	}

	@Override
	protected TeamIndex doFindOne(Long id) {
		return teamIndexRepository.findOne(id);
	}

	@Override
	public boolean save(TeamIndex entity) {
		teamIndexRepository.save(entity);
		return true;
	}

	@Override
	protected List<TeamIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(SbdTeamInternalApis.getTeamsByIds(ids));
	}
}
