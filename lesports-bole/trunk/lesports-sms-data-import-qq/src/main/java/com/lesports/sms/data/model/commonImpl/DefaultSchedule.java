package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ_1;
import com.lesports.sms.data.util.formatter.QQStartDateFormatter;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author qiaohngxin
 * @since 16-4-18
 */
public class DefaultSchedule extends DefaultModel implements Serializable {
    @JsonPathQQ(value = "$.data.*.*")
    @JsonPathQQ_1(value = "$.data.matches.*.list.*")
//  @JsonPathQQ_1(value = "$.data.matches.2016-10-30.list.*")
    private List<ScheduleModel> scheduleModels;

    public static class ScheduleModel {
        @JsonPathQQ(formatter = QQStartDateFormatter.class, value = "$.startTime")
        @JsonPathQQ_1(formatter = QQStartDateFormatter.class, value = "$.matchInfo.startTime")
        private String startTime;
        @JsonPathQQ(value = "$.mid")
        @JsonPathQQ_1(value = "$.matchInfo.mid")
        private String partnerId;
        private Long roundId;
        private Long stageId;
        private MatchStatus status;
        @JsonPathQQ(value = "$.rightId")
        @JsonPathQQ_1(value = "$.matchInfo.leftId")
        private String homeTeamId;
        @JsonPathQQ_1(value = "$.matchInfo.leftName")
        private String homeTeamName;
        private String homeScore;
        @JsonPathQQ(value = "$.leftId")
        @JsonPathQQ_1(value = "$.matchInfo.rightId")
        private String awayTeamId;
        @JsonPathQQ_1(value = "$.matchInfo.rightName")
        private String awayTeamName;
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

        public MatchStatus getStatus() {
            return status;
        }

        public void setStatus(MatchStatus status) {
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



