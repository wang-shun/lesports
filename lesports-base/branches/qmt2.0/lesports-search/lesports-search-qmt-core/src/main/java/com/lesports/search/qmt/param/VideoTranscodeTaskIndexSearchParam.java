/**
 * 
 */
package com.lesports.search.qmt.param;

import com.lesports.search.qmt.meta.MetaData.IndexType;

/**
 * @author sunyue7
 *
 */
public class VideoTranscodeTaskIndexSearchParam extends SingleTypeSearchParam {

	/**
	 * 
	 */
	public VideoTranscodeTaskIndexSearchParam() {
		super();
		indexType = IndexType.TRANSCODE_VIDEO_TASK;
	}

}
