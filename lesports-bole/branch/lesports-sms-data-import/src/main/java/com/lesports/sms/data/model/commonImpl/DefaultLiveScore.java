package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.api.common.MatchStatus;
import com.lesports.sms.data.util.formatter.MatchStatusFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaSectionFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaIsFirstFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaMatchStatusFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaTeamLiveStatsFormatter;
import com.lesports.sms.data.util.formatter.sportrard.*;
import com.lesports.sms.data.util.formatter.stats.StatsMatchStatusFormatter;
import com.lesports.utils.xml.annotation.XPathSoda;
import com.lesports.utils.xml.annotation.XPathSportrard;
import com.lesports.utils.xml.annotation.XPathStats;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/10/21.
 */
public class DefaultLiveScore extends DefaultModel {
    @XPathStats(value = "sports-statistics/sports-boxscores/bk-boxscores/bk-boxscore")
    @XPathSportrard(value = "/BetradarLivescoreData/Sport/Category/Tournament/Match")
    @XPathSoda(value = "/SoccerFeed/Match")
    private List<LiveModel> liveModels;

    public static class LiveModel {
        @XPathStats(value = "./gamecode/@global-id")
        @XPathSportrard(value = "./@Id")
        @XPathSoda(value = "./@id")
        private String partnerId;
        @XPathStats(formatter = StatsMatchStatusFormatter.class, value = "./gamestate/@status")
        @XPathSportrard(formatter = MatchStatusFormatter.class, value = "./Status/@Code")
        @XPathSoda(formatter = SodaMatchStatusFormatter.class, value = "./@period")
        private MatchStatus status;
        @XPathSportrard(formatter = SectionFormatter.class, value = "./Status/@Code")
        @XPathSoda(formatter = SodaSectionFormatter.class, value = "./@period")
        private Long sectionId;
        @XPathSportrard(formatter = SectionTimeFormatter.class, value = "./CurrentPeriodStart")
        private Double sectionTime;
        @XPathStats(value = "./home-team/team-code/@global-id")
        @XPathSportrard(value = "./Team1/@UniqueTeamId")
        @XPathSoda(value = "./Team[@side='Home']/@id")
        private String homeCompetitorId;
        @XPathSportrard(value = "./Team2/@UniqueTeamId")
        @XPathStats(value = "./visiting-team/team-code/@global-id")
        @XPathSoda(value = "./Team[@side='Away']/@id")
        private String awayCompetitorId;
        @XPathSportrard(value = "./Scores/Score[@type='Current']/Team1")
        @XPathStats(value = "./home-team/linescore")
        @XPathSoda(value = "./Team[@side='Home']/@score")
        String homeCompetitorScore;
        @XPathStats(value = "./home-team/linescore")
        @XPathSportrard(value = "./Scores/Score/Team1")
        List<SectionScore> homeCompetitorSectionScore;
        @XPathStats(value = "./team-stats/home-team-stats")
        @XPathSportrard(formatter = SportrardHomeTeamLiveStatsFormatter.class, value = "./Statistics")
        @XPathSoda(formatter = SodaTeamLiveStatsFormatter.class, value = "./Team[@side='Home']")
        Map<String, Object> homeCompetitorStat;
        @XPathSportrard(value = "./Team1/@formation")
        @XPathSoda(value = "./Team[@side='Home']/formation_used")
        String homeLineUpForamtion;
        @XPathStats(value = "./player-stats/home-player-stat")
        @XPathSportrard(value = "./Lineups/TeamPlayer[PlayerTeam=1]")
        @XPathSoda(value = "./Team[@side='Home']/PlayerLineUp/Player")
        List<SquadPlayerModel> homeLineUp;
        @XPathStats(value = "./visiting-team/linescore")
        @XPathSportrard(value = "./Scores/Score[@type='Current']/Team2")
        @XPathSoda(value = "./Team[@side='Away']/@score")
        String awayCompetitorScore;
        @XPathStats(value = "./visiting-team/linescore")
        @XPathSportrard(value = "./Scores/Score/Team2")
        List<SectionScore> awayCompetitorSectionScore;
        @XPathStats(value = "./team-stats/home-team-stats")
        @XPathSportrard(formatter = SportrardAwayTeamLiveStatsFormatter.class, value = "./Statistics")
        @XPathSoda(formatter = SodaTeamLiveStatsFormatter.class, value = "./Team[@side='Away']")
        Map<String, Object> awayCompetitorStat;
        @XPathSportrard(value = "./Team1/@formation")
        @XPathSoda(value = "./Team[@side='Away']/formation_used")
        String awayLineUpFormation;
        @XPathStats(value = "./player-stats/visiting-player-stat")
        @XPathSportrard(value = "./Lineups/TeamPlayer[PlayerTeam=2]")
        @XPathSoda(value = "./Team[@side='Away']/PlayerLineUp/Player")
        List<SquadPlayerModel> awayLineUp;

        public Map<String, Object> getAwayCompetitorStat() {
            return awayCompetitorStat;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }


        public com.lesports.api.common.MatchStatus getStatus() {
            return status;
        }

        public void setStatus(com.lesports.api.common.MatchStatus status) {
            this.status = status;
        }


        public Double getSectionTime() {
            return sectionTime;
        }

        public void setSectionTime(Double sectionTime) {
            this.sectionTime = sectionTime;
        }

        public String getHomeCompetitorId() {
            return homeCompetitorId;
        }

        public void setHomeCompetitorId(String homeCompetitorId) {
            this.homeCompetitorId = homeCompetitorId;
        }

        public String getAwayCompetitorId() {
            return awayCompetitorId;
        }

        public void setAwayCompetitorId(String awayCompetitorId) {
            this.awayCompetitorId = awayCompetitorId;
        }


        public Long getSectionId() {
            return sectionId;
        }


        public String getHomeLineUpForamtion() {
            return homeLineUpForamtion;
        }

        public List<SquadPlayerModel> getHomeLineUp() {
            return homeLineUp;
        }

        public String getAwayLineUpFormation() {
            return awayLineUpFormation;
        }

        public List<SquadPlayerModel> getAwayLineUp() {
            return awayLineUp;
        }

        public void setAwayCompetitorStat(Map<String, Object> awayCompetitorStat) {
            this.awayCompetitorStat = awayCompetitorStat;
        }

        public void setSectionId(Long sectionId) {
            this.sectionId = sectionId;
        }

        public Map<String, Object> getHomeCompetitorStat() {
            return homeCompetitorStat;
        }

        public void setHomeCompetitorStat(Map<String, Object> homeCompetitorStat) {
            this.homeCompetitorStat = homeCompetitorStat;
        }


        public void setHomeLineUpForamtion(String homeLineUpForamtion) {
            this.homeLineUpForamtion = homeLineUpForamtion;
        }

        public void setHomeLineUp(List<SquadPlayerModel> homeLineUp) {
            this.homeLineUp = homeLineUp;
        }

        public void setAwayLineUpFormation(String awayLineUpFormation) {
            this.awayLineUpFormation = awayLineUpFormation;
        }

        public void setAwayLineUp(List<SquadPlayerModel> awayLineUp) {
            this.awayLineUp = awayLineUp;
        }

        public List<SectionScore> getHomeCompetitorSectionScore() {
            return homeCompetitorSectionScore;
        }

        public void setHomeCompetitorSectionScore(List<SectionScore> homeCompetitorSectionScore) {
            this.homeCompetitorSectionScore = homeCompetitorSectionScore;
        }

        public String getHomeCompetitorScore() {
            return homeCompetitorScore;
        }

        public void setHomeCompetitorScore(String homeCompetitorScore) {
            this.homeCompetitorScore = homeCompetitorScore;
        }

        public String getAwayCompetitorScore() {
            return awayCompetitorScore;
        }

        public void setAwayCompetitorScore(String awayCompetitorScore) {
            this.awayCompetitorScore = awayCompetitorScore;
        }

        public List<SectionScore> getAwayCompetitorSectionScore() {
            return awayCompetitorSectionScore;
        }

        public void setAwayCompetitorSectionScore(List<SectionScore> awayCompetitorSectionScore) {
            this.awayCompetitorSectionScore = awayCompetitorSectionScore;
        }


        public static class SectionScore {
            @XPathSportrard(formatter = SoccerScoreSectionFormatter.class, value = "../@type")
            private Long periodId;
            @XPathSportrard(formatter = SoccerSectionNumFormatter.class, value = "../@type")
            private Integer periodNum;
            @XPathSportrard(value = "./text()")
            private String score;

            public Long getPeriodId() {
                return periodId;
            }

            public void setPeriodId(Long periodId) {
                this.periodId = periodId;
            }

            public Integer getPeriodNum() {
                return periodNum;
            }

            public void setPeriodNum(Integer periodNum) {
                this.periodNum = periodNum;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }
        }

        public static class SquadPlayerModel {
            @XPathStats(value = "./player-code/@global-id")
            @XPathSportrard(value = "./Player/@id")
            @XPathSoda(value = "./@id")
            private String playerId;
            @XPathStats(value = "./games/games-started")
            @XPathSportrard(formatter = SportrardIsFirstFormatter.class, value = "./Substitute")
            @XPathSoda(formatter = SodaIsFirstFormatter.class, value = "./@status")
            private boolean starting;
            private java.lang.Long position;
            @XPathSportrard(value = "./ShirtNumber")
            @XPathSoda(value = "./@ShirtNumber")
            private java.lang.Integer number;
            private java.lang.Integer dnp;
            private java.lang.String isOnCourt;
            @XPathSportrard(value = "./@pos")
            @XPathSoda(value = "./Stat[@type='formation_place']")
            private java.lang.String squadOrder;
            private Map<String, Object> stats;

            public String getPlayerId() {
                return playerId;
            }

            public void setPlayerId(String playerId) {
                this.playerId = playerId;
            }

            public boolean getStarting() {
                return starting;
            }

            public void setStarting(boolean starting) {
                this.starting = starting;
            }

            public Long getPosition() {
                return position;
            }

            public void setPosition(Long position) {
                this.position = position;
            }

            public Integer getNumber() {
                return number;
            }

            public void setNumber(Integer number) {
                this.number = number;
            }

            public Integer getDnp() {
                return dnp;
            }

            public void setDnp(Integer dnp) {
                this.dnp = dnp;
            }

            public String getIsOnCourt() {
                return isOnCourt;
            }

            public void setIsOnCourt(String isOnCourt) {
                this.isOnCourt = isOnCourt;
            }

            public String getSquadOrder() {
                return squadOrder;
            }

            public void setSquadOrder(String squadOrder) {
                this.squadOrder = squadOrder;
            }

            public Map<String, Object> getStats() {
                return stats;
            }

            public void setStats(Map<String, Object> stats) {
                this.stats = stats;
            }
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (LiveModel model : liveModels) {
            res.add(model);
        }
        return res;
    }

}
