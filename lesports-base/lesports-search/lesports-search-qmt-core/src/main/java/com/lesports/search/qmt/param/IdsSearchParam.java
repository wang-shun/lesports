/**
 * 
 */
package com.lesports.search.qmt.param;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;

/**
 * @author sunyue7
 *
 */
public class IdsSearchParam extends PageAndSortSearchParam {

	@QueryParam("ids")
	private String ids;

	@Override
	protected BoolQueryBuilder createBoolQueryBuilder() {
		BoolQueryBuilder builder = super.createBoolQueryBuilder();
		List<Long> idList = toLongList(ids);
		if (CollectionUtils.isNotEmpty(idList)) {
			builder.must(new TermsQueryBuilder("id", idList));
		}
		return builder;
	}

	public boolean hasOnlyIdsParameter() {
		return StringUtils.isNotEmpty(ids) && StringUtils.isEmpty(getTagIds());
	}

	public List<Long> getIdList() {
		return toLongList(ids);
	}

	@Override
	public Integer getPage() {
		// disable page if only search IDs
		return hasOnlyIdsParameter() ? -1 : super.getPage();
	}

	@Override
	public String getSortBy() {
		// disable sort if only search IDs
		return hasOnlyIdsParameter() ? null : super.getSortBy();
	}

	@Override
	public void validate() {
		if (StringUtils.isEmpty(ids) && StringUtils.isEmpty(getTagIds())) {
			throw new LeWebApplicationException("At least one of ids or tagIds parameter is necessary!",
					LeStatus.PARAM_INVALID);
		}
	}

}
