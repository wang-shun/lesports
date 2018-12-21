/**
 * 
 */
package com.lesports.search.qmt.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.IdsSearchParam;
import com.lesports.search.qmt.param.TypeSearchParam;
import com.lesports.search.qmt.service.FlexibleSearchService;
import com.lesports.search.qmt.utils.PageResult;

/**
 * @author sunyue7
 *
 */
@Service
public class FlexibleSearchServiceImpl implements FlexibleSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(FlexibleSearchService.class);

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lesports.bole.service.FlexibleSearchService#findByIds(java.util.List)
	 */
	@Override
	public PageResult<Indexable> findByFields(final IdsSearchParam param) {
		final ObjectMapper om = createObjectMapper();
		PageResult<Indexable> result = elasticsearchTemplate.query(param.createNativeSearchQueryBuilder().build(),
				new ResultsExtractor<PageResult<Indexable>>() {

					@Override
					public PageResult<Indexable> extract(SearchResponse response) {
						HashMap<Long, Indexable> indexMap = new HashMap<Long, Indexable>();
						SearchHit[] hits = response.getHits().getHits();
						for (int i = 0; i < hits.length; i++) {
							SearchHit hit = hits[i];
							IndexType indexType = MetaData.IndexType.valueOf(hit.getType().toUpperCase());
							if (indexType != null && indexType.getIndexClass() != null) {
								try {
									Indexable index = om.readValue(hit.getSourceAsString(), indexType.getIndexClass());
									indexMap.put(index.getId(), index);
								} catch (JsonParseException e) {
									LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(),
											e);
								} catch (JsonMappingException e) {
									LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(),
											e);
								} catch (IOException e) {
									LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(),
											e);
								}
							}
						}
						if (param.hasOnlyIdsParameter()) {
							ArrayList<Indexable> indexList = new ArrayList<Indexable>(indexMap.size());
							for (Long id : param.getIdList()) {
								Indexable index = indexMap.get(id);
								if (index != null) {
									indexList.add(index);
								}
							}
							return new PageResult<Indexable>(indexList, response.getHits().getTotalHits());
						}
						return new PageResult<Indexable>(new ArrayList<Indexable>(indexMap.values()),
								response.getHits().getTotalHits());
					}
				});
		return result;
	}

	@Override
	public PageResult<Indexable> findByTypes(TypeSearchParam param) {
		final ObjectMapper om = createObjectMapper();
		PageResult<Indexable> result = elasticsearchTemplate.query(param.createNativeSearchQueryBuilder().build(),
				new ResultsExtractor<PageResult<Indexable>>() {

					@Override
					public PageResult<Indexable> extract(SearchResponse response) {
						List<Indexable> list = new ArrayList<Indexable>();
						SearchHit[] hits = response.getHits().getHits();
						for (int i = 0; i < hits.length; i++) {
							SearchHit hit = hits[i];
							IndexType indexType = MetaData.IndexType.valueOf(hit.getType());
							if (indexType != null && indexType.getIndexClass() != null) {
								try {
									Indexable index = om.readValue(hit.getSourceAsString(), indexType.getIndexClass());
									list.add(index);
								} catch (JsonParseException e) {
									LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(),
											e);
								} catch (JsonMappingException e) {
									LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(),
											e);
								} catch (IOException e) {
									LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(),
											e);
								}
							}
						}
						return new PageResult<Indexable>(list, response.getHits().getTotalHits());
					}
				});
		return result;
	}

	public static ObjectMapper createObjectMapper() {
		return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

}
