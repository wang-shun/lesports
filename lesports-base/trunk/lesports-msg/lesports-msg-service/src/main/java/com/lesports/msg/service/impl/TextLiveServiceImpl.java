package com.lesports.msg.service.impl;

import com.alibaba.fastjson.JSON;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.TextLiveService;
import com.lesports.msg.util.CacheParamUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by lufei1 on 2015/10/15.
 */
@Service("textLiveService")
public class TextLiveServiceImpl extends AbstractService implements TextLiveService {
    private static final Logger logger = LoggerFactory.getLogger(TextLiveServiceImpl.class);

    @Override
    public boolean deleteTextLiveApiCache(Map<String, String> paramMap) {
        String cacheKey = CacheParamUtil.buildCacheKey(paramMap);
        if (StringUtils.isBlank(cacheKey)) {
            logger.warn("cache key is null,paramMap:{}", JSON.toJSON(paramMap));
            return false;
        }
        return deleteNgxCache(cacheKey, -1) & deleteHkNgxCache(cacheKey, -1);
    }

    @Override
    public boolean deleteTextLiveApiCache(long textLiveId) {
        return deleteNgxApiCache(textLiveId);
    }

}
