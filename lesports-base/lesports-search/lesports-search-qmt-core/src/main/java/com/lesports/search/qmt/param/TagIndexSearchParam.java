/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class TagIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	/**
	 * 
	 */
	public TagIndexSearchParam() {
		super();
		indexType = IndexType.TAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lesports.bole.param.AbstractSearchParam#createBoolQueryBuilder()
	 */
	@Override
	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = super.createBoolQueryBuilder();
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(prefixOrWildcardString("name", name));
		}
		return boolQueryBuilder;
	}

}
