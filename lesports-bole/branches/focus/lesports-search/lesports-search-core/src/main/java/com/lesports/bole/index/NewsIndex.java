package com.lesports.bole.index;

import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.bole.utils.ElasticSearchResult;
import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Set;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
@Document(indexName = "sms_hongkong", type = "news")
public class NewsIndex extends SearchIndex<Long> {
    private String name;
    //发布时间
    private String publishAt;
    //新闻类型
    private Integer newsType;
    //在线状态
    private Integer onlineStatus;
    //新闻上的赛事id
    private List<Long> mids;
    //新闻的平台信息
    private List<Integer> platforms;
    //允许展示的国家
    private Integer allowCountry;
    private Integer languageCode;
    private List<Integer> supportLicences;
    private Set<Long> tagIds;
    private Set<Long> tids;

    public Set<Long> getTagIds() {
		return tagIds;
	}

	public void setTagIds(Set<Long> tagIds) {
		this.tagIds = tagIds;
	}

	public List<Integer> getSupportLicences() {
        return supportLicences;
    }

    public void setSupportLicences(List<Integer> supportLicences) {
        this.supportLicences = supportLicences;
    }

    public List<Integer> getPlatforms() {
        return platforms;
    }

    public Integer getAllowCountry() {
        return allowCountry;
    }

    public void setAllowCountry(Integer allowCountry) {
        this.allowCountry = allowCountry;
    }

    public Integer getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(Integer languageCode) {
        this.languageCode = languageCode;
    }

    public void setPlatforms(List<Integer> platforms) {
        this.platforms = platforms;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        this.setMids(null);
        return true;
    }

	public Set<Long> getTids() {
		return tids;
	}

	public void setTids(Set<Long> tids) {
		this.tids = tids;
	}
}
