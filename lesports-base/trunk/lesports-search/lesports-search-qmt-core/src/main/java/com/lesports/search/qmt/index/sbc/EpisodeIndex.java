/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.Episode;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.utils.PinyinUtils;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.EPISODE_INDEX, type = "EPISODE")
public class EpisodeIndex extends Episode implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5925671386439201790L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.EPISODE;
	}
	
	public String getNamePinyin() {
		return PinyinUtils.pinyin(this.getName());
	}
}
