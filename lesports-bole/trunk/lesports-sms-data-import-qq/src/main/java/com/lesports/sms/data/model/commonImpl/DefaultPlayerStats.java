package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.formatter.QQ.QQPlayerAvgStatsFormatter;
import com.lesports.sms.data.util.formatter.QQ.QQPlayerStatsFormatter;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
//http://ziliaoku.sports.qq.com/cube/index?cubeId=10&dimId=9&params=t2:2016|t3:1|t1:3704&from=sportsdatabase
public class DefaultPlayerStats extends DefaultModel {
    @JsonPathQQ(value = "$.data.nbaPlayerSeasonStat")
    private List<PlayerStats> playerStatsList;

    public static class PlayerStats {
        @JsonPathQQ(value = "$.playerId")
        private String partnerId;
        @JsonPathQQ(value = "$")
        private List<StatT> MainStats;
        private List<StatT> subStats;

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }


        public void setMainStats(List<StatT> mainStats) {
            MainStats = mainStats;
        }

        public void setSubStats(List<StatT> subStats) {
            this.subStats = subStats;
        }

        public List<StatT> getSubStats() {
            return subStats;
        }

        public List<StatT> getMainStats() {
            return MainStats;
        }

        public static class StatT {
            private Long cid;
            private String season;
            @JsonPathQQ(formatter = QQPlayerStatsFormatter.class, value = "$")
            private Map<String, Object> elementStats;
            @JsonPathQQ(formatter = QQPlayerAvgStatsFormatter.class, value = "$")
            private Map<String, Object> elementAvgStats;

            public String getSeason() {
                return season;
            }

            public void setSeason(String season) {
                this.season = season;
            }

            public Long getCid() {
                return cid;
            }

            public void setCid(Long cid) {
                this.cid = cid;
            }

            public Map<String, Object> getElementStats() {
                return elementStats;
            }

            public void setElementStats(Map<String, Object> elementStats) {
                this.elementStats = elementStats;
            }

            public Map<String, Object> getElementAvgStats() {
                return elementAvgStats;
            }

            public void setElementAvgStats(Map<String, Object> elementAvgStats) {
                this.elementAvgStats = elementAvgStats;
            }
        }

    }


    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (PlayerStats model : playerStatsList) {
            res.add(model);
        }
        return res;
    }
}
