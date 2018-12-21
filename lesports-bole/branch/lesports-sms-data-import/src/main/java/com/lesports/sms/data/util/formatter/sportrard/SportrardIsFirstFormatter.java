package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SportrardIsFirstFormatter implements Function<String, Boolean> {
    @Nullable
    @Override
    public Boolean apply(String input) {
      if(input.equals("0")){
         return true;
        }
        return false;
    }
}

