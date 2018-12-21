/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class TopicIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("channelId")
	private Long channelId;

	/**
	 * Competition ID
	 */
	@QueryParam("cid")
	private Long cid;

	/**
	 * 
	 */
	public TopicIndexSearchParam() {
		super();
		indexType = IndexType.TOPIC;
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
		if (null != channelId) {
			boolQueryBuilder.must(new TermQueryBuilder("channelId", channelId));
		}
		if (null != cid) {
			boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
		}
		return boolQueryBuilder;
	}

}
