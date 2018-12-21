package com.lesports.crawler.model.source;

import com.lesports.bole.model.LiveType;

import java.io.Serializable;


/**
 * 直播
 * 
 * @author denghui
 *
 */
public class SourceLive implements Serializable {
  private static final long serialVersionUID = -838988992146780904L;
  // 直播网站名称
  private String site;
  // 直播类型
  private LiveType type;
  // 直播名称
  private String name;
  // 直播地址
  private String url;

  public SourceLive() {
    super();
  }

  public SourceLive(String site, LiveType type, String name, String url) {
    super();
    this.site = site;
    this.type = type;
    this.name = name;
    this.url = url;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public LiveType getType() {
    return type;
  }

  public void setType(LiveType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
