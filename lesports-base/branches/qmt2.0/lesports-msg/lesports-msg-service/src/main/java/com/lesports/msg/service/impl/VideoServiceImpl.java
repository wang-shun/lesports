package com.lesports.msg.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lesports.msg.cache.*;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.VideoService;
import com.lesports.msg.util.IndexResult;
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
import java.text.MessageFormat;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
@Service("videoService")
public class VideoServiceImpl extends AbstractService implements VideoService {
    private static final Logger LOG = LoggerFactory.getLogger(VideoServiceImpl.class);

    private static final Utf8KeyCreater<Long> VRS_VIDEO_CREATE = new Utf8KeyCreater<>("V2_VRS_VIDEO_");
    private static final Utf8KeyCreater<Long> OLD_VRS_VIDEO_CREATE = new Utf8KeyCreater<>("V2_VRS_VIDEO_");

    private final static MessageFormat format = new MessageFormat("http://sports.letv.com/video/{0}.html");

    private static final String INDEX_SMS_VIDEO_URL = LeProperties.getString("search.index.host", "http://10.154.157.44:9380") + "/search/v1/i/sms/videos/{0}?caller=1007";

    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();

    @Resource
    private NgxCmsMemCache ngxCmsMemCache;

    @Resource
    private NgxCmsMemCacheTDXY ngxCmsMemCacheTDXY;

    @Resource
    private SisWebMemCache sisWebMemCache;

    @Override
    public boolean deleteVideoInSis(long id) {
        return sisWebMemCache.delete(VRS_VIDEO_CREATE.textKey(id), id) & sisWebMemCache.delete(OLD_VRS_VIDEO_CREATE.textKey(id), id);
    }

    @Override
    public boolean deleteVideoApiCache(long id) {
        return deleteNgxApiCache(id);
    }

    @Override
    public boolean deletePageInMemCached(String url) {
        return ngxCmsMemCache.delete(url, -1) & HkCacheApis.deleteNgx(url, -1l) & ngxCmsMemCacheTDXY.delete(url, -1);
    }

    @Override
    public boolean deletePageInMemCached(long id) {
        return ngxCmsMemCache.delete(format.format(new Object[]{String.valueOf(id)}), id) & ngxCmsMemCacheTDXY.delete(format.format(new Object[]{String.valueOf(id)}), id) & HkCacheApis.deleteNgx(format.format(new Object[]{String.valueOf(id)}), id);
    }

    @Override
    public boolean deletePageInCdn(String url) {
        return CdnCacheApis.delete(url);
    }

    @Override
    public boolean indexVideo(long id) {
        try {
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("content-type", "application/x-www-form-urlencoded");
            HttpEntity httpEntity = new HttpEntity(param, httpHeaders);

            ResponseEntity<IndexResult> result = TEMPLATE.exchange(INDEX_SMS_VIDEO_URL, HttpMethod.PUT, httpEntity, IndexResult.class, id);
            LOG.info("indexing video {}, result {}", id, JSONObject.toJSONString(result.getBody()));
            if (!isIndexSuccess(result)) {
                result = TEMPLATE.exchange(INDEX_SMS_VIDEO_URL, HttpMethod.PUT, httpEntity, IndexResult.class, id);
                LOG.info("reIndexing video {}, result {}", id, JSONObject.toJSONString(result.getBody()));
            }
            return true;
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deletePageInCdn(long id) {
        return CdnCacheApis.delete(format.format(new Object[]{String.valueOf(id)}));
    }
}
