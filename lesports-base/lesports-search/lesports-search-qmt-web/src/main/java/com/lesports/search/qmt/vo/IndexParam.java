package com.lesports.search.qmt.vo;

import javax.ws.rs.FormParam;
import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
public class IndexParam {
    @FormParam("id")
    private Long id;
    @FormParam("name")
    private String name;
    @FormParam("updateAt")
    private String updateAt;
    //发布时间
    @FormParam("publishAt")
    private String publishAt;
    //新闻类型
    @FormParam("newsType")
    private Integer newsType;
    //在线状态
    @FormParam("onlineStatus")
    private Integer onlineStatus;
    //新闻上的赛事id
    @FormParam("mids")
    private List<Long> mids;
    //是否删除
    @FormParam("deleted")
    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(String publishAt) {
        this.publishAt = publishAt;
    }

    public Integer getNewsType() {
        return newsType;
    }

    public void setNewsType(Integer newsType) {
        this.newsType = newsType;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public List<Long> getMids() {
        return mids;
    }

    public void setMids(List<Long> mids) {
        this.mids = mids;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
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
}
