/**
 * 
 */
package com.lesports.search.qmt.index.config;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.config.model.DictEntry;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_CONFIG, type = "DICT_ENTRY")
public class DictEntryIndex extends DictEntry implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9161878846413303062L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.DICT_ENTRY;
	}

}
