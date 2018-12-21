package com.lesports.bole.index;

import com.lesports.bole.utils.ElasticSearchResult;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/9
 */
public interface Init {
    public boolean newInstance(ElasticSearchResult.HitItem hitItem);
}
