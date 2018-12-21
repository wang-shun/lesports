package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;

import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
@Document(indexName = "sms_hongkong", type = "episodes")
public class EpisodeIndex extends SearchIndex<Long> {
    //节目名称
    private String name;
    //关联的比赛id
    private Long mid;
    //专辑id
    private Long aid;
    //节目类型
    private Integer type;
    private Boolean hasLive;
    private String startTime;
    //是否是超级手机体育桌面的子频道推荐赛程
    private Boolean isLephoneChannelMatch;
    //是否是超级手机体育桌面的推荐赛程
    private Boolean isLephoneMatch;
	private Integer allowCountry;
	private Integer languageCode;
	private Set<Long> tagIds;
	private Set<Long> competitorIds;
	private Integer status;
	private Integer textLiveStatus;
    private List<Integer> livePlatforms;
    private Boolean online;

    public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public List<Integer> getLivePlatforms() {
		return livePlatforms;
	}

	public void setLivePlatforms(List<Integer> livePlatforms) {
		this.livePlatforms = livePlatforms;
	}

	public Set<Long> getTagIds() {
		return tagIds;
	}

	public void setTagIds(Set<Long> tagIds) {
		this.tagIds = tagIds;
	}

	public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getHasLive() {
        return hasLive;
    }

    public void setHasLive(Boolean hasLive) {
        this.hasLive = hasLive;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Boolean getIsLephoneChannelMatch() {
        return isLephoneChannelMatch;
    }

    public void setIsLephoneChannelMatch(Boolean isLephoneChannelMatch) {
        this.isLephoneChannelMatch = isLephoneChannelMatch;
    }

    public Boolean getIsLephoneMatch() {
        return isLephoneMatch;
    }

    public void setIsLephoneMatch(Boolean isLephoneMatch) {
        this.isLephoneMatch = isLephoneMatch;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

	public Boolean getLephoneChannelMatch() {
		return isLephoneChannelMatch;
	}

	public void setLephoneChannelMatch(Boolean lephoneChannelMatch) {
		isLephoneChannelMatch = lephoneChannelMatch;
	}

	public Boolean getLephoneMatch() {
		return isLephoneMatch;
	}

	public void setLephoneMatch(Boolean lephoneMatch) {
		isLephoneMatch = lephoneMatch;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTextLiveStatus() {
		return textLiveStatus;
	}

	public void setTextLiveStatus(Integer textLiveStatus) {
		this.textLiveStatus = textLiveStatus;
	}

	public Set<Long> getCompetitorIds() {
		return competitorIds;
	}

	public void setCompetitorIds(Set<Long> competitorIds) {
		this.competitorIds = competitorIds;
	}
}
