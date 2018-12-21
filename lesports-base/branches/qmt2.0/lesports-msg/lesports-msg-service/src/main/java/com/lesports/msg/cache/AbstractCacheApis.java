package com.lesports.msg.cache;

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
 * @since 2016/7/25
 */
public class AbstractCacheApis {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCacheApis.class);
    private static final RestTemplate restTemplate = RestTemplateFactory.getTemplate(10000);

    protected static boolean delete(String area, String cacheType, String key, Long id, boolean DELETE_CACHE, String CLEAN_CACHE_URL) {
        if (!DELETE_CACHE) {
            return false;
        }
        boolean deleteSuccess = false;
        int retry = 0;
        while (retry < 2) {
            try {
                String url = MessageFormat.format(CLEAN_CACHE_URL, cacheType, key);
                ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
                if (null != result && result.getStatusCode() == HttpStatus.OK) {
                    deleteSuccess = true;
                }
                if (null != result && result.getStatusCode() == HttpStatus.NOT_FOUND) {
                    deleteSuccess = true;
                }
                LOG.info("delete key : {} in {} {} cache for id : {}, result : {}. response : {}, retry : {}", key, area, cacheType, id, deleteSuccess, result.getBody(), retry);
            } catch (HttpClientErrorException e) {
                deleteSuccess = true;
                LOG.info("delete key : {} in {} {} cache for id : {}, not found, retry : {}", key, area, cacheType, id, retry);
            } catch (Exception e) {
                LOG.warn("fail to delete key : {} in {} {} cache for id : {}, result : {}, retry : {}. {}", key, area, cacheType, id, deleteSuccess, retry, e.getMessage(), e);
            }
            if (deleteSuccess) {
                return true;
            }
            retry++;
        }
        return false;
    }

    protected static boolean deleteNgxB(String area, String key, Long id, boolean DELETE_CACHE, String CLEAN_CACHE_URL) {
        return delete(area, "sports-ngx", key, id, DELETE_CACHE, CLEAN_CACHE_URL);
    }

    protected static boolean deleteWebB(String area, String key, Long id, boolean DELETE_CACHE, String CLEAN_CACHE_URL) {
        return delete(area, "sports-web", key, id, DELETE_CACHE, CLEAN_CACHE_URL);
    }
}
