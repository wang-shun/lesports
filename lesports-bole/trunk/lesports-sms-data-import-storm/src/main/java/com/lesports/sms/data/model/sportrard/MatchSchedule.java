package com.lesports.sms.data.model.sportrard;

import com.lesports.sms.data.model.CommonSchedule;
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
public class MatchSchedule extends CommonSchedule implements Serializable  {

    @XPath("/ns2:SportradarData/Sport/@id")
    @Formatter(GameFTypeFormatter.class)
    private Long sportId;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@uniqueTournamentId")
    private String tourmamentId;
    @XPath("./@dateOfMatch")
    @Formatter(DateFormatter.class)
    private String startTime;
    @XPath("./@id")
    private String partnerId;
    @XPath("./Roundinfo/@round")
    private String roundNum;
    @XPath("./Roundinfo/@roundname")
    private String roundName;
    @XPath("./Teams/Team[@type='1']")
    private List<MatchTeam> homeTeam;
    @XPath("./Teams/Team[@type='2']")
    private List<MatchTeam> awayTeam;
    @XPath("./Result")
    private List<MatchResult> MatchResult;

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
        return roundName;
    }
    public List<MatchTeam> getHomeTeam() {
        return homeTeam;
    }
    public List<MatchTeam> getAwayTeam() {
        return awayTeam;
    }

    public List<MatchSchedule.MatchResult> getMatchResult() {
        return MatchResult;
    }

    public static class MatchExtenedTeam implements Serializable {
        @XPath("./@id")
        private String teamId;
        @XPath("./@name")
        private String teamName;

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
    }

    public static class MatchResult implements Serializable {
        @XPath("./@canceled")
        private boolean isCanceled;
        @XPath("./@postponed")
        private boolean isPostponed;
        @XPath("./Score[@type='FT']/@value")
        private String firstScore;
        private String newPartnerId;

        public boolean isCanceled() {
            return isCanceled;
        }

        public void setCanceled(boolean isCanceled) {
            this.isCanceled = isCanceled;
        }

        public boolean isPostponed() {
            return isPostponed;
        }

        public void setPostponed(boolean isPostponed) {
            this.isPostponed = isPostponed;
        }

        public String getFirstScore() {
            return firstScore;
        }

        public void setFirstScore(String firstScore) {
            this.firstScore = firstScore;
        }

        public String getNewPartnerId() {
            return newPartnerId;
        }

        public void setNewPartnerId(String newPartnerId) {
            this.newPartnerId = newPartnerId;
        }
    }

}



