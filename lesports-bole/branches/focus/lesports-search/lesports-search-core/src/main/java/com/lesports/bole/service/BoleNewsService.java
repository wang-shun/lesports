package com.lesports.bole.service;

import com.lesports.bole.index.BoleNewsIndex;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.utils.PageResult;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/11
 */
public interface BoleNewsService extends BoleSearchCrudService<BoleNewsIndex, Long>{
    public boolean save(Long id);

    public PageResult<SearchIndex> list(int page, int count, boolean source);

    public PageResult<SearchIndex> match(String word, int page, int count, boolean source);

    public PageResult<SearchIndex> matchPhrase(String word, int page, int count, boolean source);


}
