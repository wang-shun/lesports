/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.ProgramAlbum;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBC, type = "PROGRAM_ALBUM")
public class ProgramAlbumIndex extends ProgramAlbum implements Indexable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.PROGRAM_ALBUM;
	}

}
