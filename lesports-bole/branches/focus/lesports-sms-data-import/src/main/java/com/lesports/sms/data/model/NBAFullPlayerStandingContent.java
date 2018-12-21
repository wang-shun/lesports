package com.lesports.sms.data.model;

import com.lesports.utils.xml.annotation.XPath;
import org.jdom.Element;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/8/17.
 */
public class NBAFullPlayerStandingContent {
    @XPath("/sports-statistics/sports-rankings/league/@alias")
    private String cName;
    @XPath("/sports-statistics/sports-rankings/season/@season")
    private String season;
    @XPath("/sports-statistics/sports-rankings/nba-leaders/nba-player-rankings/category")
    private List<rankingItem> rankingItems;


    public static class rankingItem {
        @XPath("./@id")
        private String id;
        @XPath("./@category-heading")
        private String category;
        @XPath("./ranking")
        List<PlayerRankingItem> playerRankingItems;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public List<PlayerRankingItem> getPlayerRankingItems() {
            return playerRankingItems;
        }

        public void setPlayerRankingItems(List<PlayerRankingItem> playerRankingItems) {
            this.playerRankingItems = playerRankingItems;
        }
    }


    public static class PlayerRankingItem {
        @XPath("./@global-id")
        private String playerId;
        @XPath("./@global-team-id")
        private String teamId;
        @XPath("./@ranking")
        private Integer ranking;
        @XPath("./@stat")
        private String stats;

        public String getPlayerId() {
            return playerId;
        }

        public void setPlayerId(String playerId) {
            this.playerId = playerId;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public Integer getRanking() {
            return ranking;
        }

        public void setRanking(Integer ranking) {
            this.ranking = ranking;
        }

        public String getStats() {
            return stats;
        }

        public void setStats(String stats) {
            this.stats = stats;
        }

    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public List<rankingItem> getRankingItems() {
        return rankingItems;
    }

    public void setRankingItems(List<rankingItem> rankingItems) {
        this.rankingItems = rankingItems;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}

