package com.lesports.bole.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.lesports.crawler.model.source.Source;

import java.io.Serializable;

/**
 * Bole对阵
 * 
 * @author denghui
 *
 */
@Document(collection = "bole_competitors")
@CompoundIndexes({
        @CompoundIndex(name = "type_1_name_1", def = "{'type': 1, 'name': 1}"),
        @CompoundIndex(name = "type_1_nickname_1", def = "{'type': 1, 'nickname': 1}"),
        @CompoundIndex(name = "type_1_sms_id_1", def = "{'type': 1, 'sms_id': 1}")
})
public class BoleCompetitor implements Serializable {

    private static final long serialVersionUID = -4721976010997535980L;
    @Id
    private Long id;
    // 对阵类型: TEAM/PLAYER
    private BoleCompetitorType type;
    // 对阵方名称
    private String name;
    // 昵称
    private String nickname;
    // 大项
    @Field("game_f_type")
    private Long gameFType;
    @Field("game_name")
    private String gameName;
    // Bole状态
    private BoleStatus status;
    // 标识新建赛事的来源
    private Source source;
    @Field("source_match_id")
    private String sourceMatchId;
    // 对阵方在SMS中的ID
    @Field("sms_id")
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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BoleCompetitorType getType() {
        return type;
    }

    public void setType(BoleCompetitorType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
