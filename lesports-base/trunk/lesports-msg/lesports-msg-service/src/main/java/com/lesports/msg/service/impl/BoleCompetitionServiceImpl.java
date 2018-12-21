package com.lesports.msg.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.BoleCompetitionService;
import com.lesports.msg.util.IndexResult;
import com.lesports.utils.LeProperties;
import com.lesports.utils.http.RestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
@Service
public class BoleCompetitionServiceImpl extends AbstractService implements BoleCompetitionService {
    private static final Logger LOG = LoggerFactory.getLogger(BoleCompetitionServiceImpl.class);
    private static final String INDEX_BOLE_COMPETITION_URL = LeProperties.getString("search.index.host", "http://10.154.157.44:9380") + "/search/v1/i/bole/competitions/{0}?caller=1007";
    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();

    @Override
    public boolean indexBoleCompetition(long id) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            ResponseEntity<IndexResult> result = TEMPLATE.exchange(INDEX_BOLE_COMPETITION_URL, HttpMethod.PUT, httpEntity, IndexResult.class, id);
            LOG.info("indexing bole competition {}, result {}", id, JSONObject.toJSONString(result.getBody()));
            if (!isIndexSuccess(result)) {
                result = TEMPLATE.exchange(INDEX_BOLE_COMPETITION_URL, HttpMethod.PUT, httpEntity, IndexResult.class, id);
                LOG.info("reIndexing bole competition {}, result {}", id, JSONObject.toJSONString(result.getBody()));
            }
            return true;
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }


}
