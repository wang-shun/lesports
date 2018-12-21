package com.lesports.sms.data.util.formatter.soda;

import com.google.common.base.Function;
import com.lesports.qmt.sbd.SbdLiveEventInternalApis;
import com.lesports.qmt.sbd.model.LiveEvent;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SodaEventTypeFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        LiveEvent validEvent = SbdLiveEventInternalApis.getLiveEventByNameAndParentId(input, 0L);
        if (validEvent == null) return validEvent.getId();
        return 0L;
    }
}

