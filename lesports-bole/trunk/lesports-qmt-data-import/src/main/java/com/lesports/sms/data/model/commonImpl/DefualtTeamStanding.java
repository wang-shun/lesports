package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.api.common.ScopeType;
import com.lesports.sms.data.util.formatter.soda.SodaTeamStandingStatsFormatter;
import com.lesports.sms.data.util.formatter.ScopeIdFormatter;
import com.lesports.sms.data.util.formatter.ScopeTypeFormatter;
import com.lesports.sms.data.util.formatter.sportrard.SportrardTeamStandinglStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.NBATeamStandinglStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.StatsScopeIdFormatter;
import com.lesports.sms.data.util.formatter.stats.StatsScopeTypeFormatter;
import com.lesports.sms.data.util.xml.annotation.XPathStats;
import com.lesports.sms.data.util.xml.annotation.XPathStats2;
import com.lesports.sms.data.util.xml.annotation.XPathSoda;
import com.lesports.sms.data.util.xml.annotation.XPathSoda2;
import com.lesports.sms.data.util.xml.annotation.XPathSportrard;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/5/12.
 */
public class DefualtTeamStanding extends DefaultModel {
    @XPathStats(value = "/sports-statistics/sports-standings/nba-standings/nba-conference-standings/nba-division-standings")
    @XPathStats2(value = "/sports-statistics/sports-standings/nba-standings/nba-conference-standings")
    @XPathSportrard(value = "/ns2:SportradarData/Sport/Category/Tournament/LeagueTable/LeagueTableRows")
    @XPathSoda(value = "/SoccerFeed/Competition/Round[last()]")
    private List<TeamStanding> standings;

    public static class TeamStanding {
        @XPathStats(formatter = StatsScopeTypeFormatter.class, value = "concat('DIVISION')")
        @XPathStats2(formatter = StatsScopeTypeFormatter.class, value = "concat('CONFERENCE')")
        @XPathSportrard(formatter = ScopeTypeFormatter.class, value = "/ns2:SportradarData/@fileName")
        @XPathSoda(value = "./group")
        private ScopeType scopeType;
        @XPathStats(formatter = StatsScopeIdFormatter.class, value = "./@division")
        @XPathStats2(formatter = StatsScopeIdFormatter.class, value = "./@conference")
        @XPathSportrard(formatter = ScopeIdFormatter.class, value = "/ns2:SportradarData/@fileName")
        @XPathSoda(value = "./group")
        private Long scopeId;
        @XPathStats(value = "./nba-team-standings")
        @XPathStats2(value = "./nba-division-standings/nba-team-standings")
        @XPathSportrard(value = "./LeagueTableRow")
        @XPathSoda(value = "./Teams[@group='A']")
        @XPathSoda2(value = "./Teams[@group='B']")
        private List<TeamStandingItem> items;

        public ScopeType getScopeType() {
            return scopeType;
        }

        public void setScopeType(ScopeType scopeType) {
            this.scopeType = scopeType;
        }

        public Long getScopeId() {
            return scopeId;
        }

        public void setScopeId(Long scopeId) {
            this.scopeId = scopeId;
        }

        public List<TeamStandingItem> getItems() {
            return items;
        }

        public void setItems(List<TeamStandingItem> items) {
            this.items = items;
        }

        public static class TeamStandingItem {
            @XPathStats(value = "./team-code/@global-id")
            @XPathSportrard(value = "./Team/@id")
            @XPathSoda(value = "./@id")
            private String teamId;
            @XPathStats(value = "./place/@place")
            @XPathStats2(value = "./conference-seed/@seed")
            @XPathSportrard(value = "./LeagueTableColumn[@key='positionTotal']/@value")
            @XPathSoda(value = "./@rank")
            public Integer rank;
            @XPathStats(formatter = NBATeamStandinglStatsFormatter.class, value = ".")
            @XPathSportrard(formatter = SportrardTeamStandinglStatsFormatter.class, value = ".")
            @XPathSoda(formatter = SodaTeamStandingStatsFormatter.class, value = ".")
            public Map<String, Object> teamStats;


            public String getTeamId() {
                return teamId;
            }

            public void setTeamId(String teamId) {
                this.teamId = teamId;
            }

            public Integer getRank() {
                return rank;
            }

            public void setRank(Integer rank) {
                this.rank = rank;
            }

            public Map<String, Object> getTeamStats() {
                return teamStats;
            }

            public void setTeamStats(Map<String, Object> teamStats) {
                this.teamStats = teamStats;
            }
        }
    }


    public List<TeamStanding> getStandings() {
        return standings;
    }

    public void setStandings(List<TeamStanding> standings) {
        this.standings = standings;
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (TeamStanding model : standings) {
            res.add(model);
        }
        return res;
    }

}
