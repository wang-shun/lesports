package com.lesports.msg.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.msg.cache.*;
import com.lesports.msg.util.IndexResult;
import com.lesports.msg.util.WebResult;
import com.lesports.utils.MD5Util;
import com.lesports.utils.Utf8KeyCreater;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lesports.msg.Constants.DELETE_CDN_CACHE_ENABLE;
import static com.lesports.msg.Constants.DELETE_MEM_CACHE_ENABLE;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
public abstract class AbstractService {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);

    private static final String IDS_CACHE_KEY_PREFIX = "id_";
    private static final String IDS_CACHE_KEY_PREFIX_V2 = "V2_id_";

    private static final Set<Long> CALL_ID_SET = Sets.newHashSet(1001L, 1002L, 1003L, 1004L, 1008L, 1009L, 1011L, 1014L, 1016L, 1018L, 1051L, 1054L);
    private static final Set<Long> HK_CALL_ID_SET = Sets.newHashSet(3001L, 3002L, 3003L, 3004L, 3006L, 3008L, 3010L, 3011L, 3013L, 3014L, 3017L, 3020L);
    private static final Set<Long> US_CALL_ID_SET = Sets.newHashSet(4001L, 4002L, 4003L, 4004L, 4006L, 4008L, 4010L, 4011L, 4013L, 4014L, 4017L, 4020L);
    private static final Set<CountryCode> COUNTRY_CODE_SET = Sets.newHashSet(CountryCode.CN);
    private static final Set<CountryCode> HK_COUNTRY_CODE_SET = Sets.newHashSet(CountryCode.HK);
    private static final Set<CountryCode> US_COUNTRY_CODE_SET = Sets.newHashSet(CountryCode.US);
    private static final Set<LanguageCode> LANGUAGE_CODE_SET = Sets.newHashSet(LanguageCode.ZH_CN);
    private static final Set<LanguageCode> HK_LANGUAGE_CODE_SET = Sets.newHashSet(LanguageCode.EN_US, LanguageCode.ZH_HK);
    private static final Set<LanguageCode> US_LANGUAGE_CODE_SET = Sets.newHashSet(LanguageCode.EN_US);

    @Resource
    private BjRedisCache redisCache;
    @Resource
    private HkRedisCache hkRedisCache;
    @Resource
    private NgxApiMemCache ngxApiMemCache;
    @Resource
    private NgxApiMemCacheTDXY ngxApiMemCacheTDXY;
    @Resource
    private SmsMemCache smsMemCache;

    /**
     * 删除id的api缓存key,并清空id下面所有的key
     *
     * @param id
     * @return
     */
    public boolean deleteNgxApiCache(long id) {
        return deleteNgxCache(IDS_CACHE_KEY_PREFIX + id, id) & deleteHkNgxCache(IDS_CACHE_KEY_PREFIX + id, id)
                & deleteNgxCache(IDS_CACHE_KEY_PREFIX_V2 + id, id) & deleteHkNgxCache(IDS_CACHE_KEY_PREFIX_V2 + id, id);
    }

    /**
     * 删除id的页面缓存key,并清空id下面所有的key
     *
     * @param id
     * @return
     */
    public boolean deleteNgxPageCache(long id) {
        return deleteNgxCache(IDS_CACHE_KEY_PREFIX + id + "_page", id) & deleteHkNgxCache(IDS_CACHE_KEY_PREFIX + id + "_page", id)
                & deleteNgxCache(IDS_CACHE_KEY_PREFIX_V2 + id + "_page", id) & deleteHkNgxCache(IDS_CACHE_KEY_PREFIX_V2 + id + "_page", id);
    }

    /**
     * 清除realtime服务缓存
     *
     * @param KEY_CREATE_LIST
     * @param id
     * @return
     */
    protected boolean deleteSmsRealtimeWebCache(List<Utf8KeyCreater<Long>> KEY_CREATE_LIST, long id) {
        if (CollectionUtils.isEmpty(KEY_CREATE_LIST)) {
            return true;
        }
        List<String> keys;
        List<String> hkKeys;
        List<String> usKeys;
        for (Utf8KeyCreater<Long> keyCreater : KEY_CREATE_LIST) {
            String partKey = keyCreater.textKey(id);
            keys = appendLanguage(appendCountry(appendCaller(partKey, CALL_ID_SET), COUNTRY_CODE_SET), LANGUAGE_CODE_SET);
            hkKeys = appendLanguage(appendCountry(appendCaller(partKey, HK_CALL_ID_SET), HK_COUNTRY_CODE_SET), HK_LANGUAGE_CODE_SET);
            usKeys = appendLanguage(appendCountry(appendCaller(partKey, US_CALL_ID_SET), US_COUNTRY_CODE_SET), US_LANGUAGE_CODE_SET);
            if (CollectionUtils.isNotEmpty(keys)) {
                for (String key : keys) {
                    smsMemCache.delete(key, id);
                }
            }
            if (CollectionUtils.isNotEmpty(hkKeys)) {
                for (String key : hkKeys) {
                    HkCacheApis.deleteWeb(key, id);
                }
            }
            if (CollectionUtils.isNotEmpty(usKeys)) {
                for (String key : usKeys) {
                    UsCacheApis.deleteWeb(key, id);
                }
            }
        }


        return true;
    }

    private List<String> appendCaller(String partKey, Set<Long> callerSet) {
        List<String> keys = Lists.newArrayListWithCapacity(callerSet.size());
        for (Long caller : callerSet) {
            keys.add(partKey + "_" + caller);
        }
        return keys;
    }

    private List<String> appendCountry(List<String> keys, Set<CountryCode> countryCodeSet) {
        List<String> result = Lists.newArrayList();
        for (CountryCode countryCode : countryCodeSet) {
            for (String partKey : keys) {
                result.add(partKey + "_" + countryCode);
            }
        }
        return result;
    }

    private List<String> appendLanguage(List<String> keys, Set<LanguageCode> languageCodeSet) {
        List<String> result = Lists.newArrayList();
        for (LanguageCode languageCode : languageCodeSet) {
            for (String partKey : keys) {
                result.add(partKey + "_" + languageCode);
            }
        }
        return result;
    }

    public boolean deleteNgxCache(String idKey, long id) {
        try {
            Set<String> keys = redisCache.smembers(idKey);
            if (CollectionUtils.isEmpty(keys)) {
                return true;
            }
            String[] keyArray = new String[keys.size()];
            int i = 0;
            for (String key : keys) {
                keyArray[i] = key;
                i++;
            }
            redisCache.srem(idKey, keyArray);
            for (String key : keys) {
                if (DELETE_MEM_CACHE_ENABLE) {
                    ngxApiMemCache.delete(key, id);
                    ngxApiMemCacheTDXY.delete(key, id);
                }
            }
        } catch (Exception e) {
            LOG.error("fail delete nginx cache for : {}, {}", idKey, e.getMessage(), e);
        }

        return true;
    }

    public boolean deleteHkNgxCache(String idKey, long id) {
        try {
            Set<String> keys = hkRedisCache.smembers(idKey);
            if (CollectionUtils.isEmpty(keys)) {
                return true;
            }
            String[] keyArray = new String[keys.size()];
            int i = 0;
            for (String key : keys) {
                keyArray[i] = key;
                i++;
            }
            hkRedisCache.srem(idKey, keyArray);
            for (String key : keys) {
                if (DELETE_MEM_CACHE_ENABLE) {
                    HkCacheApis.deleteNgx(key, id);
                }
            }
        } catch (Exception e) {
            LOG.error("fail delete hk nginx cache for : {}, {}", idKey, e.getMessage(), e);
        }

        return true;
    }

    protected boolean deleteMemCacheAndCdnCache(String host, String webKeyPattern, long id, boolean md5) {
        String key = MessageFormat.format(webKeyPattern, String.valueOf(id));

        String keyWithHost = host + MessageFormat.format(webKeyPattern, String.valueOf(id));
        boolean result = true;
        if (DELETE_MEM_CACHE_ENABLE) {
            if (md5) {
                result = ngxApiMemCache.deleteAfterMD5(key) & ngxApiMemCacheTDXY.deleteAfterMD5(key) & HkCacheApis.deleteNgx(MD5Util.md5(key), id);
            } else {
                result = ngxApiMemCache.delete(key, id) & ngxApiMemCacheTDXY.delete(key, id) & HkCacheApis.deleteNgx(key, id);
            }
        }

        result = result && CdnCacheApis.delete(keyWithHost);

        return result;
    }

    public boolean isIndexSuccess(ResponseEntity<IndexResult> result) {
        if (null == result) {
            return false;
        }
        if (null == result.getBody()) {
            return false;
        }
        IndexResult ir = result.getBody();
        if (null == ir.getCode() || 200 != ir.getCode()) {
            return false;
        }
        if (null == ir.getData()) {
            return false;
        }
        if (!ir.getData().getSuccess()) {
            return false;
        }
        return true;
    }
    public boolean isWebResultSuccess(ResponseEntity<WebResult> result) {
        if (null == result) {
            return false;
        }
        if (null == result.getBody()) {
            return false;
        }
        WebResult ir = result.getBody();
        if (StringUtils.isEmpty(ir.getCode()) || !"A00000".equals(ir.getCode())) {
            return false;
        }
        return true;
    }
}
