package com.lesports.sms.data.template.stats;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.sportrard.MatchSchedule;
import com.lesports.sms.data.model.stats.StatsSchedule;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
public class StatsScheduleTemplate extends XmlTemplate {
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/Matches/Match")
    private List<StatsSchedule> matches;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (StatsSchedule schedule : matches) {
            res.add(schedule);
        }
        return res;
    }
}