package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.formatter.QQ.QQTeamAvgStatsFormatter;
import com.lesports.sms.data.util.formatter.QQ.QQTeamStatsFormatter;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultTeamStats extends DefaultModel {
@JsonPathQQ(value="$.data.nbaTeamSeasonStat[0]")
    private List<TeamStat> teamStatList;

    public static class TeamStat {
     @JsonPathQQ(value="$.teamId")
        private String PartnerId;
    @JsonPathQQ(value="$")
        private List<StatT> statTList;

        public List<StatT> getStatTList() {
            return statTList;
        }

        public void setStatTList(List<StatT> statTList) {
            this.statTList = statTList;
        }

        public String getPartnerId() {
            return PartnerId;
        }

        public void setPartnerId(String partnerId) {
            PartnerId = partnerId;
        }
    }

    public static class StatT {
        private Long cid;
        private String season;
        @JsonPathQQ(formatter = QQTeamStatsFormatter.class,value="$")
        private Map<String, Object> stats;
        @JsonPathQQ(formatter = QQTeamAvgStatsFormatter.class,value="$")
        private Map<String, Object> avgStats;

        public void setStats(Map<String, Object> stats) {
            this.stats = stats;
        }

        public Map<String, Object> getStats() {
            return stats;
        }

        public Long getCid() {
            return cid;
        }

        public void setCid(Long cid) {
            this.cid = cid;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (TeamStat model : teamStatList) {
            res.add(model);
        }
        return res;
    }
}
