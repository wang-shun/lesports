package com.lesports.sms.data.model.sportrard;

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
public class SportrardPlayerStanding implements Serializable {

    @XPath("/ns2:SportradarData/Sport/@id")
    private String sportId;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@uniqueTournamentId")
    private String tourmamentId;
    @XPath("./Team")
    private List<TeamPlayerStanding> teamDatas;

    public List<TeamPlayerStanding> getTeamDatas() {
        return teamDatas;
    }

    public String getTourmamentId() {
        return tourmamentId;
    }

    public String getSportId() {
        return sportId;
    }

    public static class TeamPlayerStanding implements Serializable {
        @XPath("./@id")
        private String teamId;
        @XPath("./GoalScorers/Player")//TODO@XPath("./Assist/Player")
        private List<PlayerData> playerDatas;

        public String getTeamId() {
            return teamId;
        }

        public List<PlayerData> getPlayerDatas() {
            return playerDatas;
        }
    }

    public static class PlayerData implements Serializable {
        @XPath("./@playerID")
        private String playerId;
        @XPath("./@playerEnName")
        private String enName;
        @XPath("./Translation[@key='playerName']/TranslatedValue[@lang='zh']/@value")
        private String name;
        @XPath("./@PlayerStats/PlayerStatsEntry")
        private List<PlayerStatsEntry> statsEntryData;

        public String getPlayerId() {
            return playerId;
        }

        public String getEnName() {
            return enName;
        }

        public String getName() {
            return name;
        }

        public List<PlayerStatsEntry> getStatsEntryData() {
            return statsEntryData;
        }
    }

    public static class PlayerStatsEntry implements Serializable {
        @XPath("./@id")
        private String key;
        @XPath("./@value")
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}



