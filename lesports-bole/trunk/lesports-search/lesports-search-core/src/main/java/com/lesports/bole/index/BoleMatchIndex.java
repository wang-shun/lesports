package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;
import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/14
 */
@Document(indexName = "bole", type = "matches")
public class BoleMatchIndex extends SearchIndex<Long> {
    // 比赛名称
    private String name;
    // 赛程名称
    private String competitionName;
    // 对阵双方名称
    private List<String> competitorNames;
    // 赛季名称
    private String competitionSeasonName;
    // 开始时间
    private String startTime;
    // 状态
    private Integer status;
    // sms id
    private Long smsId;
    // 赛程id
    private Long competitionId;
    // 对阵双方id
    private List<Long> competitorIds;
    // 赛季id
    private Long competitionSeasonId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public List<String> getCompetitorNames() {
        return competitorNames;
    }

    public void setCompetitorNames(List<String> competitorNames) {
        this.competitorNames = competitorNames;
    }

    public String getCompetitionSeasonName() {
        return competitionSeasonName;
    }

    public void setCompetitionSeasonName(String competitionSeasonName) {
        this.competitionSeasonName = competitionSeasonName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSmsId() {
        return smsId;
    }

    public void setSmsId(Long smsId) {
        this.smsId = smsId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public List<Long> getCompetitorIds() {
        return competitorIds;
    }

    public void setCompetitorIds(List<Long> competitorIds) {
        this.competitorIds = competitorIds;
    }

    public Long getCompetitionSeasonId() {
        return competitionSeasonId;
    }

    public void setCompetitionSeasonId(Long competitionSeasonId) {
        this.competitionSeasonId = competitionSeasonId;
    }

    @Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        this.setCompetitorIds(null);
        return true;
    }
}
