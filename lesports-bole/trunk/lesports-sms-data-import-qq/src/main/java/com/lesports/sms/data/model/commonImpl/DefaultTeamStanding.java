package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.formatter.QQ.*;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/5/12.
 */
public class DefaultTeamStanding extends DefaultModel {
    @JsonPathQQ(value = "$[1].*")
    private List<TeamStanding> standings;

    public static class TeamStanding {
        @JsonPathQQ(formatter = QQTeamStandingScopeTypeFormatter.class, value = "$")
        private ScopeType scopeType;
        @JsonPathQQ(formatter = QQTeamStandingScopeIdFormatter.class, value = "$")
        private Long scopeId;
        @JsonPathQQ(formatter = QQTeamStandingTypeFormatter.class, value = "$")
        private Long type;
        @JsonPathQQ(value="$.*")
        private List<TeamStandingItem> items;

        public ScopeType getScopeType() {
            return scopeType;
        }

        public void setScopeType(ScopeType scopeType) {
            this.scopeType = scopeType;
        }

        public Long getScopeId() {
            return scopeId;
        }

        public void setScopeId(Long scopeId) {
            this.scopeId = scopeId;
        }

        public List<TeamStandingItem> getItems() {
            return items;
        }

        public void setItems(List<TeamStandingItem> items) {
            this.items = items;
        }

        public Long getType() {
            return type;
        }

        public void setType(Long type) {
            this.type = type;
        }

        public static class TeamStandingItem {
            @JsonPathQQ(value = "$.teamId")
            private String teamId;
            @JsonPathQQ(value = "$.serial")
            public Integer rank;
            @JsonPathQQ(formatter = QQTeamStandingStatsFormatter.class, value = "$")
            public Map<String, Object> teamStats;


            public String getTeamId() {
                return teamId;
            }

            public void setTeamId(String teamId) {
                this.teamId = teamId;
            }

            public Integer getRank() {
                return rank;
            }

            public void setRank(Integer rank) {
                this.rank = rank;
            }

            public Map<String, Object> getTeamStats() {
                return teamStats;
            }

            public void setTeamStats(Map<String, Object> teamStats) {
                this.teamStats = teamStats;
            }
        }
    }


    public List<TeamStanding> getStandings() {
        return standings;
    }

    public void setStandings(List<TeamStanding> standings) {
        this.standings = standings;
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (TeamStanding model : standings) {
            res.add(model);
        }
        return res;
    }

}
