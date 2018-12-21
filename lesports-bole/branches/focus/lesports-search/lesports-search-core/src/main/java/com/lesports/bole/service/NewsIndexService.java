package com.lesports.bole.service;

import com.lesports.api.common.CountryCode;
import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.utils.PageResult;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface NewsIndexService extends BoleSearchCrudService<NewsIndex, Long> {
    PageResult<NewsIndex> findByParams(long id, String name, Integer newsType, Integer onlineStatus, Integer mid,
                                       Integer platform, Integer supportLicence, List<String> publishAt, Integer countryCode, Integer languageCode, int page, int count);
}
