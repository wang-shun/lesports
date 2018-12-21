package com.lesports.sms.data.model;

import com.lesports.sms.data.utils.DateFormatter;
import com.lesports.sms.data.utils.GameFTypeFormatter;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author qiaohngxin
 * @since 16-4-18
 */
public class CommonSchedule implements Serializable {
    private Long sportId;
    private String tourmamentId;
    private String startTime;
    private String endTime;
    private String partnerId;
    private String partnerCode;
    private String roundNum;
    private String stageName;
    private String status;
    @XPath("./@canceled")
    private boolean isCanceled;
    @XPath("./@postponed")
    private boolean isPostponed;
    private List<MatchTeam> homeTeam;
    private List<MatchTeam> awayTeam;


    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getTourmamentId() {
        return tourmamentId;
    }

    public void setTourmamentId(String tourmamentId) {
        this.tourmamentId = tourmamentId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(String roundNum) {
        this.roundNum = roundNum;
    }

    public String getRoundName() {
        return roundNum;
    }

    public List<MatchTeam> getHomeTeam() {
        return homeTeam;
    }

    public List<MatchTeam> getAwayTeam() {
        return awayTeam;
    }

    public static class MatchTeam implements Serializable {
        @XPath("./@id")
        private String teamId;
        @XPath("./@name")
        private String teamName;
        @XPath("./@name")
        private String score;


        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }



}



