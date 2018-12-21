package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.api.common.GroundType;
import com.lesports.sms.data.util.formatter.MinToSecondFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaEventTypeFormatter;
import com.lesports.sms.data.util.formatter.soda.SodaSectionFormatter;
import com.lesports.sms.data.util.formatter.sportrard.CardTypeFormatter;
import com.lesports.sms.data.util.formatter.sportrard.EventDetailTypeFormatter;
import com.lesports.sms.data.util.formatter.sportrard.SectionFormatter;
import com.lesports.sms.data.util.formatter.sportrard.TeamTypeFormatter;
import com.lesports.sms.data.util.formatter.stats.StatsEventDetailTypeFormatter;
import com.lesports.sms.data.util.formatter.stats.StatsEventTypeFormatter;
import com.lesports.sms.data.util.formatter.stats.StatsSectionFormatter;
import com.lesports.sms.data.util.xml.annotation.XPathStats;
import com.lesports.sms.data.util.xml.annotation.XPathStats2;
import com.lesports.sms.data.util.xml.annotation.XPathSoda;
import com.lesports.sms.data.util.xml.annotation.XPathSoda2;
import com.lesports.sms.data.util.xml.annotation.XPathSportrard;
import com.lesports.sms.data.util.xml.annotation.XPathSportrard2;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/10/21.
 */
public class DefaultLiveEvent extends DefaultModel {
    @XPathStats(value = "sports-statistics/sports-scores/nba-playbyplay")
    @XPathSportrard(value = "/BetradarLivescoreData/Sport/Category/Tournament/Match")
    @XPathSoda(value = "/SoccerFeed/Match")
    private List<LiveModel> liveModels;

    public static class LiveModel {
        @XPathStats(value = "./gamecode/@global-id")
        @XPathSportrard(value = "./@Id")
        @XPathSoda(value = "./@id")
        private String partnerId;
        @XPathSportrard(value = "./Cards/Card")
        List<LiveSingleEvent> cardEvent;
        @XPathSportrard(value = "./Substitutions/Substitution")
        List<LiveSingleEvent> substationEvent;
        @XPathSportrard(value = "./Goals/Goal")
        List<LiveSingleEvent> goalEvent;
        @XPathStats(value = "./play")
        List<LiveSingleEvent> commonEvents;

        public static class LiveSingleEvent {
            @XPathStats(value = "./@id")
            private String partnerId;
            @XPathStats(formatter = StatsSectionFormatter.class, value = "./@quarter")
            @XPathSportrard(formatter = SectionFormatter.class, value = "./Status/@Code")
            @XPathSoda(formatter = SodaSectionFormatter.class, value = "./@periodId")
            private Long sectionId;
            @XPathSportrard(formatter=MinToSecondFormatter.class,value = "./Time")
            @XPathSoda(value = "./@min")
            private Double time;
            @XPathStats(value = "./@home-score")
            @XPathSoda(value = "../@homeScore")
            private String homeTeamScore;
            @XPathStats(value = "./@visitor-score")
            @XPathSoda(value = "../@awayScore")
            private String awayTeamScore;
            @XPathStats(formatter = StatsEventTypeFormatter.class, value = "./@event-id")
            @XPathSoda(formatter = SodaEventTypeFormatter.class, value = "./@eventName")
            @XPathSportrard(formatter = CardTypeFormatter.class, value = "./@type")
            private Long eventType;
            @XPathSportrard(formatter = EventDetailTypeFormatter.class, value = "./@from")
            @XPathStats(formatter = StatsEventDetailTypeFormatter.class, value = "concat(./@event-id,'|',./@detail-id)")
            private Long eventDetailType;
            @XPathStats(value = "./@x-shot-coord")
            private String coordinateX;
            @XPathStats(value = "./@x-shot-coord")
            private String coordinateY;
            @XPathStats(value = "./@global-team-code-1")
            @XPathSoda(value = "./@teamId")
            private String teamId;
            @XPathSportrard(formatter = TeamTypeFormatter.class, value = "./PlayerTeam")
            private GroundType teamType;//teamHOmeor Away
            @XPathStats(value = "./@player-id-1")
            @XPathSportrard(value = "./Player/@id")
            @XPathSoda(value = "./@PlayerId")
            private String MainPlayerId;
            @XPathStats(value = "./@player-id-2")
            @XPathSportrard2(value = "./PlayerOut/@id")
            @XPathSoda(value = "./@SubOnName")
            private String subOutplayerId;
            @XPathSoda(value = "./@SubOnName")
            @XPathSportrard2(value = "./PlayerIn/@id")
            private String subOnplayerId;

            public String getPartnerId() {
                return partnerId;
            }

            public void setPartnerId(String partnerId) {
                this.partnerId = partnerId;
            }

            public Long getEventDetailType() {
                return eventDetailType;
            }

            public void setEventDetailType(Long eventDetailType) {
                this.eventDetailType = eventDetailType;
            }

            public Double getTime() {
                return time;
            }

            public void setTime(Double time) {
                this.time = time;
            }

            public Long getEventType() {
                return eventType;
            }

            public void setEventType(Long eventType) {
                this.eventType = eventType;
            }

            public String getTeamId() {
                return teamId;
            }

            public void setTeamId(String teamId) {
                this.teamId = teamId;
            }

            public String getMainPlayerId() {
                return MainPlayerId;
            }

            public void setMainPlayerId(String mainPlayerId) {
                MainPlayerId = mainPlayerId;
            }

            public Long getSectionId() {
                return sectionId;
            }

            public void setSectionId(Long sectionId) {
                this.sectionId = sectionId;
            }

            public String getHomeTeamScore() {
                return homeTeamScore;
            }

            public void setHomeTeamScore(String homeTeamScore) {
                this.homeTeamScore = homeTeamScore;
            }

            public String getAwayTeamScore() {
                return awayTeamScore;
            }

            public void setAwayTeamScore(String awayTeamScore) {
                this.awayTeamScore = awayTeamScore;
            }

            public String getCoordinateX() {
                return coordinateX;
            }

            public void setCoordinateX(String coordinateX) {
                this.coordinateX = coordinateX;
            }

            public String getCoordinateY() {
                return coordinateY;
            }

            public void setCoordinateY(String coordinateY) {
                this.coordinateY = coordinateY;
            }

            public GroundType getTeamType() {
                return teamType;
            }

            public void setTeamType(GroundType teamType) {
                this.teamType = teamType;
            }

            public String getSubOutplayerId() {
                return subOutplayerId;
            }

            public void setSubOutplayerId(String subOutplayerId) {
                this.subOutplayerId = subOutplayerId;
            }

            public String getSubOnplayerId() {
                return subOnplayerId;
            }

            public void setSubOnplayerId(String subOnplayerId) {
                this.subOnplayerId = subOnplayerId;
            }
        }


        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public List<LiveSingleEvent> getCardEvent() {
            return cardEvent;
        }

        public void setCardEvent(List<LiveSingleEvent> cardEvent) {
            this.cardEvent = cardEvent;
        }

        public List<LiveSingleEvent> getSubstationEvent() {
            return substationEvent;
        }

        public void setSubstationEvent(List<LiveSingleEvent> substationEvent) {
            this.substationEvent = substationEvent;
        }

        public List<LiveSingleEvent> getGoalEvent() {
            return goalEvent;
        }

        public void setGoalEvent(List<LiveSingleEvent> goalEvent) {
            this.goalEvent = goalEvent;
        }

        public List<LiveSingleEvent> getCommonEvents() {
            return commonEvents;
        }

        public void setCommonEvents(List<LiveSingleEvent> commonEvents) {
            this.commonEvents = commonEvents;
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
