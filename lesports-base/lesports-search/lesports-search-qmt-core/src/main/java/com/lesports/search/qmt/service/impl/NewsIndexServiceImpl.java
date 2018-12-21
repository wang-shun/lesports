package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcNewsInternalApis;
import com.lesports.qmt.sbc.model.News;
import com.lesports.search.qmt.index.sbc.NewsIndex;
import com.lesports.search.qmt.repository.NewsIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.NewsIndexService;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Service
public class NewsIndexServiceImpl extends AbstractSearchService<NewsIndex, Long> implements NewsIndexService {

	@Resource
	private NewsIndexRepository newsIndexRepository;

	@Override
	protected NewsIndex getEntityFromRpc(Long idLong) {
		News news = QmtSbcNewsInternalApis.getNewsById(idLong);
		if (null == news) {
			return null;
		}
		return buildIndex(new NewsIndex(), news);
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
	protected List<NewsIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcNewsInternalApis.getNewsByIds(ids));
	}

}
