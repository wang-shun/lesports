package com.lesports.sms.data.model.stats;

import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.data.utils.DateFormatter;
import com.lesports.sms.data.utils.GameFTypeFormatter;
import com.lesports.sms.data.utils.MatchStatusFormatter;
import com.lesports.sms.data.utils.MomentFormatter;
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
//public class StatsLiveScore extends CommonLiveScore implements Serializable {
//    @XPath("/sports-statistic/sports-boxscores/@BetradarSportId")
//    private String sportId;
//    @XPath("/gameCode/@global-id")
//    private String partenrId;
//    @XPath("./gamestate/@status-id")
//    private String status;
//    @XPath("./gamestate/@minutes")
//    private String restMinutes;
//    @XPath("./gamestate/@seconds")
//    private String currentSeconds;
//    @XPath("./gamestate/@quarter")
//    private String currentQuarter;
//    @XPath("./home-team/linescore/@score")
//    private String homeScore;
//    @XPath("./home-team/linescore")
//    private Element homeSectiohScores;
//    @XPath("./visiting-team/linescore/@score")
//    private String awayScore;
//    @XPath("./visiting-team/linescore")
//    private Element awaySectiohScore;
//    @XPath("./team-stats")
//    private Element teamStatics;
//    @XPath("./player-stats")
//    private Element playerStatics;
//
//    public String getSportId() {
//        return sportId;
//    }
//
//    public void setSportId(String sportId) {
//        this.sportId = sportId;
//    }
//
//    public String getPartenrId() {
//        return partenrId;
//    }
//
//    public void setPartenrId(String partenrId) {
//        this.partenrId = partenrId;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getRestMinutes() {
//        return restMinutes;
//    }
//
//    public void setRestMinutes(String restMinutes) {
//        this.restMinutes = restMinutes;
//    }
//
//    public String getCurrentSeconds() {
//        return currentSeconds;
//    }
//
//    public void setCurrentSeconds(String currentSeconds) {
//        this.currentSeconds = currentSeconds;
//    }
//
//    public String getCurrentQuarter() {
//        return currentQuarter;
//    }
//
//    public void setCurrentQuarter(String currentQuarter) {
//        this.currentQuarter = currentQuarter;
//    }
//
//    public String getHomeScore() {
//        return homeScore;
//    }
//
//    public void setHomeScore(String homeScore) {
//        this.homeScore = homeScore;
//    }
//
//    public Element getHomeSectiohScores() {
//        return homeSectiohScores;
//    }
//
//    public void setHomeSectiohScores(Element homeSectiohScores) {
//        this.homeSectiohScores = homeSectiohScores;
//    }
//
//    public String getAwayScore() {
//        return awayScore;
//    }
//
//    public void setAwayScore(String awayScore) {
//        this.awayScore = awayScore;
//    }
//
//    public Element getAwaySectiohScore() {
//        return awaySectiohScore;
//    }
//
//    public void setAwaySectiohScore(Element awaySectiohScore) {
//        this.awaySectiohScore = awaySectiohScore;
//    }
//
//    public Element getTeamStatics() {
//        return teamStatics;
//    }
//
//    public void setTeamStatics(Element teamStatics) {
//        this.teamStatics = teamStatics;
//    }
//
//    public Element getPlayerStatics() {
//        return playerStatics;
//    }
//
//    public void setPlayerStatics(Element playerStatics) {
//        this.playerStatics = playerStatics;
//    }
//}
//
//
//
