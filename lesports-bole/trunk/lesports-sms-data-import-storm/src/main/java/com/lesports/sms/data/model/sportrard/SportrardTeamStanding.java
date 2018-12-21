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
public class SportrardTeamStanding implements Serializable {
    // /ns2:SportradarData/Sport/Category/Tournament/LeagueTable/LeagueTableRows/LeagueTableRow
    @XPath("/ns2:SportradarData/Sport/@id")
    private String sportId;
    @XPath("/ns2:SportradarData/Sport/Category/Tournament/@uniqueTournamentId")
    private String tourmamentId;
    @XPath("./LeagueTableRow")
    private List<LeagueTableRow> rows;

    public String getSportId() {
        return sportId;
    }

    public String getTourmamentId() {
        return tourmamentId;
    }

    public List<LeagueTableRow> getRows() {
        return rows;
    }

    public static class LeagueTableColumn implements Serializable {
        @XPath("./@key")
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

    public static class LeagueTableRow implements Serializable {
        @XPath("./Team/@id")
        private String teamId;
        @XPath("./LeagueTableColumn[@key='positionTotal']/@value")
        private Integer rank;
        @XPath("./LeagueTableColumn")
        private List<LeagueTableColumn> columns;

        public String getTeamId() {
            return teamId;
        }

        public Integer getRank() {
            return rank;
        }

        public List<LeagueTableColumn> getColumns() {
            return columns;
        }
    }
}




