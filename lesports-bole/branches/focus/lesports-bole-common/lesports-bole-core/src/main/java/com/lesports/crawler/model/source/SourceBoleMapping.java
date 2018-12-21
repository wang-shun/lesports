package com.lesports.crawler.model.source;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Source, Bole对应关系
 * 
 * @author denghui
 *
 */
@Document(collection = "source_bole_mappings")
@CompoundIndexes({
        @CompoundIndex(name = "source_1_type_1_value_1", def = "{'source': 1, 'source_value_type':1, 'source_value': 1}")
})
public class SourceBoleMapping implements Serializable {

    private static final long serialVersionUID = 5466677613536867940L;
    @Id
    private String id;
    // 来源
    private Source source;
    // 来源值类型: 赛事/对阵方/比赛
    @Field("source_value_type")
    private SourceValueType sourceValueType;
    // 来源值: 赛事名称/对阵方名称/比赛Id
    @Field("source_value")
    private String sourceValue;
    // 对应的BoleID
    @Field("bole_id")
    private Long boleId;
    // 创建时间
    @Field("create_at")
    private String createAt;
    // 更新时间
    @Field("update_at")
    private String updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public SourceValueType getSourceValueType() {
        return sourceValueType;
    }

    public void setSourceValueType(SourceValueType sourceValueType) {
        this.sourceValueType = sourceValueType;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Long getBoleId() {
        return boleId;
    }

    public void setBoleId(Long boleId) {
        this.boleId = boleId;
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

}
