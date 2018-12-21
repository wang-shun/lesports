package com.lesports.search.qmt.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.transcode.VideoTranscodeTaskIndex;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.VideoTranscodeTaskIndexSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Component
@Path("/s/qmt")
public class VideoTranscodeTaskResource extends AbstractSearchResource<VideoTranscodeTaskIndexSearchParam> {

	@SuppressWarnings("unchecked")
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/transcode_video_task/")
	public PageResult<VideoTranscodeTaskIndex> search(@BeanParam VideoTranscodeTaskIndexSearchParam param) {
		return (PageResult<VideoTranscodeTaskIndex>) super.search(param, IndexType.TRANSCODE_VIDEO_TASK);
	}

}
