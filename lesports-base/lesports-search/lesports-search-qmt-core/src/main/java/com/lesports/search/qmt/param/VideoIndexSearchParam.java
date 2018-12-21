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
public class VideoIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("title")
	private String title;

	@QueryParam("videoType")
	private String videoType;

	@QueryParam("channelId")
	private Long channelId;

	@QueryParam("aid")
	private Long aid;

	/**
	 * Competition ID
	 */
	@QueryParam("cid")
	private Long cid;

	/**
	 * 
	 */
	public VideoIndexSearchParam() {
		super();
		indexType = IndexType.VIDEO;
	}

	@Override
	public BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = super.createBoolQueryBuilder();
		if (StringUtils.isNotEmpty(title)) {
			boolQueryBuilder.must(prefixOrWildcardString("title", title));
		}
		if (StringUtils.isNotEmpty(videoType)) {
			boolQueryBuilder.must(new MatchQueryBuilder("videoType", videoType));
		}
		if (null != channelId) {
			boolQueryBuilder.must(new TermQueryBuilder("channel", channelId));
		}
		if (null != cid) {
			if (cid >= 0) {
				boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
			} else {
				boolQueryBuilder.mustNot(new TermQueryBuilder("cid", -cid));
			}
		}
		if (null != aid) {
			boolQueryBuilder.must(new TermQueryBuilder("aid", aid));
		}
		return boolQueryBuilder;
	}

}