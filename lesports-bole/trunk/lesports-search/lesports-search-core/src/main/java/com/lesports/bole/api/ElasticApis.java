package com.lesports.bole.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.bole.function.SearchIndexTransformer;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.utils.ElasticIndexResult;
import com.lesports.bole.utils.ElasticSearchResult;
import com.lesports.bole.utils.PageResult;
import com.lesports.utils.LeProperties;
import com.lesports.utils.http.RestTemplateFactory;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
public class ElasticApis {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticApis.class);

    private final static RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();
    private final static String ELASTIC_HOST = LeProperties.getString("search.elastic.host", "http://10.204.29.240:9200");
    private final static String SMS_INDEX_URL = ELASTIC_HOST + "/{index}/{type}/{id}";
    private final static String SMS_SEARCH_URL = ELASTIC_HOST + "/{index}/{type}/_search/?from={from}&size={size}&_source={_source}";

    /**
     * search in elastic. return the list of ids.
     *
     * @param index the name of index in elastic https://www.elastic.co/guide/en/elasticsearch/reference/1.4/docs-index_.html#index-creation
     * @param type  the name of type in elastic
     * @param param https://www.elastic.co/guide/en/elasticsearch/reference/1.4/query-dsl.html
     * @return
     */
    public static PageResult<SearchIndex> search(String index, String type, Map<String, Object> param, int page, int count, boolean source) {
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(type) || MapUtils.isEmpty(param)) {
            return new PageResult<>();
        }
        Map<String, Object> uriVariables = Maps.newHashMap();
        uriVariables.put("index", index);
        uriVariables.put("type", type);
        uriVariables.put("from", getFrom(page, count));
        uriVariables.put("size", count);
        uriVariables.put("_source", source);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(param);
        ResponseEntity<ElasticSearchResult> result = TEMPLATE.exchange(SMS_SEARCH_URL, HttpMethod.POST, entity, ElasticSearchResult.class, uriVariables);
        if (null == result) {
            return new PageResult<>();
        }
        ElasticSearchResult elasticSearchResult = result.getBody();
        if (elasticSearchResult.getTimeOut() == Boolean.TRUE) {
            return new PageResult<>();
        }
        if (null == elasticSearchResult.getHits() || CollectionUtils.isEmpty(elasticSearchResult.getHits().getHits())) {
            return new PageResult<>();
        }
        List<SearchIndex> res = Lists.newArrayList();
        for (ElasticSearchResult.HitItem hitItem : elasticSearchResult.getHits().getHits()) {
            SearchIndex searchIndex = SearchIndexTransformer.getEmptyEntity(index, type);
            if (null == searchIndex) {
                continue;
            }
            boolean re = searchIndex.newInstance(hitItem);
            if (re) {
                res.add(searchIndex);
            }
        }
        PageResult pageResult = new PageResult();
        pageResult.setRows(res);
        pageResult.setTotal(LeNumberUtils.toLong(elasticSearchResult.getHits().getTotal()));
        return pageResult;
    }

    /**
     * make index for object.
     *
     * @param index  the index where to make.
     * @param type   the type where to make.
     * @param object the object to make.
     * @return
     */
    public static boolean index(String index, String type, SearchIndex object) {
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(type) || null == object || null == object.getId()) {
            return false;
        }
        Map<String, Object> uriVariables = Maps.newHashMap();
        uriVariables.put("index", index);
        uriVariables.put("type", type);
        uriVariables.put("id", object.getId());
        HttpEntity<SearchIndex> entity = new HttpEntity<>(object);
        ResponseEntity<ElasticIndexResult> result = TEMPLATE.exchange(SMS_INDEX_URL, HttpMethod.PUT, entity, ElasticIndexResult.class, uriVariables);
        if (null == result) {
            return false;
        }
        ElasticIndexResult elasticIndexResult = result.getBody();
        if (null == elasticIndexResult) {
            return false;
        }
        LOG.info("index success for : {}", JSONObject.toJSONString(object));
        return true;
    }

    private static int getFrom(int page, int count) {
        if (page > 0) {
            return (page - 1) * count;
        }
        return 0;
    }

}
