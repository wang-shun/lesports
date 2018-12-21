/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.Video;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBC, type = "VIDEO")
public class VideoIndex extends Video implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1253330569538591321L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.VIDEO;
	}

}
