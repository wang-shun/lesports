/**
 * 
 */
package com.lesports.search.qmt.index.config;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.config.model.Tag;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_CONFIG, type = "TAG")
public class TagIndex extends Tag implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3606700670149734898L;

	private Boolean deleted = false;

	@Override
	public Boolean getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.TAG;
	}

}
