package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcAlbumInternalApis;
import com.lesports.qmt.sbc.model.Album;
import com.lesports.search.qmt.index.sbc.AlbumIndex;
import com.lesports.search.qmt.repository.AlbumIndexRepository;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.AlbumIndexService;

@Service
public class AlbumIndexServiceImpl extends AbstractSearchService<AlbumIndex, Long> implements AlbumIndexService {

	@Resource
	private AlbumIndexRepository albumIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected AlbumIndex getEntityFromRpc(Long id) {
		Album album = QmtSbcAlbumInternalApis.getAlbumById(id);
		if (null == album) {
			return null;
		}
		AlbumIndex albumIndex = buildIndex(new AlbumIndex(), album);
		return albumIndex;
	}

	@Override
	public boolean save(AlbumIndex entity) {
		albumIndexRepository.save(entity);
		return true;
	}

	@Override
	protected AlbumIndex doFindOne(Long aLong) {
		return albumIndexRepository.findOne(aLong);
	}

	@Override
	protected List<AlbumIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcAlbumInternalApis.getAlbumByIds(ids));
	}
}
