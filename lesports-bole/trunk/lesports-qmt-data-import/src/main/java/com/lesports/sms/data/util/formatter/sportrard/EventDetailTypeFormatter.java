package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class EventDetailTypeFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        Long cardType = 0L;
        if (input.equals("penalty")) {
            cardType = 100156000L;

        } else if (input.equals("heading")) {
            cardType = 104660000L;

        } else if (input.equals("owngoal")) {
            cardType = 104658000L;
        }
        return cardType;
    }
}

