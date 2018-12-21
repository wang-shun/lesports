package com.lesports.sms.data.template.sportrard;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.sportrard.SportrardPlayerStanding;
import com.lesports.sms.data.model.sportrard.SportrardTeamStanding;
import com.lesports.sms.data.template.XmlTemplate;
import com.lesports.utils.xml.annotation.XPath;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/19.
 */
public class TeamStandingTemplate extends XmlTemplate {
    @XPath("//ns2:SportradarData/Sport/Category/Tournament/LeagueTable/LeagueTableRows")
    private List<SportrardTeamStanding> liveScores;

    @Override
    public List<Object> getData() {
        List<Object> res= Lists.newArrayList();
        for (SportrardTeamStanding liveScore : liveScores) {
            res.add(liveScore);
        }
        return res;
    }
}