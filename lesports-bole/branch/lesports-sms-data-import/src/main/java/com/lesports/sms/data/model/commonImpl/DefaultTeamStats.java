package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.util.formatter.soda.SodaTeamAvgStatsFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaTeamStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.NBATeamAvgStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.NBATeamTotalStatsFormatter;
import com.lesports.utils.xml.annotation.XPathSoda;
import com.lesports.utils.xml.annotation.XPathSportrard;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultTeamStats extends DefaultModel {
    @XPathSportrard(value = "/sports-statistics/sports-teamstats/bk-team-splits")
    @XPathSoda(value = "/SoccerFeed/Team")
    private List<TeamStat> teamStatList;

    public static class TeamStat {
        @XPathSportrard(value = "./team-code/@global-id")
        @XPathSoda(value = "./@id")
        private String PartnerId;
        @XPathSportrard(value = "./bk-team-split[@stats='own']")
        @XPathSoda(value = "./Statistics/Stat")
        private List<StatT> statTList;

        public List<StatT> getStatTList() {
            return statTList;
        }

        public void setStatTList(List<StatT> statTList) {
            this.statTList = statTList;
        }

        public String getPartnerId() {
            return PartnerId;
        }

        public void setPartnerId(String partnerId) {
            PartnerId = partnerId;
        }
    }

    public static class StatT {
        @XPathSoda(value = "./@compId")
        private Long cid;
        @XPathSoda(value = "./@season")
        private String season;
        @XPathSportrard(formatter = NBATeamTotalStatsFormatter.class, value = ".")
        @XPathSoda(formatter = SodaTeamStatsFormatter.class, value = ".")
        private Map<String, Object> stats;
        @XPathSportrard(formatter = SodaTeamAvgStatsFormatter.class, value = ".")
        private Map<String, Object> avgStats;

        public void setStats(Map<String, Object> stats) {
            this.stats = stats;
        }

        public Map<String, Object> getStats() {
            return stats;
        }

        public Long getCid() {
            return cid;
        }

        public void setCid(Long cid) {
            this.cid = cid;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public Map<String, Object> getAvgStats() {
            return avgStats;
        }

        public void setAvgStats(Map<String, Object> avgStats) {
            this.avgStats = avgStats;
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (TeamStat model : teamStatList) {
            res.add(model);
        }
        return res;
    }
}
