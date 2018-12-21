package com.lesports.msg.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.NewsService;
import com.lesports.msg.util.IndexResult;
import com.lesports.msg.util.LeExecutors;
import com.lesports.msg.util.WebResult;
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

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
@Service("newsService")
public class NewsServiceImpl extends AbstractService implements NewsService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);
    private static final String INDEX_SMS_NEWS_URL = LeProperties.getString("search.index.host", "http://10.154.157.44:9380") + "/search/v1/i/sms/news/{0}?caller=1007";
    private static final String ADD_CIBN_RECOMMEND_TV_URL = LeProperties.getString("add.cibn.recommend.tv.url", "http://sms.sports.cp21.ott.cibntv.net") + "/recommendTV/addNewsToCibn?newIds={0}";

    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();

    @Override
    public boolean indexNews(long id) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            final HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            Predicate<Long> function = new Predicate<Long>() {
                @Override
                public boolean apply(@Nullable Long aLong) {
                    ResponseEntity<IndexResult> result = TEMPLATE.exchange(INDEX_SMS_NEWS_URL, HttpMethod.PUT, httpEntity, IndexResult.class, aLong);
                    LOG.info("indexing news {}, result : {}.", aLong, JSONObject.toJSONString(result));
                    return isIndexSuccess(result);
                }
            };
            return LeExecutors.executeWithRetry(3, 1000, function, id, "indexing news");
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }
    @Override
    public boolean addNewsToCibnRecommendTv(long id) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            final HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            Predicate<Long> function = new Predicate<Long>() {
                @Override
                public boolean apply(@Nullable Long aLong) {
                    ResponseEntity<WebResult> result = TEMPLATE.exchange(ADD_CIBN_RECOMMEND_TV_URL, HttpMethod.PUT, httpEntity, WebResult.class, aLong);
                    LOG.info("add news to cibn recommend tv {}, result : {}.", aLong, JSONObject.toJSONString(result));
                    return isWebResultSuccess(result);
                }
            };
            return LeExecutors.executeWithRetry(3, 1000, function, id, "add news to cibn recommend tv");
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }


    @Override
    public boolean deleteNewsApiCache(long id) {
        return deleteNgxApiCache(id);
    }
}
