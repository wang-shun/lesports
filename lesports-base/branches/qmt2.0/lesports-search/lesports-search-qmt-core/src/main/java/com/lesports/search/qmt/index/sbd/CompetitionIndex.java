/**
 * 
 */
package com.lesports.search.qmt.index.sbd;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbd.model.Competition;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_SBD, type = "COMPETITION")
public class CompetitionIndex extends Competition implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8702451285433037803L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.COMPETITION;
	}

}
