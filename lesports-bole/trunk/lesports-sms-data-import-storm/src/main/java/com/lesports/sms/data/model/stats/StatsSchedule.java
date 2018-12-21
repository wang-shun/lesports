package com.lesports.sms.data.model.stats;

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
public class StatsSchedule extends CommonSchedule implements Serializable {
    // sports-statistics/sports-schedule/nba-schedule(**)/game-schedule
    @XPath("/ns2:SportradarData/Sport/@id")
    private String sportId;
    @XPath("/sports-statistics/sports-schedule/league/@global-id")
    private String tourmamentId;
    @XPath("./date/@year")
    private String year;
    @XPath("./date/@month")
    private String month;
    @XPath("./date/@date")
    private String date;
    @XPath("./time/@hour")
    private String hour;
    @XPath("./time/@utc-hour")
    private String utcHour;
    @XPath("./time/@utc-minute")
    private String utcMinute;
    @XPath("./gamecode/@global-id")
    private String partnerId;
    @XPath("./gamecode/@code")
    private String partnerCode;
    @XPath("")
    private String roundNum;
    @XPath("./gametype/@id")
    private String stageId;
    @XPath("./visiting-team")
    private List<MatchTeam> homeTeam;
    @XPath("./home-team")
    private List<MatchTeam> awayTeam;
    @XPath("./visiting-team-score/@score")
    private String visitingScore;
    @XPath("./home-team-score/@score")
    private String homeScore;

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getUtcHour() {
        return utcHour;
    }

    public String getUtcMinute() {
        return utcMinute;
    }

    public String getVisitingScore() {
        return visitingScore;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public static class MatchTeam implements Serializable {
        @XPath("./team-code/@global-id")
        private String teamId;
        @XPath("./team-name/@name")
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


}



