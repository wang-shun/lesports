/**
 * 
 */
package com.lesports.search.qmt.index.sbd;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbd.model.CompetitionSeason;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.COMPETITION_SEASON_INDEX, type = "COMPETITION_SEASON")
public class CompetitionSeasonIndex extends CompetitionSeason implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7174440612683237144L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.COMPETITION_SEASON;
	}

}
