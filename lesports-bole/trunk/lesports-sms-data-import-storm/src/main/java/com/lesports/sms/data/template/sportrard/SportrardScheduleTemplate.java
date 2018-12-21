package com.lesports.sms.data.template.sportrard;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.sportrard.MatchSchedule;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
public class SportrardScheduleTemplate extends XmlTemplate {
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/Matches/Match")
    private List<MatchSchedule> matches;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (MatchSchedule schedule : matches) {
            res.add(schedule);
        }
        return res;
    }
}