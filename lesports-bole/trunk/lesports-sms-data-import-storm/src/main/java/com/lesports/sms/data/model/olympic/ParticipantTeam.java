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
public class ParticipantTeam implements Serializable {
    @XPath("/OdfBody/Competition/@code")
    private String competitionCode;
    @XPath("/OdfBody/@DocumentCode")
    private String sportType;
    @XPath("./@Code")
    private String code;
    @XPath("./@Name")
    private String name;
    @XPath("./@Organisation")
    private String organisation;
    @XPath("./Composition/Athlete")
    private List<TeamPlayer> players;

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public List<TeamPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<TeamPlayer> players) {
        this.players = players;
    }

    public static class TeamPlayer implements Serializable {
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
}
