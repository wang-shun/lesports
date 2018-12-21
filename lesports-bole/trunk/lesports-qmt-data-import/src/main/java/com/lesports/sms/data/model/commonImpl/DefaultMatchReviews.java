package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.api.common.CareerScopeType;
import com.lesports.qmt.sbd.api.common.CareerStatType;
import com.lesports.qmt.sbd.model.MatchReview;
import com.lesports.sms.data.util.formatter.soda.SodaPlayerCareerStatsFormatter;
import com.lesports.sms.data.util.formatter.stats.NBAPlayerTotalStatsFormatter;
import com.lesports.sms.data.util.xml.annotation.XPathSoda;
import com.lesports.sms.data.util.xml.annotation.XPathStats;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultMatchReviews extends DefaultModel {
    @XPathSoda(value = "SoccerFeed/Preview")
    private List<MatchReviewModel> matchReviewModels;

    public static class MatchReviewModel {
        @XPathSoda(value = "./Match/@id")
        private String partnerId;
        @XPathSoda(value = "./Match/@stadName")
        private String stadium;
        @XPathSoda(value = "./Match/@homeId")
        private String homeTeamId;
        @XPathSoda(value = "./Match/@awayId")
        private String awayTeamId;
        @XPathSoda(value = "./History/Match")
        private List<MatchInfoModel> confrontationsList;
        @XPathSoda(value = "./Before/Home/Match")
        private List<MatchInfoModel> homeCompetitorBeforeMatchInfoList;
        @XPathSoda(value = "./After/Home/Match")
        private List<MatchInfoModel> homeCompetitorAfterMatchInfoList;
        @XPathSoda(value = "./Before/Away/Match")
        private List<MatchInfoModel> awayCompetitorBeforeMatchInfoList;
        @XPathSoda(value = "./After/Away/Match")
        private List<MatchInfoModel> awayCompetitorAfterMatchInfoList;

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public String getStadium() {
            return stadium;
        }

        public void setStadium(String stadium) {
            this.stadium = stadium;
        }

        public String getHomeTeamId() {
            return homeTeamId;
        }

        public void setHomeTeamId(String homeTeamId) {
            this.homeTeamId = homeTeamId;
        }

        public String getAwayTeamId() {
            return awayTeamId;
        }

        public void setAwayTeamId(String awayTeamId) {
            this.awayTeamId = awayTeamId;
        }

        public List<MatchInfoModel> getConfrontationsList() {
            return confrontationsList;
        }

        public void setConfrontationsList(List<MatchInfoModel> confrontationsList) {
            this.confrontationsList = confrontationsList;
        }

        public List<MatchInfoModel> getHomeCompetitorBeforeMatchInfoList() {
            return homeCompetitorBeforeMatchInfoList;
        }

        public void setHomeCompetitorBeforeMatchInfoList(List<MatchInfoModel> homeCompetitorBeforeMatchInfoList) {
            this.homeCompetitorBeforeMatchInfoList = homeCompetitorBeforeMatchInfoList;
        }

        public List<MatchInfoModel> getHomeCompetitorAfterMatchInfoList() {
            return homeCompetitorAfterMatchInfoList;
        }

        public void setHomeCompetitorAfterMatchInfoList(List<MatchInfoModel> homeCompetitorAfterMatchInfoList) {
            this.homeCompetitorAfterMatchInfoList = homeCompetitorAfterMatchInfoList;
        }

        public List<MatchInfoModel> getAwayCompetitorBeforeMatchInfoList() {
            return awayCompetitorBeforeMatchInfoList;
        }

        public void setAwayCompetitorBeforeMatchInfoList(List<MatchInfoModel> awayCompetitorBeforeMatchInfoList) {
            this.awayCompetitorBeforeMatchInfoList = awayCompetitorBeforeMatchInfoList;
        }

        public List<MatchInfoModel> getAwayCompetitorAfterMatchInfoList() {
            return awayCompetitorAfterMatchInfoList;
        }

        public void setAwayCompetitorAfterMatchInfoList(List<MatchInfoModel> awayCompetitorAfterMatchInfoList) {
            this.awayCompetitorAfterMatchInfoList = awayCompetitorAfterMatchInfoList;
        }

        public static class MatchInfoModel {
            @XPathSoda(value = "./@id")
            private String matchPartnerId;

            public String getMatchPartnerId() {
                return matchPartnerId;
            }

            public void setMatchPartnerId(String matchPartnerId) {
                this.matchPartnerId = matchPartnerId;
            }
        }

    }


    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (MatchReviewModel model : matchReviewModels) {
            res.add(model);
        }
        return res;
    }
}
