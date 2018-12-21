package com.lesports.bole.model;

import com.google.common.collect.Lists;
import com.lesports.crawler.model.source.Source;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * Bole比赛
 * 
 * @author denghui
 *
 */
@Document(collection = "bole_matches")
public class BoleMatch implements Serializable {
    private static final long serialVersionUID = 231199301461326219L;
    @Id
    private Long id;
    //名称
    private String name;
    // Bole赛季ID
    private Long csid;
    // Bole赛事ID
    private Long cid;
    // 大项
    @Field("game_f_type")
    private Long gameFType;
    // Bole对阵方ID
    private List<Long> competitors = Lists.newArrayList();
    // 比赛开始日期:yyyyMMdd
    @Field("start_date")
    private String startDate;
    // 比赛开始时间
    @Field("start_time")
    @Indexed(name = "start_time_-1", direction = IndexDirection.DESCENDING)
    private String startTime;
    // 轮次
    private Integer round;
    // Bole状态
    private BoleStatus status;
    // 标识新建赛事的来源
    private Source source;
    @Field("source_match_id")
    private String sourceMatchId;
    // 比赛在SMS中的ID
    @Field("sms_id")
    @Indexed(name = "sms_id_1")
    private Long smsId = Long.valueOf(0);
    @Field("attach_id")
    private Long attachId = Long.valueOf(0);
    @Field("mapping_id")
    private Long mappingId = Long.valueOf(0);
    // 创建时间
    @Field("create_at")
    private String createAt;
    // 更新时间
    @Field("update_at")
    private String updateAt;

    public Long getAttachId() {
        return attachId;
    }

    public void setAttachId(Long attachId) {
        this.attachId = attachId;
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCsid() {
        return csid;
    }

    public void setCsid(Long csid) {
        this.csid = csid;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public List<Long> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<Long> competitors) {
        this.competitors = competitors;
    }

    public void addCompetitor(Long competitor) {
        if (competitor != null) {
            this.competitors.add(competitor);
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public BoleStatus getStatus() {
        return status;
    }

    public void setStatus(BoleStatus status) {
        this.status = status;
    }

    public Long getSmsId() {
        return smsId;
    }

    public void setSmsId(Long smsId) {
        this.smsId = smsId;
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

    public Long getGameFType() {
        return gameFType;
    }

    public void setGameFType(Long gameFType) {
        this.gameFType = gameFType;
    }

    public String getSourceMatchId() {
        return sourceMatchId;
    }

    public void setSourceMatchId(String sourceMatchId) {
        this.sourceMatchId = sourceMatchId;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

}
