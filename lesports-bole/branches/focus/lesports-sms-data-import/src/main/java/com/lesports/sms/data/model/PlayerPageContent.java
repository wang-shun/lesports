package com.lesports.sms.data.model;

import com.lesports.utils.xml.annotation.XPath;
import org.jdom.Element;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/8/17.
 */
public class PlayerPageContent {
    @XPath("/SoccerFeed/Player/@id")
    private String sodaId;
    @XPath("/SoccerFeed/Player/@weight")
    private Integer weight;
    @XPath("/SoccerFeed/Player/@height")
    private Integer height;
    @XPath("/SoccerFeed/Player/@birthDate")
    private String birthDate;
    @XPath("/SoccerFeed/Player/@name")
    private String name;
    @XPath("/SoccerFeed/Player/@nameEng")
    private String engLishName;
    @XPath("/SoccerFeed/Player/Position/@name")
    private String positionName;
    @XPath("/SoccerFeed/Player/Team/@id")
    private String currentClubId;
    @XPath("/SoccerFeed/Player/Team/@number")
    private String currentClubNumber;
    @XPath("/SoccerFeed/Player/Team/@name")
    private String currentClubName;
    @XPath("/SoccerFeed/Player/National/@id")
    private String currentNationalId;
    @XPath("/SoccerFeed/Player/National/@number")
    private String currentNationalNumber;
    @XPath("/SoccerFeed/Player/National/@name")
    private String currentNationalName;
    @XPath("/SoccerFeed/Player/Career/Team")
    private List<Career> careers;
    @XPath("/SoccerFeed/Player/Statistics/National")
    private List<Element> CurrentNationalStats;
    @XPath("/SoccerFeed/Player/Statistics/National/Stat")
    private List<Element> nationalStats;
    @XPath("/SoccerFeed/Player/Statistics/Team")
    private List<Element> CurrentClubStats;
    @XPath("/SoccerFeed/Player/Statistics/Team/Stat")
    private List<Element> clubStats;

    public static class Career {
        @XPath("./@season")
        private String season;
        @XPath("./@id")
        private String clubId;

        public String getClubId() {
            return clubId;
        }

        public void setClubId(String clubId) {
            this.clubId = clubId;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getEngLishName() {
        return engLishName;
    }

    public void setEngLishName(String engLishName) {
        this.engLishName = engLishName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getSodaId() {
        return sodaId;
    }

    public void setSodaId(String sodaId) {
        this.sodaId = sodaId;
    }

    public List<Element> getClubStats() {
        return clubStats;
    }

    public void setClubStats(List<Element> clubStats) {
        this.clubStats = clubStats;
    }

    public List<Element> getNationalStats() {
        return nationalStats;
    }

    public void setNationalStats(List<Element> nationalStats) {
        this.nationalStats = nationalStats;
    }

    public List<Career> getCareers() {
        return careers;
    }

    public void setCareers(List<Career> careers) {
        this.careers = careers;
    }

    public String getCurrentClubId() {
        return currentClubId;
    }

    public void setCurrentClubId(String currentClubId) {
        this.currentClubId = currentClubId;
    }

    public String getCurrentClubNumber() {
        return currentClubNumber;
    }

    public void setCurrentClubNumber(String currentClubNumber) {
        this.currentClubNumber = currentClubNumber;
    }

    public List<Element> getCurrentNationalStats() {
        return CurrentNationalStats;
    }

    public void setCurrentNationalStats(List<Element> currentNationalStats) {
        CurrentNationalStats = currentNationalStats;
    }

    public List<Element> getCurrentClubStats() {
        return CurrentClubStats;
    }

    public void setCurrentClubStats(List<Element> currentClubStats) {
        CurrentClubStats = currentClubStats;
    }

    public String getCurrentClubName() {
        return currentClubName;
    }

    public String getCurrentNationalId() {
        return currentNationalId;
    }

    public String getCurrentNationalNumber() {
        return currentNationalNumber;
    }

    public String getCurrentNationalName() {
        return currentNationalName;
    }
}

