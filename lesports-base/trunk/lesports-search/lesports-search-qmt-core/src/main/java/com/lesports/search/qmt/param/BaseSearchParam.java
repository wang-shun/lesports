/**
 * 
 */
package com.lesports.search.qmt.param;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public abstract class BaseSearchParam implements SearchParam {

	public static final char[] WILDCARD = new char[] { '*', '?' };

	@QueryParam("id")
	@DefaultValue("0")
	private Long id = 0L;

	@QueryParam("tagIds")
	private String tagIds;

	@QueryParam("deleted")
	private Boolean deleted;

	@QueryParam("createAt")
	private String createAt;

	@QueryParam("updateAt")
	private String updateAt;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lesports.bole.param.SearchParam#createNativeSearchQueryBuilder()
	 */
	@Override
	public NativeSearchQueryBuilder createNativeSearchQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = createBoolQueryBuilder();
		boolQueryBuilder = boolQueryBuilder != null ? boolQueryBuilder : new BoolQueryBuilder();
		return getDefaultNativeBuilder().withQuery(boolQueryBuilder);
	}

	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		if (deleted != null) {
			boolQueryBuilder.must(new TermQueryBuilder("deleted", deleted));
		}
		if (id > 0) {
			boolQueryBuilder.must(new TermQueryBuilder("id", id));
		}
		if (StringUtils.isNotEmpty(tagIds)) {
			boolean exclude = tagIds.startsWith("-");
			String tags = exclude ? tagIds.substring(1) : tagIds;
			List<String> tagList = toStringList(tags);
			if (CollectionUtils.isNotEmpty(tagList)) {
				if (exclude) {
					boolQueryBuilder.mustNot(new TermsQueryBuilder("relatedIds", tagList));
					boolQueryBuilder.mustNot(new TermsQueryBuilder("tagIds", tagList));
				} else {
					boolQueryBuilder.should(new TermsQueryBuilder("relatedIds", tagList));
					boolQueryBuilder.should(new TermsQueryBuilder("tagIds", tagList));
				}
			}
		}
		QueryBuilder rangeQuery = null;
		if ((rangeQuery = betweenQueryBuilder("createAt", createAt)) != null) {
			boolQueryBuilder.must(rangeQuery);
		}
		if ((rangeQuery = betweenQueryBuilder("updateAt", updateAt)) != null) {
			boolQueryBuilder.must(rangeQuery);
		}
		return boolQueryBuilder;
	}

	protected QueryBuilder betweenQueryBuilder(String fieldName, String range) {
		if (StringUtils.isEmpty(range) || !StringUtils.contains(range, ",")) {
			return null;
		}
		String[] fromAndTo = range.split(",");
		if (fromAndTo.length > 0 && fromAndTo.length <= 2) {
			RangeQueryBuilder rangeQuery = new RangeQueryBuilder(fieldName);
			if (StringUtils.isNotEmpty(fromAndTo[0])) {
				rangeQuery.gte(fromAndTo[0]);
			}
			if (StringUtils.isNotEmpty(fromAndTo[1])) {
				rangeQuery.lte(fromAndTo[1]);
			}
			return rangeQuery;
		}
		return null;
	}

	protected QueryBuilder prefixOrWildcardString(String fieldName, String text) {
		if (StringUtils.containsAny(text, WILDCARD)) {
			return new WildcardQueryBuilder(fieldName, text.toLowerCase());
		}
		return new MatchQueryBuilder(fieldName, text).type(MatchQueryBuilder.Type.PHRASE_PREFIX);
	}

	protected QueryBuilder multiWildcardString(String text, String... fieldNames) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		for (String fieldName : fieldNames) {
			boolQueryBuilder.should(prefixOrWildcardString(fieldName, text));
		}
		return boolQueryBuilder;
	}

	@Override
	public void validate() {
		// do nothing
	}

	protected List<Long> toLongList(String text) {
		if (StringUtils.isNotEmpty(text)) {
			String[] array = text.split(",");
			List<Long> list = new ArrayList<Long>(array.length);
			for (String id : array) {
				list.add(Long.valueOf(id));
			}
			return list;
		}
		return null;
	}

	protected List<String> toStringList(String text) {
		if (StringUtils.isNotEmpty(text)) {
			String[] array = text.split(",");
			List<String> list = new ArrayList<String>(array.length);
			for (String str : array) {
				list.add(str);
			}
			return list;
		}
		return null;
	}

	protected List<IndexType> toIndexTypeList(String text) {
		if (StringUtils.isNotEmpty(text)) {
			String[] array = text.split(",");
			ArrayList<IndexType> result = new ArrayList<IndexType>();
			for (String type : array) {
				IndexType indexType = toIndexType(type);
				if (indexType != null) {
					result.add(indexType);
				}
			}
			return result;
		}
		return null;
	}

	protected IndexType toIndexType(String text) {
		if (StringUtils.isNotEmpty(text)) {
			return MetaData.IndexType.valueOf(text.toUpperCase());
		}
		return null;
	}

	protected NativeSearchQueryBuilder getDefaultNativeBuilder() {
		return new NativeSearchQueryBuilder().withIndices(MetaData.QMT_INDEXES);
	}

	public Long getId() {
		return id;
	}

	public String getTagIds() {
		return tagIds;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public String[] getUpdateAtRange() {
		return StringUtils.isEmpty(updateAt) ? null : updateAt.split(",");
	}

}
