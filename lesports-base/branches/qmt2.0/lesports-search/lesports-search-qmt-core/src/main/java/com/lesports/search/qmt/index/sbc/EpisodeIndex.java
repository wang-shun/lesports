/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.Episode;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBC, type = "EPISODE")
public class EpisodeIndex extends Episode implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5925671386439201790L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.EPISODE;
	}
	
}
