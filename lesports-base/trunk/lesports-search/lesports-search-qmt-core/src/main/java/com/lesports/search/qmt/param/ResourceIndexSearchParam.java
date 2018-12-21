/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class ResourceIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("updateType")
	private String updateType;

	@QueryParam("platform")
	private String platform;

	/**
	 * 
	 */
	public ResourceIndexSearchParam() {
		super();
		indexType = IndexType.RESOURCE;
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
		if (StringUtils.isNotEmpty(updateType)) {
			boolQueryBuilder.must(new TermQueryBuilder("updateType", updateType));
		}
		if (StringUtils.isNotEmpty(platform)) {
			boolQueryBuilder.must(new MatchQueryBuilder("platforms", platform));
		}
		return boolQueryBuilder;
	}

}
