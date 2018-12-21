package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SoccerSectionNumFormatter implements Function<String, Integer> {
    @Nullable
    @Override
    public Integer apply(String input) {
        Integer periodNum = 0;
        if (input.equals("Period1")) {
            periodNum = 1;
        }
        if (input.equals("Normaltime")) {
            periodNum = 2;
        }
        //加时赛结束
        if (input.equals("Overtime")) {
            periodNum = 3;

        }
        //点球大战结束
        if (input.equals("Penalties")) {
            periodNum = 4;
        }
        return periodNum;
    }
}



