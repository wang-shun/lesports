/**
 * 
 */
package com.lesports.search.qmt.index.sbd;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbd.model.TopList;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBD, type = "TOP_LIST")
public class TopListIndex extends TopList implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2225173480032972865L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.TOP_LIST;
	}

}
