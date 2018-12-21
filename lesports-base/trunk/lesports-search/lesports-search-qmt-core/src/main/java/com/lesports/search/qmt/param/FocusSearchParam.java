/**
 * 
 */
package com.lesports.search.qmt.param;

import java.util.HashSet;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class FocusSearchParam extends PageAndSortSearchParam {

	@QueryParam("focusType")
	private String focusType;

	@QueryParam("focusId")
	private Long focusId;

	@QueryParam("refTypes")
	private String refTypes;

	@Override
	public final NativeSearchQueryBuilder createNativeSearchQueryBuilder() {
		throw new RuntimeException("This parameter doesn't use this builder");
	}

	public BoolQueryBuilder focusQueryBuilder() {
		return new BoolQueryBuilder().must(new TermQueryBuilder("id", focusId));
	}

	public BoolQueryBuilder refsQueryBuilder() {
		return new BoolQueryBuilder().should(new TermsQueryBuilder("relatedIds", new Object[] { focusId }))
				.should(new TermsQueryBuilder("tagIds", new Object[] { focusId }));
	}

	public Long getFocusId() {
		return focusId;
	}

	public IndexType getFocusIndexType() {
		return toIndexType(focusType);
	}

	public String[] getRefIndexTypesArray() {
		List<IndexType> types = toIndexTypeList(refTypes);
		String[] typeArray = new String[types.size()];
		for (int i = 0; i < typeArray.length; i++) {
			typeArray[i] = types.get(i).name();
		}
		return typeArray;
	}

	public String[] getRefIndexsArray() {
		List<IndexType> typeList = toIndexTypeList(refTypes);
		HashSet<String> indexSet = new HashSet<String>();
		for (IndexType indexType : typeList) {
			if (indexType != null) {
				indexSet.add(indexType.getIndex());
			}
		}
		return indexSet.toArray(new String[indexSet.size()]);
	}

	public void setFocusType(String focusType) {
		this.focusType = focusType;
	}

	@Override
	public void validate() {
		if (focusId == null || focusId <= 0) {
			throw new LeWebApplicationException("Missing or invalid focusId parameter!", LeStatus.PARAM_INVALID);
		}
		if (StringUtils.isEmpty(refTypes)) {
			throw new LeWebApplicationException("refTypes parameter is necessary to specify reference content types!",
					LeStatus.PARAM_INVALID);
		}
	}
}
