package com.lesports.sms.data.template.olympic;

import com.google.common.collect.Lists;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.annotation.Tag;
import com.lesports.sms.data.model.olympic.Schedule;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
@Tag(Constants.TAG_TEMPLATE_SCHEDULE)
public class ScheduleTemplate extends XmlTemplate {
    @XPath("/OdfBody/Competition/Unit")
    private List<Schedule> schedules;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (Schedule schedule : schedules) {
            res.add(schedule);
        }
        return res;
    }
}