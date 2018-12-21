package com.lesports.sms.data.model.sportrard;

import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.data.utils.*;
import com.lesports.utils.xml.annotation.Formatter;
import com.lesports.utils.xml.annotation.XPath;
import org.dom4j.Element;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/4/18.
 */
//public class MatchLiveScore extends CommonLiveScore{
//    @XPath("/BetradarLiveScoreData/generateAt")
//    @Formatter(DateFormatter.class)
//    private String betradarTime;
//    @XPath("./CurrentPeriodStart/text()")
//    @Formatter(DateFormatter.class)
//    private String currentPeriodStartTime;
//    @XPath("/BetradarLiveScoreData/Sport/@BetradarSportId")
//    private String sportId;
//    @XPath("./@Id")
//    private String partenrId;
//    @XPath("./Status/@Code")
//    private String status;
//    @XPath("./Status/@Code")
//    @Formatter(MomentFormatter.class)
//    private String moment;
//    @XPath("./Scores/Score[@type='Current']/Team1/text()")
//    private String homeScore;
//    @XPath("./Scores/Score[@type='Current']/Team2/text()")
//    private String awayScore;
//    @XPath("./Scores")
//    private Element scores;
//    @XPath("./Statistics")
//    private Element statistics;
//
//    @XPath("./Goals/Goal")
//    private List<Goal> gaolActions;
//    @XPath("./Cards/Card")
//    private List<Card> cardActions;
//    @XPath("./Substitutions/Substitution")
//    private List<Substitution> substitutionActions;
//    @XPath("./Team1/@UniqueTeamId")
//    private String hometeamId;
//    @XPath("./Team1/@Formation")
//    private String homeFormation;
//    @XPath("./Lineups/TeamPlayer[PlayerTeam=1]")
//    private List<TeamPlayer> homePlayerList;
//    @XPath("./Team2/@UniqueTeamId")
//    private String awayteamId;
//    @XPath("./Team2/@Formation")
//    private String awayFormation;
//
//    public Element getStatistics() {
//        return statistics;
//    }
//
//    public void setStatistics(Element statistics) {
//        this.statistics = statistics;
//    }
//
//    public Element getScores() {
//        return scores;
//    }
//
//    public void setScores(Element scores) {
//        this.scores = scores;
//    }
//
//    @XPath("./Lineups/TeamPlayer[PlayerTeam=2]")
//
//    private List<TeamPlayer> awayPlayerList;
//
//    public List<TeamPlayer> getAwayPlayerList() {
//        return awayPlayerList;
//    }
//
//    public String getAwayteamId() {
//        return awayteamId;
//    }
//
//    public List<TeamPlayer> getHomePlayerList() {
//        return homePlayerList;
//    }
//
//    public String getHometeamId() {
//        return hometeamId;
//    }
//
//    public String getPartenrId() {
//        return partenrId;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public String getHomeScore() {
//        return homeScore;
//    }
//
//    public String getAwayScore() {
//        return awayScore;
//    }
//
//    public List<Goal> getGaolActions() {
//        return gaolActions;
//    }
//
//    public List<Card> getCardActions() {
//        return cardActions;
//    }
//
//    public List<Substitution> getSubstitutionActions() {
//        return substitutionActions;
//    }
//
//    public String getBetradarTime() {
//        return betradarTime;
//    }
//
//    public void setBetradarTime(String betradarTime) {
//        this.betradarTime = betradarTime;
//    }
//
//    public String getCurrentPeriodStartTime() {
//        return currentPeriodStartTime;
//    }
//
//    public void setCurrentPeriodStartTime(String currentPeriodStartTime) {
//        this.currentPeriodStartTime = currentPeriodStartTime;
//    }
//
//    public String getSportId() {
//        return sportId;
//    }
//
//    public void setSportId(String sportId) {
//        this.sportId = sportId;
//    }
//
//    public void setPartenrId(String partenrId) {
//        this.partenrId = partenrId;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getMoment() {
//        return moment;
//    }
//
//    public void setMoment(String moment) {
//        this.moment = moment;
//    }
//
//    public void setHomeScore(String homeScore) {
//        this.homeScore = homeScore;
//    }
//
//    public void setAwayScore(String awayScore) {
//        this.awayScore = awayScore;
//    }
//
//    public void setGaolActions(List<Goal> gaolActions) {
//        this.gaolActions = gaolActions;
//    }
//
//    public void setCardActions(List<Card> cardActions) {
//        this.cardActions = cardActions;
//    }
//
//    public void setSubstitutionActions(List<Substitution> substitutionActions) {
//        this.substitutionActions = substitutionActions;
//    }
//
//    public void setHometeamId(String hometeamId) {
//        this.hometeamId = hometeamId;
//    }
//
//    public void setHomePlayerList(List<TeamPlayer> homePlayerList) {
//        this.homePlayerList = homePlayerList;
//    }
//
//    public void setAwayteamId(String awayteamId) {
//        this.awayteamId = awayteamId;
//    }
//
//    public void setAwayPlayerList(List<TeamPlayer> awayPlayerList) {
//        this.awayPlayerList = awayPlayerList;
//    }
//
//    public String getHomeFormation() {
//        return homeFormation;
//    }
//
//    public void setHomeFormation(String homeFormation) {
//        this.homeFormation = homeFormation;
//    }
//
//    public String getAwayFormation() {
//        return awayFormation;
//    }
//
//    public void setAwayFormation(String awayFormation) {
//        this.awayFormation = awayFormation;
//    }
//
//    public static class TeamPlayer {
//        @XPath("./Player/@id")
//        private String playerId;
//        @XPath("./Player/@name")
//        private String playerName;
//        @XPath("./ShirtNumber/text()")
//        private Integer shirtNumber;
//        @XPath("./Substitute/text")
//        private boolean isSubstitte;
//        @XPath("./@pos")
//        private String pos;
//        @XPath("./@pos")
//        @Formatter(PositionFormatter.class)
//        private Long position;
//
//        public String getPlayerId() {
//            return playerId;
//        }
//
//        public void setPlayerId(String playerId) {
//            this.playerId = playerId;
//        }
//
//        public String getPlayerName() {
//            return playerName;
//        }
//
//        public void setPlayerName(String playerName) {
//            this.playerName = playerName;
//        }
//
//        public Integer getShirtNumber() {
//            return shirtNumber;
//        }
//
//        public void setShirtNumber(Integer shirtNumber) {
//            this.shirtNumber = shirtNumber;
//        }
//
//        public boolean isSubstitte() {
//            return isSubstitte;
//        }
//
//        public void setSubstitte(boolean isSubstitte) {
//            this.isSubstitte = isSubstitte;
//        }
//
//        public String getPos() {
//            return pos;
//        }
//
//        public void setPos(String pos) {
//            this.pos = pos;
//        }
//
//        public Long getPosition() {
//            return position;
//        }
//
//        public void setPosition(Long position) {
//            this.position = position;
//        }
//    }
//
//    public static class Goal {
//        @XPath("./PlayerTeam/text()")
//        private String team;
//        @XPath("./Time/text()")
//        private String time;
//        @XPath("./@from")
//        private String goalType;
//        @XPath("./Player/@id")
//        private String playerId;
//
//        public String getTeam() {
//            return team;
//        }
//
//        public String getTime() {
//            return time;
//        }
//
//        public String getGoalType() {
//            return goalType;
//        }
//
//        public String getPlayerId() {
//            return playerId;
//        }
//    }
//
//    public static class Card {
//        @XPath("./PlayerTeam/text()")
//        private String team;
//        @XPath("./@type")
//        private String cardType;
//        @XPath("./Time/text()")
//        private String time;
//        @XPath("./Player/@id")
//        private String playerId;
//
//        public String getTeam() {
//            return team;
//        }
//
//        public String getCardType() {
//            return cardType;
//        }
//
//        public String getTime() {
//            return time;
//        }
//
//        public String getPlayerId() {
//            return playerId;
//        }
//    }
//
//    public static class Substitution {
//        @XPath("./PlayerTeam/text()")
//        private String team;
//        @XPath("./Time/text()")
//        private String time;
//        @XPath("./PlayerOut/@id")
//        private String outPlayerId;
//        @XPath("./PlayerIn/@id")
//        private String inPlayerId;
//
//        public String getTeam() {
//            return team;
//        }
//
//        public String getTime() {
//            return time;
//        }
//
//        public String getOutPlayerId() {
//            return outPlayerId;
//        }
//
//        public String getInPlayerId() {
//            return inPlayerId;
//        }
//    }
//}
