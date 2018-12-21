package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;
import com.lesports.qmt.sbd.SbdLiveEventInternalApis;
import com.lesports.qmt.sbd.model.LiveEvent;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class CardTypeFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        Long cardType = 0L;
        if (input.equals("Yellow")) {
            LiveEvent validEvent = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("黄牌", 0L);
            cardType = validEvent == null ? 0L : validEvent.getId();
        }

        if (input.contains("Red")) {
            LiveEvent validEvent = SbdLiveEventInternalApis.getLiveEventByNameAndParentId("红牌", 0L);
            cardType = validEvent == null ? 0L : validEvent.getId();
        }
        return cardType;
    }
}

