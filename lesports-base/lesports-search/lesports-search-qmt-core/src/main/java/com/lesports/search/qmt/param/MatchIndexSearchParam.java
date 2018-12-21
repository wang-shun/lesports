/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class MatchIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("gameFType")
	private Long gameFType;

	@QueryParam("gameSType")
	private Long gameSType;

	@QueryParam("discipline")
	private Long discipline;

	/**
	 * Competition ID
	 */
	@QueryParam("cid")
	private Long cid;

	@Deprecated
	@QueryParam("startTimeBefore")
	private String startTimeBefore;

	@Deprecated
	@QueryParam("startTimeAfter")
	private String startTimeAfter;

	@Deprecated
	@QueryParam("endTimeBefore")
	private String endTimeBefore;

	@Deprecated
	@QueryParam("endTimeAfter")
	private String endTimeAfter;

	@QueryParam("startTime")
	private String startTime;

	@QueryParam("endTime")
	private String endTime;
	/**
	 * yyyyMMdd
	 */
	@QueryParam("startDate")
	private String startDate;

	@QueryParam("localStartTime")
	private String localStartTime;

	/**
	 * yyyyMMdd
	 */
	@QueryParam("localStartDate")
	private String localStartDate;

	/**
	 * 
	 */
	public MatchIndexSearchParam() {
		super();
		indexType = IndexType.MATCH;
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
		if (null != gameFType) {
			boolQueryBuilder.must(new TermQueryBuilder("gameFType", gameFType));
		}
		if (null != gameSType) {
			boolQueryBuilder.must(new TermQueryBuilder("gameSType", gameSType));
		}
		if (null != discipline) {
			boolQueryBuilder.must(new TermQueryBuilder("discipline", discipline));
		}
		if (null != cid) {
			boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
		}
		QueryBuilder rangeQuery = null;
		if (startTime == null) {
			startTime = startTimeAfter == null ? ""
					: startTimeAfter + "," + startTimeBefore == null ? "" : startTimeBefore;
		}
		if ((rangeQuery = betweenQueryBuilder("startTime", startTime)) != null) {
			boolQueryBuilder.must(rangeQuery);
		}
		if (endTime == null) {
			endTime = endTimeAfter == null ? "" : endTimeAfter + "," + endTimeBefore == null ? "" : endTimeBefore;
		}
		if ((rangeQuery = betweenQueryBuilder("endTime", endTime)) != null) {
			boolQueryBuilder.must(rangeQuery);
		}
		if (StringUtils.isNotEmpty(startDate)) {
			boolQueryBuilder.must(new TermQueryBuilder("startDate", startDate));
		}
		if (StringUtils.isNotEmpty(localStartTime)) {
			boolQueryBuilder.must(new TermQueryBuilder("localStartTime", localStartTime));
		}
		if (StringUtils.isNotEmpty(localStartDate)) {
			boolQueryBuilder.must(new TermQueryBuilder("localStartDate", localStartDate));
		}
		return boolQueryBuilder;
	}

}
