/**
 * 
 */
package com.lesports.bole.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lesports.api.common.CountryCode;
import com.lesports.bole.Constants;
import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.repository.CommonSearchRepository;
import com.lesports.bole.service.FlexibleSearchService;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.api.common.OnlineStatus;
import com.lesports.sms.api.common.Platform;

/**
 * @author sunyue7
 *
 */
@Service
public class FlexibleSearchServiceImpl implements FlexibleSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(FlexibleSearchService.class);

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	@Resource
	private CommonSearchRepository commonSearchRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lesports.bole.service.FlexibleSearchService#findRelated(java.lang.
	 * String, java.lang.String[], com.lesports.sms.api.common.Platform,
	 * java.lang.String[], java.lang.Integer[], java.lang.String,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Map<String, PageResult<SearchIndex<Long>>> findRelated(String focusIds, String[] typeArray,
			Platform platform, String[] startTimeRange, Integer[] episodeStatus, String episodeSort, Integer page,
			Integer count) {
		Client esClient = elasticsearchTemplate.getClient();

		// search focus entity
		String[] idArray = focusIds.split(",");
		HashMap<String, PageResult<SearchIndex<Long>>> result = new HashMap<String, PageResult<SearchIndex<Long>>>();
		MultiSearchRequestBuilder searchBuilder = esClient.prepareMultiSearch();
		for (String type : typeArray) {
			result.put(type, new PageResult<SearchIndex<Long>>(new ArrayList<SearchIndex<Long>>(), 0L));
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.must(new TermQueryBuilder("allowCountry", CountryCode.CN.getValue()));
			boolQueryBuilder.must(new MatchQueryBuilder("deleted", false));
			SearchRequestBuilder subSearchBuilder = esClient.prepareSearch("sms_hongkong").setTypes(type)
					.setFrom(page * count).setSize(count);
			if (type.equalsIgnoreCase("news")) {
				boolQueryBuilder.must(new TermsQueryBuilder("tids", idArray));
				boolQueryBuilder.must(new TermQueryBuilder("onlineStatus", OnlineStatus.ONLINE.getValue()));
				if (platform != null) {
					boolQueryBuilder.must(new MatchQueryBuilder("platforms", platform.getValue()));
				}
				subSearchBuilder.addSort("publishAt", SortOrder.DESC);
			} else if (type.equalsIgnoreCase("episodes")) {
				boolQueryBuilder.must(new TermsQueryBuilder("competitorIds", idArray));
				boolQueryBuilder.must(new TermQueryBuilder("online", Boolean.TRUE));
				if (platform != null) {
					boolQueryBuilder.must(new MatchQueryBuilder("livePlatforms", platform.getValue()));
				}
				subSearchBuilder.addSort("startTime",
						"desc".equalsIgnoreCase(episodeSort) ? SortOrder.DESC : SortOrder.ASC);
				subSearchBuilder.addSort("status", SortOrder.DESC);
				if (startTimeRange != null) {
					if (StringUtils.isNotEmpty(startTimeRange[0])) {
						boolQueryBuilder.must(new RangeQueryBuilder("startTime").gte(startTimeRange[0]));
					}
					if (StringUtils.isNotEmpty(startTimeRange[1])) {
						boolQueryBuilder.must(new RangeQueryBuilder("startTime").lte(startTimeRange[1]));
					}
				}
				if (episodeStatus != null) {
					BoolQueryBuilder statusQueryBuilder = new BoolQueryBuilder();
					for (Integer status : episodeStatus) {
						statusQueryBuilder.should(new TermQueryBuilder("status", status));
					}
					boolQueryBuilder.must(statusQueryBuilder);
				}
			}
			searchBuilder.add(subSearchBuilder.setQuery(boolQueryBuilder));
		}

		MultiSearchResponse multiSearchResponse = searchBuilder.execute().actionGet();

		// parse response
		ObjectMapper om = new ObjectMapper();
		for (MultiSearchResponse.Item item : multiSearchResponse.getResponses()) {
			SearchResponse response = item.getResponse();
			if (response == null) {
				continue;
			}
			for (SearchHit hit : response.getHits().hits()) {
				try {
					PageResult<SearchIndex<Long>> bucket = result.get(hit.getType());
					Class<? extends SearchIndex<Long>> indexClass = null;
					if (Constants.Type.news.equals(hit.getType())) {
						indexClass = NewsIndex.class;
					}
					if (Constants.Type.episodes.equals(hit.getType())) {
						indexClass = EpisodeIndex.class;
					}
					bucket.getRows().add(om.readValue(hit.getSourceAsString(), indexClass));
					bucket.setTotal(response.getHits().getTotalHits());
				} catch (JsonParseException e) {
					LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(), e);
				} catch (JsonMappingException e) {
					LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(), e);
				} catch (IOException e) {
					LOG.error("Deserialize index faild: ID = {}, type = {}", hit.getId(), hit.getType(), e);
				}
			}
		}

		return result;
	}

}
