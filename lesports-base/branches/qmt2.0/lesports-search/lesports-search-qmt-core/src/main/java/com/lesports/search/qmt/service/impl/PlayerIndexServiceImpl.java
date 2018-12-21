package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbd.SbdPlayerInternalApis;
import com.lesports.qmt.sbd.model.Player;
import com.lesports.search.qmt.index.sbd.PlayerIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.PlayerIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.PlayerIndexService;

/**
 * @author sunyue7
 */
@Service
public class PlayerIndexServiceImpl extends AbstractSearchService<PlayerIndex, Long> implements PlayerIndexService {

	@Resource
	private PlayerIndexRepository playerIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected PlayerIndex getEntityFromRpc(Long id) {
		Player player = SbdPlayerInternalApis.getPlayerById(id);
		if (player == null) {
			return null;
		}
		PlayerIndex playerIndex = buildIndex(new PlayerIndex(), player);
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
	protected List<PlayerIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(SbdPlayerInternalApis.getPlayersByIds(ids));
	}
}
