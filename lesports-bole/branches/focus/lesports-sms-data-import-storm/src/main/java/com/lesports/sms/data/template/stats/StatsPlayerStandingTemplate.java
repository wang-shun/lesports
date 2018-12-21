package com.lesports.sms.data.template.stats;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.sportrard.SportrardPlayerStanding;
import com.lesports.sms.data.model.stats.StatsPlayerStanding;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
public class StatsPlayerStandingTemplate extends XmlTemplate {
    @XPath("//ns2:SportradarData/Sport/Category/Tournament/Teams")
    private List<StatsPlayerStanding> liveScores;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (StatsPlayerStanding liveScore : liveScores) {
            res.add(liveScore);
        }
        return res;
    }
}