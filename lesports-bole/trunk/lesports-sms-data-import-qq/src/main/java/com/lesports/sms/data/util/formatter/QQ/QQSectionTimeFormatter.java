package com.lesports.sms.data.util.formatter.QQ;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.formatter.ElementFormatter;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/30.
 */
public class QQSectionTimeFormatter implements Function<String, Double> {
    @Nullable
    @Override
    public Double apply(String input) {
        if (StringUtils.isBlankOrNull(input)) return 0.0;
        String[] sectionTimesList = input.split("\\ ");
        if (sectionTimesList == null || sectionTimesList.length < 2) return 0.0;
        String sectionName = sectionTimesList[1];
        String[] timeList = sectionName.split("\\:");
        if (timeList == null || timeList.length <= 0) return 0.0;
        Integer minutes = CommonUtil.parseInt(timeList[0], 0);
        Integer sections = CommonUtil.parseInt(timeList[1], 0);
        Integer totalSections = minutes * 60 + sections;
        return CommonUtil.parseDouble(totalSections.toString(), 0.0);
    }
}