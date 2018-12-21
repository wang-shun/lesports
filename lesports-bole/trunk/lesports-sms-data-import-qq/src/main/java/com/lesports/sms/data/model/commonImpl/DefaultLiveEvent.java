package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.DefaultModel;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/10/21.
 */
public class DefaultLiveEvent extends DefaultModel {
    private List<LiveModel> liveModels;

    public static class LiveModel {
        private String partnerId;
        List<LiveSingleEvent> cardEvent;

        List<LiveSingleEvent> substationEvent;
        List<LiveSingleEvent> goalEvent;
        List<LiveSingleEvent> commonEvents;

        public static class LiveSingleEvent {
            private String partnerId;
            private Long sectionId;
            private Double time;

            private String homeTeamScore;

            private String awayTeamScore;
            private Long eventType;

            private Long eventDetailType;

            private String coordinateX;

            private String coordinateY;

            private String teamId;


            private String MainPlayerId;

            private String subOutplayerId;
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
