/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.Album;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBC, type = "ALBUM")
public class AlbumIndex extends Album implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3513031959414675462L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.ALBUM;
	}

}
