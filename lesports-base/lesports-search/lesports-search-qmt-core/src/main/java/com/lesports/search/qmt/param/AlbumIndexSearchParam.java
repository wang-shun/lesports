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
public class AlbumIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("title")
	private String title;

	/**
	 * Competition ID
	 */
	@QueryParam("cid")
	private Long cid;

	/**
	 * Competition ID
	 */
	@QueryParam("channelId")
	private Long channelId;

	/**
	 * 
	 */
	public AlbumIndexSearchParam() {
		super();
		indexType = IndexType.ALBUM;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lesports.bole.param.AbstractSearchParam#createBoolQueryBuilder()
	 */
	@Override
	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = super.createBoolQueryBuilder();
		if (StringUtils.isNotEmpty(title)) {
			boolQueryBuilder.must(prefixOrWildcardString("title", title));
		}
		if (null != cid) {
			boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
		}
		if (null != channelId) {
			boolQueryBuilder.must(new TermQueryBuilder("channel", channelId));
		}
		return boolQueryBuilder;
	}

}
