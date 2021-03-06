/**
 * 
 */
package com.lesports.search.qmt.index.sbd;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbd.model.Player;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBD, type = "PLAYER")
public class PlayerIndex extends Player implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2005479201837045055L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.PLAYER;
	}

}
