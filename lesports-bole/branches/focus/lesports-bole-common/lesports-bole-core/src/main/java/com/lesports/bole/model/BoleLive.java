package com.lesports.bole.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.lesports.crawler.model.source.Source;

import java.io.Serializable;

/**
 * Bole直播信息
 * 
 * @author denghui
 *
 */
@Document(collection = "bole_lives")
public class BoleLive implements Serializable {

    private static final long serialVersionUID = 6401614933201887363L;
    @Id
    private Long id;
    // 来源
    private Source source;
    @Indexed(name = "cid_1")
    private Long cid;
    // Bole比赛ID
    @Field("match_id")
    @Indexed(name = "match_id_1")
    private Long matchId = Long.valueOf(0);
    // 无效/有效
    private Boolean deleted;
    // 直播网站名称
    private String site;
    // 直播信息名称
    private String name;
    // 直播类型
    private LiveType type;
    // 直播地址
    private String url;
    // 直播状态: 上线/下线等
    private BoleLiveStatus status = BoleLiveStatus.OFFLINE;
    // 创建时间
    @Field("create_at")
    private String createAt;
    // 更新时间
    @Field("update_at")
    private String updateAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LiveType getType() {
        return type;
    }

    public void setType(LiveType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BoleLiveStatus getStatus() {
        return status;
    }

    public void setStatus(BoleLiveStatus status) {
        this.status = status;
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

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

}
