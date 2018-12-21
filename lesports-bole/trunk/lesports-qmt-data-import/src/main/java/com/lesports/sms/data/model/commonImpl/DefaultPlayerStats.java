package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.util.formatter.soda.SodaCidFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaPlayerStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.NBAPlayerTotalStatsFormatter;
import com.lesports.sms.data.util.xml.annotation.XPathStats;
import com.lesports.sms.data.util.xml.annotation.XPathStats2;
import com.lesports.sms.data.util.xml.annotation.XPathSoda;
import com.lesports.sms.data.util.xml.annotation.XPathSoda2;
import com.lesports.sms.data.util.xml.annotation.XPathSportrard;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultPlayerStats extends DefaultModel {
    @XPathStats(value = "sports-statistics/sports-rankings/bk-player-splits")
    @XPathSoda(value = "SoccerFeed/Player")
    private List<PlayerStats> playerStatsList;

    public static class PlayerStats {
        @XPathStats(value = "./player-code/@global-id")
        @XPathSoda(value = "./@id")
        private String partnerId;
        @XPathStats(value = "./bk-player-split")
        @XPathSoda(value = "./Statistics/Team/Stat")
        private List<StatT> MainStats;
        @XPathSoda(value = "./Statistics/National/Stat")
        private List<StatT> subStats;

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }


        public void setMainStats(List<StatT> mainStats) {
            MainStats = mainStats;
        }

        public void setSubStats(List<StatT> subStats) {
            this.subStats = subStats;
        }

        public List<StatT> getSubStats() {
            return subStats;
        }

        public List<StatT> getMainStats() {
            return MainStats;
        }

        public static class StatT {
            @XPathSoda(formatter= SodaCidFormatter.class,value="./@compId")
            private Long cid;
            @XPathSoda(value="./@season")
            private String season;
            @XPathStats(formatter = NBAPlayerTotalStatsFormatter.class, value = ".")
            @XPathSoda(formatter = SodaPlayerStatsFormatter.class, value = ".")
            private Map<String, Object> elementStats;

            public String getSeason() {
                return season;
            }

            public void setSeason(String season) {
                this.season = season;
            }

            public Long getCid() {
                return cid;
            }

            public void setCid(Long cid) {
                this.cid = cid;
            }

            public Map<String, Object> getElementStats() {
                return elementStats;
            }

            public void setElementStats(Map<String, Object> elementStats) {
                this.elementStats = elementStats;
            }
        }

    }


    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (PlayerStats model : playerStatsList) {
            res.add(model);
        }
        return res;
    }
}
