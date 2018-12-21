package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;
import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Set;

/**
 * Created by yangyu on 16/8/3.
 */
@Document(indexName = "sms_hongkong", type = "players")
public class PlayerIndex extends SearchIndex<Long>{
	private String name;
	private String englishName;
	private Set<Long> cids;
	private Long gameFType;
	private List<Integer> allowCountries;
	@Field(type = FieldType.Nested)
	private List<LangString> multiLangNames2;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Set<Long> getCids() {
		return cids;
	}

	public void setCids(Set<Long> cids) {
		this.cids = cids;
	}

	public Long getGameFType() {
		return gameFType;
	}

	public void setGameFType(Long gameFType) {
		this.gameFType = gameFType;
	}

	public List<Integer> getAllowCountries() {
		return allowCountries;
	}

	public void setAllowCountries(List<Integer> allowCountries) {
		this.allowCountries = allowCountries;
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
