/**
 * 
 */
package com.lesports.search.qmt.service;

import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.param.IdsSearchParam;
import com.lesports.search.qmt.param.TypeSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * This service provides search index service over all indexes and types
 * 
 * @author sunyue7
 *
 */
public interface FlexibleSearchService {

	/**
	 * Find index list by given IDs or related tags
	 * 
	 * @param param
	 * @return
	 */
	public PageResult<Indexable> findByFields(IdsSearchParam param);

	public PageResult<Indexable> findByTypes(TypeSearchParam param);

}
