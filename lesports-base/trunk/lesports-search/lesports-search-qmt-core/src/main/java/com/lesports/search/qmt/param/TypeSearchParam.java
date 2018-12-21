/**
 * 
 */
package com.lesports.search.qmt.param;

import java.util.HashSet;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class TypeSearchParam extends PageAndSortSearchParam {

	@QueryParam("name")
	private String name;

	@QueryParam("types")
	private String types;

	@QueryParam("dictParentIds")
	private String dictParentIds;

	@QueryParam("excludeEpisodeTypes")
	private String excludeEpisodeTypes;

	@Override
	public NativeSearchQueryBuilder createNativeSearchQueryBuilder() {
		List<IndexType> typeList = toIndexTypeList(types);
		if (CollectionUtils.isEmpty(typeList)) {
			return null;
		}
		NativeSearchQueryBuilder builder = super.createNativeSearchQueryBuilder();
		HashSet<String> indexSet = new HashSet<String>();
		HashSet<String> typeSet = new HashSet<String>();
		for (IndexType indexType : typeList) {
			if (indexType != null) {
				indexSet.add(indexType.getIndex());
				typeSet.add(indexType.getIdType().name());
			}
		}
		builder.withIndices(indexSet.toArray(new String[indexSet.size()]))
				.withTypes(typeSet.toArray(new String[typeSet.size()]));
		return builder;
	}

	@Override
	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder builder = new BoolQueryBuilder();
		HashSet<IndexType> typeSet = new HashSet<IndexType>(toIndexTypeList(types));
		// handle each type
		if (!typeSet.isEmpty()) {
			for (IndexType type : typeSet) {
				BoolQueryBuilder typeBuilder = super.createBoolQueryBuilder();
				typeBuilder.must(new MatchQueryBuilder("indexType", type.name()));
				if (StringUtils.isNotEmpty(name)) {
					BoolQueryBuilder nameOrTitle = new BoolQueryBuilder();
					nameOrTitle.should(prefixOrWildcardString("name", name));
					nameOrTitle.should(prefixOrWildcardString("title", name));
					typeBuilder.must(nameOrTitle);
				}
				// handle DICT_ENTRY
				if (StringUtils.isNotEmpty(dictParentIds) && type.equals(IndexType.DICT_ENTRY)) {
					typeBuilder.must(new TermsQueryBuilder("parentId", toLongList(dictParentIds)));
				}
				// handle EPISODE
				if (StringUtils.isNotEmpty(excludeEpisodeTypes) && type.equals(IndexType.EPISODE)) {
					typeBuilder.mustNot(new MatchQueryBuilder("type", excludeEpisodeTypes.split(",")));
				}
				builder.should(typeBuilder);
			}
		}
		return builder;
	}

	@Override
	public void validate() {
		if (StringUtils.isEmpty(types)) {
			throw new LeWebApplicationException("types parameter is necessary to specify search scope!",
					LeStatus.PARAM_INVALID);
		}
	}

}
