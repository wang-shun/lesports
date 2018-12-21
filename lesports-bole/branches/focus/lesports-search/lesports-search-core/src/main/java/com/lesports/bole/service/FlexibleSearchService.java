/**
 * 
 */
package com.lesports.bole.service;

import java.util.Map;

import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.utils.PageResult;
import com.lesports.sms.api.common.Platform;

/**
 * @author sunyue7
 *
 */
public interface FlexibleSearchService {

	public Map<String, PageResult<SearchIndex<Long>>> findRelated(String focusIds, String[] typeArray,
			Platform platform, String[] startTimeRange, Integer[] episodeStatus, String episodeSort, Integer page,
			Integer count);

}
