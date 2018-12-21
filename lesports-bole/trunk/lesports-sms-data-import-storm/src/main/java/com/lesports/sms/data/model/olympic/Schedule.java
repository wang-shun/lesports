package com.lesports.sms.data.model.olympic;

import com.lesports.sms.data.utils.DateFormatter;
import com.lesports.sms.data.utils.LocalDateFormatter;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public class Schedule implements Serializable {
    @XPath("/OdfBody/@DocumentCode")
    private String documentCode;
    @XPath("./@Code")
    private String code;
    @XPath("./ItemName/@Value")
    private String enName;
    @XPath("./@StartDate")
    @Formatter(DateFormatter.class)
    private String startTime;
    @XPath("./@StartDate")
    @Formatter(LocalDateFormatter.class)
    private String localTime;
    @XPath("./@EndDate")
    @Formatter(DateFormatter.class)
    private String endTime;
    @XPath("./@ScheduleStatus")
    private String scheduleStatus;
    @XPath("./@UnitNum")
    private Integer number;
    @XPath("./@PhaseType")
    private String phraseType;
    @XPath("./@Medal")
    private String medal;
    @XPath("./VenueDescription/@VenueName")
    private String venueName;
    @XPath("./StartList/Start")
    private List<Competitor> competitorList;

    public static class Competitor implements Serializable {
        @XPath("./@SortOrder")
        private String order;
        @XPath("./Competitor/@Code")
        private String competitorCode;
        @XPath("./Competitor/@Type")
        private String competitorType;
        @XPath("./Competitor/@Organisation")
        private String organisation;

        public String getCompetitorCode() {
            return competitorCode;
        }

        public String getCompetitorType() {
            return competitorType;
        }

        public String getOrganisation() {
            return organisation;
        }

        public String getOrder() {
            return order;
        }
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getPhraseType() {
        return phraseType;
    }

    public void setPhraseType(String phraseType) {
        this.phraseType = phraseType;
    }

    public String getMedal() {
        return medal;
    }

    public void setMedal(String medal) {
        this.medal = medal;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public List<Competitor> getCompetitorList() {
        return competitorList;
    }

    public void setCompetitorList(List<Competitor> competitorList) {
        this.competitorList = competitorList;
    }
}
