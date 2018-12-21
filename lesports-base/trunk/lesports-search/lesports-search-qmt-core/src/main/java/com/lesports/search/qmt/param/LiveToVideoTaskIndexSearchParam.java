/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class LiveToVideoTaskIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("taskName")
	private String taskName;

	/**
	 * 
	 */
	public LiveToVideoTaskIndexSearchParam() {
		super();
		indexType = IndexType.TRANSCODE_LIVE_TASK;
	}

	@Override
	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = super.createBoolQueryBuilder();
		if (StringUtils.isNotEmpty(taskName)) {
			boolQueryBuilder.must(prefixOrWildcardString("taskName", taskName));
		}
		return boolQueryBuilder;
	}
}
