package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.util.formatter.soda.SodaAssistStandingStatFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaGoalScoreStandingStatFormatter;
import com.lesports.sms.data.util.formatter.sportrard.AssistStandingStatFormatter;
import com.lesports.sms.data.util.formatter.sportrard.GoalScoreStandingStatFormatter;
import com.lesports.sms.data.util.formatter.stats.NBAPlayerStandingStatFormatter;
import com.lesports.sms.data.util.formatter.stats.StandingTypeFormatter;
import com.lesports.utils.xml.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/5/12.
 */
public class DefaultPlayerStanding extends DefaultModel implements Serializable {
    @XPathStats2(value = "/sports-statistics/sports-rankings/bk-leaders/bk-player-rankings/category")
    @XPathSportrard(value = "/ns2:SportradarData/Sport/Category/Tournament")
    @XPathSoda(value = "/SoccerFeed/Competition/Goal")
    @XPathSoda2(value = "/SoccerFeed/Competition/Assist")
    private List<PlayerStandings> playerStandingses;

    public static class PlayerStandings {
        @XPathStats(formatter = StandingTypeFormatter.class, value = "./@category_heading")
        @XPathSportrard(formatter = StandingTypeFormatter.class, value = "/ns2:SportradarData/@serviceName")
        @XPathSoda(formatter = StandingTypeFormatter.class, value = "concat('goalscore')")
        private Long type;
        @XPathStats(value = "./ranking")
        @XPathSportrard(value = "./Teams/Team/GoalScorers/Player")
        @XPathSportrard2(value = "./Teams/Team/Assists/Player")
        @XPathSoda(value = "./Player")
        private List<PlayerStandingItem> items;

        public Long getType() {
            return type;
        }

        public void setType(Long type) {
            this.type = type;
        }

        public List<PlayerStandingItem> getItems() {
            return items;
        }

        public void setItems(List<PlayerStandingItem> items) {
            this.items = items;
        }
    }

    public static class PlayerStandingItem {
        @XPathStats(value = "./player-id/@global-id")
        @XPathSportrard(value = "./@playerID")
        @XPathSoda(value = ". /@id")
        private String PartnerId;
        @XPathStats(value = "./ranking/@ranking")
        @XPathSportrard(value = "./PlayerStats/PlayerStatsEntry[@id='4']/@value")
        @XPathSportrard2(value = "./PlayerStats/PlayerStatsEntry[@id='27']/@value")
        private Integer rank;
        @XPathStats(formatter = NBAPlayerStandingStatFormatter.class, value = ".")
        @XPathSportrard(formatter = GoalScoreStandingStatFormatter.class, value = "./PlayerStats")
        @XPathSportrard2(formatter = AssistStandingStatFormatter.class, value = "./PlayerStats")
        @XPathSoda(formatter = SodaGoalScoreStandingStatFormatter.class, value = ".")
        @XPathSoda2(formatter = SodaAssistStandingStatFormatter.class, value = ".")
        private Map<String, Object> stats;

        public String getPartnerId() {
            return PartnerId;
        }

        public void setPartnerId(String partnerId) {
            PartnerId = partnerId;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public void setStats(Map<String, Object> stats) {
            this.stats = stats;
        }

        public Map<String, Object> getStats() {
            return stats;
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (PlayerStandings model : playerStandingses) {
            res.add(model);
        }
        return res;
    }

}
