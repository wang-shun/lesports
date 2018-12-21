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
public class EpisodeIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("episodeType")
	private String episodeType;

	@QueryParam("channelId")
	private Long channelId;

	/**
	 * Album ID
	 */
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
	public EpisodeIndexSearchParam() {
		super();
		indexType = IndexType.EPISODE;
	}

	@Override
	public BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = super.createBoolQueryBuilder();
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(prefixOrWildcardString("name", name));
		}
		if (null != episodeType) {
			boolQueryBuilder.must(new MatchQueryBuilder("type", episodeType));
		}
		if (null != aid) {
			boolQueryBuilder.must(new TermQueryBuilder("aid", aid));
		}
		if (null != channelId) {
			boolQueryBuilder.must(new TermQueryBuilder("channelId", channelId));
		}
		if (null != cid) {
			if (cid >= 0) {
				boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
			} else {
				boolQueryBuilder.mustNot(new TermQueryBuilder("cid", -cid));
			}
		}
		return boolQueryBuilder;
	}

}