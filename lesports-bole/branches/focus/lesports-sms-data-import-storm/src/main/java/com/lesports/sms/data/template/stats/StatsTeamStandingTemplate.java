package com.lesports.sms.data.template.stats;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.sportrard.SportrardTeamStanding;
import com.lesports.sms.data.model.stats.StatsTeamStanding;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
public class StatsTeamStandingTemplate extends XmlTemplate {
    @XPath("//ns2:SportradarData/Sport/Category/Tournament/LeagueTable/LeagueTableRows")
    private List<StatsTeamStanding> teamStandings;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (StatsTeamStanding teamStanding : teamStandings) {
            res.add(teamStanding);
        }
        return res;
    }
}