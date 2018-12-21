/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.QmtResource;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBC, type = "RESOURCE")
public class ResourceIndex extends QmtResource implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3123773644646678093L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.RESOURCE;
	}

}
