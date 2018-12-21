package com.lesports.sms.data.utils;

import com.google.common.base.Function;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.sportrard.SportrardConstants;
import com.lesports.sms.model.DictEntry;
import com.lesports.utils.LeDateUtils;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class GameFTypeFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        String sportsType = SportrardConstants.sportsTypeMap.get(input);
        List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
        Long gameFType = dictEntryList.get(0).getId();
        return gameFType;
    }

}

