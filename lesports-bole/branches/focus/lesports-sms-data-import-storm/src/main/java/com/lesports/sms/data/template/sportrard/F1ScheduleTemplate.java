package com.lesports.sms.data.template.sportrard;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.olympic.MedalDetail;
import com.lesports.sms.data.model.sportrard.F1MatchSchedule;
import com.lesports.sms.data.model.sportrard.MatchSchedule;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
public class F1ScheduleTemplate extends XmlTemplate {
    @XPath("/ns2:SportradarData/Sport/Category/Stage/ChildStages/Stage")
    private List<Stage> stages;

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (Stage stage : stages) {
            for (F1MatchSchedule schedule : stage.getSchedules())
                res.add(schedule);
        }
        return res;
    }

    public static class Stage {
        @XPath("./ChildStages/Stage")
        private List<F1MatchSchedule> schedules;

        public List<F1MatchSchedule> getSchedules() {
            return schedules;
        }

        public void setSchedules(List<F1MatchSchedule> schedules) {
            this.schedules = schedules;
        }
    }
}