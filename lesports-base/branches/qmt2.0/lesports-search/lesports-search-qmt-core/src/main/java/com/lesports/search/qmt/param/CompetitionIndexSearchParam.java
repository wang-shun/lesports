/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class CompetitionIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("gameFType")
	private Long gameFType;

	@QueryParam("gameSType")
	private Long gameSType;

	/**
	 * 
	 */
	public CompetitionIndexSearchParam() {
		super();
		indexType = IndexType.COMPETITION;
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
			boolQueryBuilder.must(multiWildcardString(name, "name", "abbreviation"));
		}
		if (null != gameFType) {
			boolQueryBuilder.must(new TermQueryBuilder("gameFType", gameFType));
		}
		if (null != gameSType) {
			boolQueryBuilder.must(new TermQueryBuilder("gameSType", gameSType));
		}
		return boolQueryBuilder;
	}

}
