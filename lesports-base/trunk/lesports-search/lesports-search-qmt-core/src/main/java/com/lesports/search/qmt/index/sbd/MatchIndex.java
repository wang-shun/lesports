/**
 * 
 */
package com.lesports.search.qmt.index.sbd;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbd.model.Match;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.utils.PinyinUtils;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.MATCH_INDEX, type = "MATCH")
public class MatchIndex extends Match implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7779482430412270066L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.MATCH;
	}
	

	public String getNamePinyin() {
		return PinyinUtils.pinyin(this.getName());
	}
}
