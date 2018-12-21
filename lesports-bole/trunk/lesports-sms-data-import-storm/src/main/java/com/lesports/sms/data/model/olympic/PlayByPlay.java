package com.lesports.sms.data.model.olympic;

import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-19
 */
public class PlayByPlay implements Serializable {
    @XPath("/OdfBody/@DocumentCode")
    private String matchCode;
    @XPath("./@Code")
    private String periodCode;
    @XPath("./@Time")
    private String matchTime;
    @XPath("./@Pos")
    private String eventNumber;
    @XPath("./@Value")
    private String eventTypeCode;
    @XPath("./@Result")
    private String eventResultCode;
    @XPath("./@Text")
    private String textContent;
    @XPath("./Competitor/@Code")
    private String competitorCode;
    @XPath("./Competitor/@Type")
    private String competitorType;
    @XPath("./Competitor/Composition/Athlete")
    private List<EventAthlete> athletes;

    public static class EventAthlete implements Serializable {
        @XPath("./@Code")
        private String playerCode;
        @XPath("./@Order")
        private Long playerOrder;

        public String getPlayerCode() {
            return playerCode;
        }

        public void setPlayerCode(String playerCode) {
            this.playerCode = playerCode;
        }

        public Long getPlayerOrder() {
            return playerOrder;
        }

        public void setPlayerOrder(Long playerOrder) {
            this.playerOrder = playerOrder;
        }
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public void setPeriodCode(String periodCode) {
        this.periodCode = periodCode;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(String eventNumber) {
        this.eventNumber = eventNumber;
    }

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public String getEventResultCode() {
        return eventResultCode;
    }

    public void setEventResultCode(String eventResultCode) {
        this.eventResultCode = eventResultCode;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getCompetitorCode() {
        return competitorCode;
    }

    public void setCompetitorCode(String competitorCode) {
        this.competitorCode = competitorCode;
    }

    public String getCompetitorType() {
        return competitorType;
    }

    public void setCompetitorType(String competitorType) {
        this.competitorType = competitorType;
    }

    public List<EventAthlete> getAthletes() {
        return athletes;
    }

    public void setAthletes(List<EventAthlete> athletes) {
        this.athletes = athletes;
    }
}
