package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.api.common.CareerScopeType;
import com.lesports.qmt.sbd.api.common.CareerStatType;
import com.lesports.sms.data.util.formatter.soda.SodaPlayerCareerStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.NBAPlayerTotalStatsFormatter;
import com.lesports.sms.data.util.xml.annotation.XPathStats;
import com.lesports.sms.data.util.xml.annotation.XPathStats2;
import com.lesports.sms.data.util.xml.annotation.XPathSoda;
import com.lesports.sms.data.util.xml.annotation.XPathSoda2;
import com.lesports.sms.data.util.xml.annotation.XPathSportrard;
import com.lesports.sms.data.util.xml.annotation.XPathSportrard2;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultPlayerCareerStats extends DefaultModel {
    @XPathStats(value = "sports-statistics/sports-rankings/bk-player-splits")
    @XPathSoda(value = "SoccerFeed/Player")
    private List<PlayerCareerStats> playerStatsList;

    public static class PlayerCareerStats {
        @XPathStats(value = "./player-code/@global-id")
        @XPathSoda(value = "./@id")
        private String partnerId;
        private CareerScopeType type;
        private Long ScopeId;
        private CareerStatType statTypeType;//total,bese
        @XPathSoda(value = "./Statistics/Team")
        private List<CareerStatT> clubStats;
        @XPathSoda(value = "./Statistics/National")
        private List<CareerStatT> NationalStats;

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public CareerScopeType getType() {
            return type;
        }

        public void setType(CareerScopeType type) {
            this.type = type;
        }

        public Long getScopeId() {
            return ScopeId;
        }

        public void setScopeId(Long scopeId) {
            ScopeId = scopeId;
        }

        public CareerStatType getStatTypeType() {
            return statTypeType;
        }

        public void setStatTypeType(CareerStatType statTypeType) {
            this.statTypeType = statTypeType;
        }

        public List<CareerStatT> getClubStats() {
            return clubStats;
        }

        public void setClubStats(List<CareerStatT> clubStats) {
            this.clubStats = clubStats;
        }

        public List<CareerStatT> getNationalStats() {
            return NationalStats;
        }

        public void setNationalStats(List<CareerStatT> nationalStats) {
            NationalStats = nationalStats;
        }

        public static class CareerStatT {
            @XPathStats(formatter = NBAPlayerTotalStatsFormatter.class, value = ".")
            @XPathSoda(formatter = SodaPlayerCareerStatsFormatter.class, value = ".")
            private Map<String, Object> elementStats;

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
        for (PlayerCareerStats model : playerStatsList) {
            res.add(model);
        }
        return res;
    }
}
