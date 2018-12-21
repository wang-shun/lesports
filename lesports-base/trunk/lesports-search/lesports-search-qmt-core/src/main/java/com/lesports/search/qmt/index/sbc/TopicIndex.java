/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.Topic;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.utils.PinyinUtils;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.TOPIC_INDEX, type = "TOPIC")
public class TopicIndex extends Topic implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4204520067779822547L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.TOPIC;
	}
	
	public String getNamePinyin() {
		return PinyinUtils.pinyin(this.getName());
	}
}
