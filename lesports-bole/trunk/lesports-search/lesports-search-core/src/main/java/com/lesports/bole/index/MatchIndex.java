package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;
import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
@Document(indexName = "sms_hongkong", type = "matches")
public class MatchIndex extends SearchIndex<Long> {
    private String name;
	private Long cid;
	private Long csid;
	private List<Integer> allowCountries;
	private String startTime;
	@Field(type = FieldType.Nested)
	private List<LangString> multiLangNames2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Long getCsid() {
		return csid;
	}

	public void setCsid(Long csid) {
		this.csid = csid;
	}

	public List<Integer> getAllowCountries() {
		return allowCountries;
	}

	public void setAllowCountries(List<Integer> allowCountries) {
		this.allowCountries = allowCountries;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public List<LangString> getMultiLangNames2() {
		return multiLangNames2;
	}

	public void setMultiLangNames2(List<LangString> multiLangNames2) {
		this.multiLangNames2 = multiLangNames2;
	}

	@Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        return true;
    }
}
