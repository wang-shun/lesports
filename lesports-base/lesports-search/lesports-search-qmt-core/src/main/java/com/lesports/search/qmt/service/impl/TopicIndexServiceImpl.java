package com.lesports.search.qmt.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lesports.qmt.sbc.client.QmtSbcTopicInternalApis;
import com.lesports.qmt.sbc.model.Topic;
import com.lesports.search.qmt.index.sbc.TopicIndex;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.repository.TopicIndexRepository;
import com.lesports.search.qmt.service.AbstractSearchService;
import com.lesports.search.qmt.service.TopicIndexService;

@Service
public class TopicIndexServiceImpl extends AbstractSearchService<TopicIndex, Long> implements TopicIndexService {

	@Resource
	private TopicIndexRepository topicIndexRepository;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	@Override
	protected TopicIndex getEntityFromRpc(Long id) {
		Topic topic = QmtSbcTopicInternalApis.getTopicById(id);
		if (null == topic) {
			return null;
		}
		TopicIndex topicIndex = buildIndex(new TopicIndex(), topic);
		return topicIndex;
	}

	@Override
	public boolean save(TopicIndex entity) {
		topicIndexRepository.save(entity);
		return true;
	}

	@Override
	protected TopicIndex doFindOne(Long aLong) {
		return topicIndexRepository.findOne(aLong);
	}

	@Override
	protected List<TopicIndex> getEntityFromRpc(List<Long> ids) {
		return buildIndex(QmtSbcTopicInternalApis.getTopicsByIds(ids));
	}
}
