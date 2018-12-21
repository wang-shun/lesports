package com.lesports.msg.service.impl;

import com.google.common.collect.Lists;
import com.lesports.msg.cache.SmsMemCache;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.EpisodeService;
import com.lesports.sms.model.Episode;
import com.lesports.utils.LeProperties;
import com.lesports.utils.Utf8KeyCreater;
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

import javax.annotation.Resource;
import java.util.List;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Service
public class EpisodeServiceImpl extends AbstractService implements EpisodeService {
    private static final Logger LOG = LoggerFactory.getLogger(EpisodeServiceImpl.class);
    private static final String INDEX_SMS_EPISODE_URL = LeProperties.getString("search.index.host", "http://10.154.157.44:9380") + "/search/v1/i/sms/episodes/{0}?caller=1007";
    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();
    private static final List<Utf8KeyCreater<Long>> KEY_CREATER_LIST = Lists.newArrayList(new Utf8KeyCreater<Long>("V2_HALL_EPISODE_"),
            new Utf8KeyCreater<Long>("V2_HALL_EPISODE_APP_"),new Utf8KeyCreater<Long>("V2_POLLING_EPISODE_"),
            new Utf8KeyCreater<Long>("V2_POLLING_EPISODE_APP_"), new Utf8KeyCreater<Long>("V2_DETAIL_EPISODE_"),new Utf8KeyCreater<Long>("V2_DETAIL_EPISODE_APP_"));

    @Override
    public boolean indexEpisode(Episode episode) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            ResponseEntity<String> result = TEMPLATE.exchange(INDEX_SMS_EPISODE_URL, HttpMethod.PUT, httpEntity, String.class, episode.getId());
            LOG.info("indexing episode {}, result {}", episode.getId(), result.getBody());
            return true;
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteSmsRealtimeWebEpisodeCache(long id) {
        return deleteSmsRealtimeWebCache(KEY_CREATER_LIST, id);
    }

    @Override
    public boolean deleteEpisodeApiCache(long id) {

        boolean resultRedis = deleteNgxApiCache(id);
        if (!resultRedis) {
            LOG.error("fail to delete id cache key relation : {} in redis.", id);
        }
        return resultRedis;
    }

}
