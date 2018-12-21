/**
 * 
 */
package com.lesports.search.qmt.param;

import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public abstract class SingleTypeSearchParam extends PageAndSortSearchParam {

	protected IndexType indexType;

	@Override
	public NativeSearchQueryBuilder createNativeSearchQueryBuilder() {
		return indexType != null
				? super.createNativeSearchQueryBuilder().withIndices(indexType.getIndex().getName()).withTypes(indexType.name())
				: super.createNativeSearchQueryBuilder();
	}
}
