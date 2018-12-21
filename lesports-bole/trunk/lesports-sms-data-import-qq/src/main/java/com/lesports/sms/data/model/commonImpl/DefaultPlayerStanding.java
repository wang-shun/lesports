package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.formatter.QQ.QQPlayerStandingStatsFormatter;
import com.lesports.sms.data.util.formatter.QQ.QQPlayerStandingTypeFormatter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/5/12.
 */
public class DefaultPlayerStanding extends DefaultModel implements Serializable {
    @JsonPathQQ(value = "$.data.*")
    private List<PlayerStandings> playerStandingses;

    public static class PlayerStandings {
        @JsonPathQQ(formatter = QQPlayerStandingTypeFormatter.class, value = "$")
        private Long type;
        @JsonPathQQ(value = "$.*")
        private List<PlayerStandingItem> items;

        public Long getType() {
            return type;
        }

        public void setType(Long type) {
            this.type = type;
        }

        public List<PlayerStandingItem> getItems() {
            return items;
        }

        public void setItems(List<PlayerStandingItem> items) {
            this.items = items;
        }
    }

    public static class PlayerStandingItem {
        @JsonPathQQ(value = "$.playerId")
        private String PartnerId;
        private Integer rank;
        @JsonPathQQ(formatter = QQPlayerStandingStatsFormatter.class, value = "$")
        private Map<String, Object> stats;

        public String getPartnerId() {
            return PartnerId;
        }

        public void setPartnerId(String partnerId) {
            PartnerId = partnerId;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public void setStats(Map<String, Object> stats) {
            this.stats = stats;
        }

        public Map<String, Object> getStats() {
            return stats;
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (PlayerStandings model : playerStandingses) {
            res.add(model);
        }
        return res;
    }

}
