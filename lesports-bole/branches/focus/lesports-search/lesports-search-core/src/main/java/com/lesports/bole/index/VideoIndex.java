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
 * @since 2015/11/27
 */
@Document(indexName = "sms_hongkong", type = "videos")
public class VideoIndex extends SearchIndex<Long> {
    private String name;
    private Integer type;
    private List<Integer> supportLicences;
    @Field(type = FieldType.Nested)
    private List<LangString> multiLangNames;
    //允许展示的国家
    private List<Integer> allowCountries;
    //平台信息
    private List<Integer> platforms;

    public List<Integer> getSupportLicences() {
        return supportLicences;
    }

    public void setSupportLicences(List<Integer> supportLicences) {
        this.supportLicences = supportLicences;
    }

    public List<Integer> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Integer> platforms) {
        this.platforms = platforms;
    }

    public List<Integer> getAllowCountries() {
        return allowCountries;
    }

    public void setAllowCountries(List<Integer> allowCountries) {
        this.allowCountries = allowCountries;
    }

    public List<LangString> getMultiLangNames() {
        return multiLangNames;
    }

    public void setMultiLangNames(List<LangString> multiLangNames) {
        this.multiLangNames = multiLangNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        return true;
    }
}
