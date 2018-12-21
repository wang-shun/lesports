/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

/**
 * @author sunyue7
 *
 */
public abstract class PageAndSortSearchParam extends BaseSearchParam {

	@QueryParam("page")
	@DefaultValue("0")
	private Integer page = 0;

	@QueryParam("count")
	@DefaultValue("50")
	private Integer count = 50;

	@QueryParam("sortBy")
	private String sortBy;

	@QueryParam("desc")
	@DefaultValue("true")
	private Boolean desc;

	public Integer getPage() {
		return page;
	}

	public Integer getCount() {
		return count;
	}

	public String getSortBy() {
		return sortBy;
	}

	public Boolean getDesc() {
		return desc;
	}

	public final boolean isPageEnable() {
		return getPage() != null && getCount() != null && getPage() >= 0 && getCount() > 0;
	}

	public final boolean isSortEnable() {
		return StringUtils.isNotEmpty(sortBy);
	}

	public NativeSearchQueryBuilder createNativeSearchQueryBuilder() {
		NativeSearchQueryBuilder builder = super.createNativeSearchQueryBuilder();
		if (isPageEnable()) {
			builder.withPageable(new PageRequest(getPage(), getCount()));
		}
		if (isSortEnable()) {
			builder.withSort(new FieldSortBuilder(sortBy)
					.order(Boolean.FALSE.equals(getDesc()) ? SortOrder.ASC : SortOrder.DESC));
		}
		return builder;
	}
}
