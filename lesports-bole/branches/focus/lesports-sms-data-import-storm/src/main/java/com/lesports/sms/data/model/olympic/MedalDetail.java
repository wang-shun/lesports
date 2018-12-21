package com.lesports.sms.data.model.olympic;

import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/3/11.
 */
public class MedalDetail implements Serializable {
    @XPath("/OdfBody/Competition/Discipline/@Code")
    private String disciplineCode;
    @XPath("../@Code")
    private String genderCode;
    @XPath("./@Code")
    private String eventCode;
    @XPath("./Medal")
    List<EventMedal> medalList;

    public static class EventMedal implements Serializable {
        @XPath("./@Code")
        private String medalCode;
        @XPath("./Competitor/@Code")
        private String competitorCode;
        @XPath("./Competitor/@Type")
        private String competitorType;
        @XPath("./Competitor/@Organisation")
        private String country;

        public String getMedalCode() {
            return medalCode;
        }

        public void setMedalCode(String medalCode) {
            this.medalCode = medalCode;
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

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public String getDisciplineCode() {
        return disciplineCode;
    }

    public void setDisciplineCode(String disciplineCode) {
        this.disciplineCode = disciplineCode;
    }

    public List<EventMedal> getMedalList() {
        return medalList;
    }

    public void setMedalList(List<EventMedal> medalList) {
        this.medalList = medalList;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }
}
