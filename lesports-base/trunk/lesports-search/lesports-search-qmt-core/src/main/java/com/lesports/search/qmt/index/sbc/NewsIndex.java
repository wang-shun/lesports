/**
 * 
 */
package com.lesports.search.qmt.index.sbc;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.sbc.model.News;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.utils.PinyinUtils;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.NEWS_INDEX, type = "NEWS")
public class NewsIndex extends News implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4541018167414586920L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.NEWS;
	}

	public String getNamePinyin() {
		return PinyinUtils.pinyin(this.getName());
	}
}
