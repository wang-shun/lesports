package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;
import org.apache.commons.beanutils.LeBeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/14
 */
@Document(indexName = "bole", type = "competitors")
public class BoleCompetitorIndex extends SearchIndex<Long> {
    // 对阵双方名称
    private String name;
    private Integer status;
    //大项
    private String gameName;
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean newInstance(ElasticSearchResult.HitItem hitItem) {
        LeBeanUtils.copyNotEmptyPropertiesQuietly(this, hitItem.getSource());
        return true;
    }
}
