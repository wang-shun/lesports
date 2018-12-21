package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ_1;
import com.lesports.sms.data.util.formatter.BooleanFormatter;
import com.lesports.sms.data.util.formatter.QQ.*;
import com.lesports.sms.model.Match;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/10/21.
 */
public class DefaultLiveScore extends DefaultModel {
    @JsonPathQQ(value = "$.data")
    private List<LiveModel> liveModels;

    public static class LiveModel {
        private String partnerId;
        @JsonPathQQ(formatter = QQMatchStatusFormatter.class, value = "$.teamInfo.matchPeriod")
        @JsonPathQQ_1(formatter = QQMatchStatusFormatter.class, value = "$.livePeriod")
        private MatchStatus status;
        @JsonPathQQ(formatter = QQSectionFormatter.class, value = "$.teamInfo.quarterDesc")
        private Long sectionId;
        @JsonPathQQ(formatter = QQSectionTimeFormatter.class, value = "$.teamInfo.quarterDesc")
        private Double sectionTime;
        @JsonPathQQ(value = "$.teamInfo.rightId")
        @JsonPathQQ_1(value = "$.teamInfo.leftId")
        private String homeCompetitorId;

        @JsonPathQQ(value = "$.teamInfo.rightGoal")
        @JsonPathQQ_1(value = "$.teamInfo.leftGoal")
        String homeCompetitorScore;
        @JsonPathQQ(formatter = QQSectionScoreFormatter.class, value = "$.periodGoals.rows[1]")
        @JsonPathQQ_1(formatter = QQSectionScoreFormatter.class, value = "$.stats[0].goals[0].rows[0]")
        Map<Integer, Match.SectionResult> homeCompetitorSectionScoreMap;
        @JsonPathQQ(formatter = QQLiveTeamStatsFormatter.class, value = "$.nbaPlayerMatchTotal.home")
        @JsonPathQQ_1(formatter = QQLiveHomeTeamStatsFormatter.class, value = "$.stats[2].teamStats")
        Map<String, Object> homeCompetitorStat;
        String homeLineUpForamtion;
        @JsonPathQQ(value = "$.playerStats.right[1:]")
        List<SquadPlayerModel> homeLineUp;
        @JsonPathQQ(value = "$.teamInfo.leftId")
        @JsonPathQQ_1(value = "$.teamInfo.rightId")
        private String awayCompetitorId;
        @JsonPathQQ(value = "$.teamInfo.leftGoal")
        @JsonPathQQ_1(value = "$.teamInfo.rightGoal")
        String awayCompetitorScore;
        @JsonPathQQ(formatter = QQSectionScoreFormatter.class, value = "$.periodGoals.rows[0]")
        @JsonPathQQ_1(formatter = QQSectionScoreFormatter.class, value = "$.stats[0].goals[0].rows[1]")
        Map<Integer, Match.SectionResult> awayCompetitorSectionScoreMap;
        @JsonPathQQ(formatter = QQLiveTeamStatsFormatter.class, value = "$.nbaPlayerMatchTotal.away")
        @JsonPathQQ_1(formatter = QQLiveAwayTeamStatsFormatter.class, value = "$.stats[2].teamStats")
        Map<String, Object> awayCompetitorStat;
        String awayLineUpFormation;
        @JsonPathQQ(value = "$.playerStats.left[1:]")
        List<SquadPlayerModel> awayLineUp;
        @JsonPathQQ_1(value = "$.stats[3].playerStats.*")
        List<SquadPlayerModel> allLineUp;

        public Map<String, Object> getAwayCompetitorStat() {
            return awayCompetitorStat;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public void setHomeCompetitorSectionScoreMap(Map<Integer, Match.SectionResult> homeCompetitorSectionScoreMap) {
            this.homeCompetitorSectionScoreMap = homeCompetitorSectionScoreMap;
        }

        public Map<Integer, Match.SectionResult> getHomeCompetitorSectionScoreMap() {
            return homeCompetitorSectionScoreMap;
        }

        public Map<Integer, Match.SectionResult> getAwayCompetitorSectionScoreMap() {
            return awayCompetitorSectionScoreMap;
        }

        public void setAwayCompetitorSectionScoreMap(Map<Integer, Match.SectionResult> awayCompetitorSectionScoreMap) {
            this.awayCompetitorSectionScoreMap = awayCompetitorSectionScoreMap;
        }

        public MatchStatus getStatus() {
            return status;
        }

        public void setStatus(MatchStatus status) {
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

        public List<SquadPlayerModel> getAllLineUp() {
            return allLineUp;
        }

        public void setAllLineUp(List<SquadPlayerModel> allLineUp) {
            this.allLineUp = allLineUp;
        }

        public static class SectionScore {

            private Long periodId;

            private Integer periodNum;

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
            @JsonPathQQ(value = "$.playerId")
            private String playerId;
            @JsonPathQQ(value = "$.row[0]")
            private String playerName;
            @JsonPathQQ(formatter = BooleanFormatter.class, value = "$.row[1]")
            private boolean starting;
            private java.lang.Long position;
            private java.lang.Integer number;
            private java.lang.Integer dnp;
            private java.lang.String isOnCourt;
            private java.lang.String squadOrder;
            @JsonPathQQ(formatter = QQLivePlayerStatsFormatter.class, value = "$.row")
            @JsonPathQQ_1(formatter = QQCBALivePlayerStatsFormatter.class, value = "$.row")
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

            public String getPlayerName() {
                return playerName;
            }

            public void setPlayerName(String playerName) {
                this.playerName = playerName;
            }

            public boolean isStarting() {
                return starting;
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
