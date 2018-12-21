package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;
import com.lesports.qmt.sbd.api.common.GroundType;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class TeamTypeFormatter implements Function<String, GroundType> {
    @Nullable
    @Override
    public GroundType apply(String input) {
        if (input.equals("1")) {
            return GroundType.HOME;

        } else if (input.contains("2")) {
            return GroundType.AWAY;

        }
        return null;
    }
}

