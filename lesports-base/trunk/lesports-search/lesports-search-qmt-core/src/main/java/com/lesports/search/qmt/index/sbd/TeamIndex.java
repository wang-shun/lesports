/**
 * 
 */
package com.lesports.search.qmt.index.sbd;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbd.model.Team;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.utils.PinyinUtils;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.TEAM_INDEX, type = "TEAM")
public class TeamIndex extends Team implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6818744447781626450L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.TEAM;
	}
	
	public String getNamePinyin() {
		return PinyinUtils.pinyin(this.getName());
	}

}
