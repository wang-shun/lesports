package com.lesports.bole.service;

import com.lesports.bole.index.MatchIndex;
import com.lesports.bole.utils.PageResult;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface MatchIndexService extends BoleSearchCrudService<MatchIndex, Long> {
	PageResult<MatchIndex> findByParams(long id, String name, Long cid, Long csid, List<String> startTime, Integer countryCode, int page, int count);
}
