package com.lesports.msg.cache;

import com.alibaba.fastjson.JSONObject;
import com.lesports.api.common.CountryCode;
import com.lesports.utils.LeProperties;
import com.lesports.utils.http.RestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/12
 */
public class HkCacheApis extends AbstractCacheApis {

    private static final String HK_CLEAN_CACHE_URL = LeProperties.getString("hk.clean.cache.url",
            "http://hk.internal.api.lesports.com/cache/memcached/{0}/delete?key={1}");
    private static final boolean DELETE_CACHE = LeProperties.getBoolean("hk.clean.cache.enable", true);

    public static boolean deleteNgx(String key, Long id) {
        return deleteNgxB(CountryCode.HK.name(), key, id, DELETE_CACHE, HK_CLEAN_CACHE_URL);
    }

    public static boolean deleteWeb(String key, Long id) {
        return deleteWebB(CountryCode.HK.name(), key, id, DELETE_CACHE, HK_CLEAN_CACHE_URL);
    }

}
