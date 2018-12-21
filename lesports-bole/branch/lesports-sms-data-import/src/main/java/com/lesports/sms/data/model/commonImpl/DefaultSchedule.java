package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.util.formatter.DateFormatter;
import com.lesports.sms.data.util.formatter.MatchInitialStatusFormatter;
import com.lesports.sms.data.util.formatter.stats.StatsMatchStatusFormatter;
import com.lesports.sms.data.util.formatter.stats.UtcDateFormatter;
import com.lesports.utils.xml.annotation.RoundFormatter;
import com.lesports.utils.xml.annotation.XPathSportrard;
import com.lesports.utils.xml.annotation.XPathStats;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author qiaohngxin
 * @since 16-4-18
 */
public class DefaultSchedule extends DefaultModel implements Serializable {
    @XPathSportrard(value = "/ns2:SportradarData/Sport/Category/Tournament/Matches/Match")
    @XPathStats(value = "/sports-statistics/sports-schedule/bk-schedule/game-schedule")
    private List<ScheduleModel> scheduleModels;

    public static class ScheduleModel {
        @XPathSportrard(formatter = DateFormatter.class, value = "./@dateOfMatch")
        @XPathStats(formatter = UtcDateFormatter.class, value = "concat(./date/@year,'-',./date/@month,'-',./date/@date,'-',./time/@hour,'-',./time/@minutes,'-'./time/@utc-hour)")
        private String startTime;
        @XPathSportrard(value = "./@id")
        @XPathStats(value = "./gamecode/@global-id")
        private String partnerId;
        @XPathSportrard(formatter = RoundFormatter.class, value = "./Roundinfo/@round")
        @XPathStats(formatter = RoundFormatter.class, value = "./week/@week")
        private Long roundId;
        private Long stageId;
        @XPathSportrard(formatter = MatchInitialStatusFormatter.class, value = "concat(./Result/@postponed,./Result/@canceled)")
        @XPathStats(formatter = StatsMatchStatusFormatter.class, value = "./status/@status")
        private com.lesports.api.common.MatchStatus status;
        @XPathSportrard(value = "./Teams/Team[@type='1']/@id")
        @XPathStats(value = "./home-team/team-code/@global-id")
        private String homeTeamId;
        @XPathSportrard(value = "./Teams/Team[@type='1']/@name")
        @XPathStats(value = "./home-team/team-name/@name")
        private String homeTeamName;
        @XPathStats(value = "./home-team-score/@score")
        private String homeScore;
        @XPathSportrard(value = "./Teams/Team[@type='2']/@id")
        @XPathStats(value = "./visiting-team/team-code/@global-id")
        private String awayTeamId;
        @XPathSportrard(value = "./Teams/Team[@type='2']/@name")
        @XPathStats(value = "./visiting-team/team-name/@name")
        private String awayTeamName;
        @XPathStats(value = "./visiting-team-score/@score")
        private String awaySore;
        private String changeToPartnerId;

        public String getChangeToPartnerId() {
            return changeToPartnerId;
        }

        public void setChangeToPartnerId(String changeToPartnerId) {
            this.changeToPartnerId = changeToPartnerId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getAwaySore() {
            return awaySore;
        }

        public void setAwaySore(String awaySore) {
            this.awaySore = awaySore;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public Long getRoundId() {
            return roundId;
        }

        public void setRoundId(Long roundId) {
            this.roundId = roundId;
        }

        public Long getStageId() {
            return stageId;
        }

        public void setStageId(Long stageId) {
            this.stageId = stageId;
        }

        public com.lesports.api.common.MatchStatus getStatus() {
            return status;
        }

        public void setStatus(com.lesports.api.common.MatchStatus status) {
            this.status = status;
        }

        public String getHomeTeamId() {
            return homeTeamId;
        }

        public void setHomeTeamId(String homeTeamId) {
            this.homeTeamId = homeTeamId;
        }

        public String getHomeScore() {
            return homeScore;
        }

        public void setHomeScore(String homeScore) {
            this.homeScore = homeScore;
        }

        public String getAwayTeamId() {
            return awayTeamId;
        }

        public void setAwayTeamId(String awayTeamId) {
            this.awayTeamId = awayTeamId;
        }

        public String getAwayTeamName() {
            return awayTeamName;
        }

        public void setAwayTeamName(String awayTeamName) {
            this.awayTeamName = awayTeamName;
        }

        public String getHomeTeamName() {
            return homeTeamName;
        }

        public void setHomeTeamName(String homeTeamName) {
            this.homeTeamName = homeTeamName;
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (ScheduleModel model : scheduleModels) {
            res.add(model);
        }
        return res;
    }


}



