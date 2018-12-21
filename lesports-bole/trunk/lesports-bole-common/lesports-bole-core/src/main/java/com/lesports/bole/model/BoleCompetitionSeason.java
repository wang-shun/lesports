package com.lesports.bole.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Bole赛季
 * 
 * @author denghui
 *
 */
@Document(collection = "bole_competition_seasons")
public class BoleCompetitionSeason implements Serializable {
  private static final long serialVersionUID = -7727013375275418509L;
  @Id
  private Long id;
  // 赛季名称
  @Indexed(name = "name_1", unique = true)
  private String name;
  // 赛季
  private String season;
  // Bole赛事Id
  private Long cid;
  // Bole状态
  private BoleStatus status;
  // 赛季在SMS中的ID
  @Field("sms_id")
  @Indexed(name = "sms_id_1")
  private Long smsId = Long.valueOf(0);
  @Field("attach_id")
  private Long attachId = Long.valueOf(0);
  @Field("mapping_id")
  private Long mappingId = Long.valueOf(0);
  // 开始时间
  @Field("start_time")
  private String startTime;
  // 创建时间
  @Field("create_at")
  private String createAt;
  // 更新时间
  @Field("update_at")
  private String updateAt;

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

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


}
