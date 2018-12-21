package com.lesports.msg.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.lesports.id.client.LeIdApis;
import com.lesports.msg.cache.CdnCacheApis;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.MatchService;
import com.lesports.msg.util.IndexResult;
import com.lesports.msg.util.LeExecutors;
import com.lesports.sms.model.Match;
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

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

import static com.lesports.msg.Constants.SIS_WEB_HOST;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Service
public class MatchServiceImpl extends AbstractService implements MatchService {
    private static final Logger LOG = LoggerFactory.getLogger(MatchServiceImpl.class);
    private final static List<MessageFormat> MATCH_PAGE_URIS = Lists.newArrayList(new MessageFormat("http://sports.letv.com/match/{0}.html"), new MessageFormat("http://www.lesports.com/match/{0}.html"),
            new MessageFormat("http://sports.le.com/match/{0}.html"), new MessageFormat("http://m.lesports.com/match/{0}.html"),
            new MessageFormat("http://m.sports.letv.com/match/{0}.html"), new MessageFormat("http://m.sports.le.com/match/{0}.html"));

    private static final String SIS_WEB_MATCH_URI = LeProperties.getString("sis.web.match.uri", "/sis-web/app/match/get?id={0}&version=1.0");
    private static final String INDEX_SMS_MATCH_URL = LeProperties.getString("search.index.host", "http://10.154.157.44:9380") + "/search/v1/i/sms/matches/{0}?caller=1007";
    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();
    private static final List<Utf8KeyCreater<Long>> KEY_CREATE_LIST = Lists.newArrayList(new Utf8KeyCreater<Long>("V2_MATCH_SQUADS_"), new Utf8KeyCreater<Long>("V2_MATCH_COMPETITOR_STATS_"));


    @Override
    public boolean deleteMatchApiCache(long matchId) {
        return deleteNgxApiCache(matchId);
    }

    @Override
    public boolean deleteSmsRealtimeWebMatch(long id) {
        return deleteSmsRealtimeWebCache(KEY_CREATE_LIST, id);
    }

    @Override
    public boolean deleteSisMatch(long matchId) {
        long oldId = LeIdApis.getOldId(matchId);
        return deleteMemCacheAndCdnCache(SIS_WEB_HOST, SIS_WEB_MATCH_URI, oldId, true);
    }

    @Override
    public boolean indexMatch(Match match) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            final HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            Predicate<Long> function = new Predicate<Long>() {
                @Override
                public boolean apply(@Nullable Long aLong) {
                    ResponseEntity<IndexResult> result = TEMPLATE.exchange(INDEX_SMS_MATCH_URL, HttpMethod.PUT, httpEntity, IndexResult.class, aLong);
                    LOG.info("indexing match {}, result : {}.", aLong, JSONObject.toJSONString(result));
                    return isIndexSuccess(result);
                }
            };
            return LeExecutors.executeWithRetry(3, 1000, function, match.getId(), "indexing match");
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteMatchPage(long matchId) {
        boolean result = true;
        for (MessageFormat messageFormat : MATCH_PAGE_URIS) {
            String url = messageFormat.format(new Object[]{String.valueOf(matchId)});
            result = CdnCacheApis.delete(url) & result;
        }
        return deleteNgxPageCache(matchId) & result;
    }
}
