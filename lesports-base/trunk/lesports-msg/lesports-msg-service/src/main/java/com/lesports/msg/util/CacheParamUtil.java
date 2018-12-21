package com.lesports.msg.util;

import com.lesports.LeConstants;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by lufei1 on 2015/10/16.
 */
public class CacheParamUtil {

    public static String buildCacheKey(Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return null;
        }
        String paramKeys = paramMap.get(LeConstants.MSG_CACHE_PARAM_KEY);
        if (StringUtils.isBlank(paramKeys)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] paramKeyArr = paramKeys.split(",");
        for (String paramKey : paramKeyArr) {
            sb.append(paramKey).append("=").append(paramMap.get(paramKey)).append("&");
        }
        return sb.toString();
    }
}
