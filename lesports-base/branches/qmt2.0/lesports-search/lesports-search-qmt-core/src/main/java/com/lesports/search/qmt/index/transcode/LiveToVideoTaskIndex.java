/**
 * 
 */
package com.lesports.search.qmt.index.transcode;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.transcode.model.LiveToVideoTask;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.QMT_INDEX_TRANSCODE, type = "TRANSCODE_LIVE_TASK")
public class LiveToVideoTaskIndex extends LiveToVideoTask implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6170860518886534991L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.TRANSCODE_LIVE_TASK;
	}

}
