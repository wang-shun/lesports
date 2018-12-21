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
@Document(indexName = "bole", type = "competitions")
public class BoleCompetitionIndex extends SearchIndex<Long> {
    // 赛事名称
    private String name;
    private Integer status;

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
