package com.lesports.bole.model;

import com.google.common.collect.Lists;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/2/17.
 */
@Document(collection = "olympics_configs")
public class BoleOlympicsLiveConfigSet {
    @Id
    private Long id;
    @Field("create_at")
    private String createAt;
    private Boolean deleted = false;
    //修改时间
    @Field("update_at")
    private String updateAt;
    @Field("match_config")
    private List<OlympicsConfig> matchExtendConfig = Lists.newArrayList();
    @Field("player_config")
    private List<OlympicsConfig> playerExtendConfig = Lists.newArrayList();
    @Field("team_config")
    private List<OlympicsConfig> teamExtendConfig = Lists.newArrayList();
    @Field("team_section_result")
    private List<OlympicsConfig> teamSectionResultConfig = Lists.newArrayList();
    @Field("player_stats_config")
    private List<OlympicsConfig> playerStatsConfig = Lists.newArrayList();
    @Field("team_stats_config")
    private List<OlympicsConfig> competitorStatsConfig = Lists.newArrayList();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public List<OlympicsConfig> getMatchExtendConfig() {
        return matchExtendConfig;
    }

    public void setMatchExtendConfig(List<OlympicsConfig> matchExtendConfig) {
        this.matchExtendConfig = matchExtendConfig;
    }

    public List<OlympicsConfig> getPlayerExtendConfig() {
        return playerExtendConfig;
    }

    public void setPlayerExtendConfig(List<OlympicsConfig> playerExtendConfig) {
        this.playerExtendConfig = playerExtendConfig;
    }

    public List<OlympicsConfig> getTeamExtendConfig() {
        return teamExtendConfig;
    }

    public void setTeamExtendConfig(List<OlympicsConfig> teamExtendConfig) {
        this.teamExtendConfig = teamExtendConfig;
    }

    public List<OlympicsConfig> getPlayerStatsConfig() {
        return playerStatsConfig;
    }

    public void setPlayerStatsConfig(List<OlympicsConfig> playerStatsConfig) {
        this.playerStatsConfig = playerStatsConfig;
    }

    public List<OlympicsConfig> getTeamSectionResultConfig() {
        return teamSectionResultConfig;
    }

    public void setTeamSectionResultConfig(List<OlympicsConfig> teamSectionResultConfig) {
        this.teamSectionResultConfig = teamSectionResultConfig;
    }

    public List<OlympicsConfig> getCompetitorStatsConfig() {
        return competitorStatsConfig;
    }

    public void setCompetitorStatsConfig(List<OlympicsConfig> competitorStatsConfig) {
        this.competitorStatsConfig = competitorStatsConfig;
    }


    public static class OlympicsConfig {
        @Field("element_path")
        private String elementPath;
        @Field("right_element_path")
        private String rightElementPath;
        @Field("op")
        private String op;
        @Field("attribute_re_name")
        private String attributeReName;
        @Field("position_key")
        private String positionKey;
        @Field("formatter_type")
        private String formatterType;

        public String getElementPath() {
            return elementPath;
        }

        public void setElementPath(String elementPath) {
            this.elementPath = elementPath;
        }

        public String getAttributeReName() {
            return attributeReName;
        }

        public void setAttributeReName(String attributeReName) {
            this.attributeReName = attributeReName;
        }

        public String getPositionKey() {
            return positionKey;
        }

        public void setPositionKey(String positionKey) {
            this.positionKey = positionKey;
        }

        public String getFormatterType() {
            return formatterType;
        }

        public void setFormatterType(String formatterType) {
            this.formatterType = formatterType;
        }

        public String getRightElementPath() {
            return rightElementPath;
        }

        public void setRightElementPath(String rightElementPath) {
            this.rightElementPath = rightElementPath;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }
    }

    @Override
    public String toString() {
        return "OlympicLiveConfigSet{" +
                "id=" + id +
                ", matchExtendConfig=" + matchExtendConfig +
                ", playerExtendConfig=" + playerExtendConfig +
                ", teamExtendConfig=" + teamExtendConfig +
                ", playerStatsConfig=" + playerStatsConfig +
                ", competitorStatsConfig=" + competitorStatsConfig +
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoleOlympicsLiveConfigSet)) return false;

        BoleOlympicsLiveConfigSet config = (BoleOlympicsLiveConfigSet) o;

        if (!id.equals(config.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

