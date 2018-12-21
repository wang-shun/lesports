package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcProgramAlbumInternalApis;
import com.lesports.qmt.sbc.model.ProgramAlbum;
import com.lesports.search.qmt.index.sbc.ProgramAlbumIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.ProgramAlbumIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.ProgramAlbumIndexService;

@Service
public class ProgramAlbumIndexServiceImpl extends AbstractSearchService<ProgramAlbumIndex, Long>
		implements ProgramAlbumIndexService {

	@Resource
	private ProgramAlbumIndexRepository programAlbumIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected ProgramAlbumIndex getEntityFromRpc(Long id) {
		ProgramAlbum album = QmtSbcProgramAlbumInternalApis.getProgramAlbumById(id);
		if (null == album) {
			return null;
		}
		ProgramAlbumIndex albumIndex = buildIndex(new ProgramAlbumIndex(), album);
		return albumIndex;
	}

	@Override
	public boolean save(ProgramAlbumIndex entity) {
		programAlbumIndexRepository.save(entity);
		return true;
	}

	@Override
	protected ProgramAlbumIndex doFindOne(Long aLong) {
		return programAlbumIndexRepository.findOne(aLong);
	}

	@Override
	protected List<ProgramAlbumIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcProgramAlbumInternalApis.getProgramAlbumsByIds(ids));
	}
}
