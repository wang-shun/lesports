package com.lesports.msg.cache;

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
public class UsCacheApis extends AbstractCacheApis {

    private static final String US_CLEAN_CACHE_URL = LeProperties.getString("us.clean.cache.url",
            "http://us.internal.api.lesports.com/cache/memcached/{0}/delete?key={1}");
    private static final boolean DELETE_CACHE = LeProperties.getBoolean("us.clean.cache.enable", false);

    public static boolean deleteNgx(String key, Long id) {
        return deleteNgxB(CountryCode.US.name(), key, id, DELETE_CACHE, US_CLEAN_CACHE_URL);
    }

    public static boolean deleteWeb(String key, Long id) {
        return deleteWebB(CountryCode.US.name(), key, id, DELETE_CACHE, US_CLEAN_CACHE_URL);
    }

}
