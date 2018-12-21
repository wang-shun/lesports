package com.lesports.sms.data.model.stats;

import com.lesports.sms.data.utils.DateFormatter;
import com.lesports.sms.data.utils.GameFTypeFormatter;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author qiaohngxin
 * @since 16-4-18
 */
public class StatsTeamStanding implements Serializable {

    @XPath("/ns2:SportradarData/Sport/@id")
    private Long sportId;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@uniqueTournamentId")
    private String tourmamentId;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@uniqueTournamentId")
    private String ConferenceName;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@uniqueTournamentId")
    private List<DivisionStanding> divisionStandings;

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

    public String getConferenceName() {
        return ConferenceName;
    }

    public List<DivisionStanding> getDivisionStandings() {
        return divisionStandings;
    }

    public static class DivisionStanding implements Serializable {
        @XPath("./@division")
        private String divisionName;
        @XPath("./nba-team-standings")
        List<TeamStanding> teamStandings;

        public String getDivisionName() {
            return divisionName;
        }

        public List<TeamStanding> getTeamStandings() {
            return teamStandings;
        }

    }

    public static class TeamStanding implements Serializable {
        @XPath("./team-code/@id")
        private String teamId;
        @XPath("./@postponed")
        private Integer divisionRank;
        @XPath("./Score[@type='FT']/@value")
        private Integer conferenceRank;
        private Element stats;

        public String getTeamId() {
            return teamId;
        }

        public Integer getDivisionRank() {
            return divisionRank;
        }

        public Integer getConferenceRank() {
            return conferenceRank;
        }

        public Element getStats() {
            return stats;
        }
    }

}



