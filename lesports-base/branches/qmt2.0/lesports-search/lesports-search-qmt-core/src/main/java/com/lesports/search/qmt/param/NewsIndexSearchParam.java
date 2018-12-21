/**
 * 
 */
package com.lesports.search.qmt.param;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class NewsIndexSearchParam extends SingleTypeSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("newsType")
	private String newsType;

	@QueryParam("online")
	private String online;

	@QueryParam("mid")
	private Integer mid;

	@QueryParam("platforms")
	private String platforms;

	@QueryParam("publishAt")
	private String publishAt;

	@QueryParam("country")
	private String country;

	@QueryParam("language")
	private String language;

	@QueryParam("supportLicence")
	private String supportLicence;

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
	public NewsIndexSearchParam() {
		super();
		indexType = IndexType.NEWS;
	}

	@Override
	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = super.createBoolQueryBuilder();
		if (StringUtils.isNotEmpty(name)) {
			boolQueryBuilder.must(prefixOrWildcardString("name", name));
		}
		if (null != newsType) {
			boolQueryBuilder.must(new MatchQueryBuilder("type", newsType));
		}
		if (null != online) {
			boolQueryBuilder.must(new MatchQueryBuilder("online", online));
		}
		if (null != mid) {
			boolQueryBuilder.must(new TermQueryBuilder("mids", mid));
		}
		List<String> platformList = toStringList(platforms);
		if (CollectionUtils.isNotEmpty(platformList)) {
			boolQueryBuilder.must(new MatchQueryBuilder("platforms", platformList));
		}
		if (StringUtils.isNotEmpty(country)) {
			boolQueryBuilder.must(new MatchQueryBuilder("allowCountry", country));
		}
		if (StringUtils.isNotEmpty(language)) {
			boolQueryBuilder.must(new MatchQueryBuilder("languageCode", language));
		}
		if (StringUtils.isNotEmpty(supportLicence)) {
			boolQueryBuilder.must(new MatchQueryBuilder("supportLicences", supportLicence));
		}
		if (null != channelId) {
			boolQueryBuilder.must(new TermQueryBuilder("channelId", channelId));
		}
		if (null != cid) {
			boolQueryBuilder.must(new TermQueryBuilder("cid", cid));
		}
		QueryBuilder rangeQuery = null;
		if ((rangeQuery = betweenQueryBuilder("publishAt", publishAt)) != null) {
			boolQueryBuilder.must(rangeQuery);
		}
		return boolQueryBuilder;
	}

}