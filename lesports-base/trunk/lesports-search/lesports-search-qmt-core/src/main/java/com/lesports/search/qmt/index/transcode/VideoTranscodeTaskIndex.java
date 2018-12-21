/**
 * 
 */
package com.lesports.search.qmt.index.transcode;

import org.springframework.data.elasticsearch.annotations.Document;

import com.lesports.qmt.transcode.model.VideoTranscodeTask;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
@Document(indexName = MetaData.TRANSCODE_VIDEO_TASK_INDEX, type = "TRANSCODE_VIDEO_TASK")
public class VideoTranscodeTaskIndex extends VideoTranscodeTask implements Indexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7009873564572256548L;

	@Override
	public IndexType getIndexType() {
		return MetaData.IndexType.TRANSCODE_VIDEO_TASK;
	}

}
