package com.lesports.bole.service;

import com.lesports.bole.index.VideoIndex;
import com.lesports.bole.utils.PageResult;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface VideoIndexService extends BoleSearchCrudService<VideoIndex, Long> {
    PageResult<VideoIndex> findByParams(long id, String name, Integer type, Integer countryCode, Integer platform, Integer supportLicence, List<String> updateAt, int page, int count);
}
