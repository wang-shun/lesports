package com.lesports.sms.data.utils;

import com.google.common.base.Function;
import com.lesports.sms.client.SbdsInternalApis;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/4/19.
 */
public class PositionFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String pos) {
        int postion = Integer.parseInt(pos);
        Long id = 0L;
//    if (1 == postion) {
//        id = SbdsInternalApis.getDictEntriesByName("^守门员$").get(0).getId();
//    }
//    if (1 < postion && postion <= pos1[0]) {
//        id = SbdsInternalApis.getDictEntriesByName("^后卫$").get(0).getId();
//    }
//    if (pos1[0] < postion && postion <= pos1[1]) {
//        id = SbdsInternalApis.getDictEntriesByName("^中场$").get(0).getId();
//    }
//    if (pos1[1] < postion && postion <= pos1[2]) {
//        id = SbdsInternalApis.getDictEntriesByName("^前锋$").get(0).getId();
//    }
        return id;
    }
}
