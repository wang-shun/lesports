package com.lesports.bole.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/2/17.
 */
@Document(collection = "olympics_setting_datas")
public class BoleOlympicSettingDataSet {
    @Id
    private String id;
    @Field("create_at")
    private String createAt;
    private Boolean deleted = false;
    //修改时间
    @Field("update_at")
    private String updateAt;
    @Field("match_config_data")
    private Set<String> matchExtendConfigData = Sets.newHashSet();
    @Field("player_config_data")
    private Set<String> playerExtendConfigData = Sets.newHashSet();
    @Field("team_config_data")
    private Set<String> teamExtendConfigData = Sets.newHashSet();
    @Field("team_result_condition")
    private Set<String> teamResultConditionData = Sets.newHashSet();
    @Field("team_result_data")
    private Set<String> teamResultData = Sets.newHashSet();
    @Field("player_stats_condition_data")
    private Set<String> playerStatsConfigConditionData = Sets.newHashSet();
    @Field("player_stats_config_data")
    private Set<String> playerStatsConfigData = Sets.newHashSet();
    @Field("team_stats_condition_data")
    private Set<String> competitorStatsConfigConditionData = Sets.newHashSet();
    @Field("team_stats_config_data")
    private Set<String> competitorStatsConfigData = Sets.newHashSet();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public Set<String> getMatchExtendConfigData() {
        return matchExtendConfigData;
    }

    public void setMatchExtendConfigData(Set<String> matchExtendConfigData) {
        this.matchExtendConfigData = matchExtendConfigData;
    }

    public Set<String> getPlayerExtendConfigData() {
        return playerExtendConfigData;
    }

    public void setPlayerExtendConfigData(Set<String> playerExtendConfigData) {
        this.playerExtendConfigData = playerExtendConfigData;
    }

    public Set<String> getTeamExtendConfigData() {
        return teamExtendConfigData;
    }

    public void setTeamExtendConfigData(Set<String> teamExtendConfigData) {
        this.teamExtendConfigData = teamExtendConfigData;
    }

    public Set<String> getPlayerStatsConfigConditionData() {
        return playerStatsConfigConditionData;
    }

    public void setPlayerStatsConfigConditionData(Set<String> playerStatsConfigConditionData) {
        this.playerStatsConfigConditionData = playerStatsConfigConditionData;
    }

    public Set<String> getPlayerStatsConfigData() {
        return playerStatsConfigData;
    }

    public void setPlayerStatsConfigData(Set<String> playerStatsConfigData) {
        this.playerStatsConfigData = playerStatsConfigData;
    }

    public Set<String> getCompetitorStatsConfigConditionData() {
        return competitorStatsConfigConditionData;
    }

    public void setCompetitorStatsConfigConditionData(Set<String> competitorStatsConfigConditionData) {
        this.competitorStatsConfigConditionData = competitorStatsConfigConditionData;
    }

    public Set<String> getCompetitorStatsConfigData() {
        return competitorStatsConfigData;
    }

    public void setCompetitorStatsConfigData(Set<String> competitorStatsConfigData) {
        this.competitorStatsConfigData = competitorStatsConfigData;
    }

    public Set<String> getTeamResultConditionData() {
        return teamResultConditionData;
    }

    public void setTeamResultConditionData(Set<String> teamResultConditionData) {
        this.teamResultConditionData = teamResultConditionData;
    }

    public Set<String> getTeamResultData() {
        return teamResultData;
    }

    public void setTeamResultData(Set<String> teamResultData) {
        this.teamResultData = teamResultData;
    }

    @Override
    public String toString() {
        return "BoleOlympicsConfigDataSet{" +
                "id='" + id + '\'' +
                ", createAt='" + createAt + '\'' +
                ", deleted=" + deleted +
                ", updateAt='" + updateAt + '\'' +
                ", matchExtendConfigData=" + matchExtendConfigData +
                ", playerExtendConfigData=" + playerExtendConfigData +
                ", teamExtendConfigData=" + teamExtendConfigData +
                ", playerStatsConfigData=" + playerStatsConfigData +
                ", competitorStatsConfigData=" + competitorStatsConfigData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoleOlympicSettingDataSet)) return false;

        BoleOlympicSettingDataSet config = (BoleOlympicSettingDataSet) o;

        if (!id.equals(config.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

