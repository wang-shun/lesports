package com.lesports.bole.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.bole.model.BoleLive;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * remove duplicate live url.
 *
 * @author pangchuanxiao
 * @since 2016/1/5
 */
@Component
public class BoleLiveDuplicateFilter {
    /**
     *
     * @param tbLives
     * @return
     */
    public List<BoleLive> filter(List<BoleLive> tbLives) {
        if (CollectionUtils.isEmpty(tbLives)) {
            return Collections.emptyList();
        }
        Map<String, BoleLive> liveMap = Maps.newHashMap();
        for (BoleLive boleLive : tbLives) {
            liveMap.put(StringUtils.substringAfter(boleLive.getUrl().replace("http://", ""), "/"), boleLive);
        }

        return Lists.newArrayList(liveMap.values());
    }
}
