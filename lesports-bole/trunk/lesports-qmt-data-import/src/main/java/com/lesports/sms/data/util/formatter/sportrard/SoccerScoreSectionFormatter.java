package com.lesports.sms.data.util.formatter.sportrard;

import com.google.common.base.Function;
import com.lesports.qmt.config.client.QmtConfigDictInternalApis;
import com.lesports.qmt.config.model.DictEntry;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.data.util.formatter.ElementFormatter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SoccerScoreSectionFormatter extends ElementFormatter implements Function<String, Long> {
    @Nullable
    @Override
    public Long apply(String input) {
        Long periodId = 0L;
        String sectionName = "";
        if (input.equals("Period1")) {
            sectionName = "上半场";
        }
        if (input.equals("Normaltime")) {
            sectionName = "下半场$";
        }
        //加时赛结束
        if (input.equals("Overtime")) {
            sectionName = "加时";
        }
        //点球大战结束
        if (input.equals("Penalties")) {
            sectionName = "点球";
        }
    return getDictIdByName(sectionName + "$");


    }
}



