package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;

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
            //TODO
            cardType = 0L;

        }
        if (input.contains("Red")) {
            cardType = 0L;

        }
        return cardType;
    }
}

