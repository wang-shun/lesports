package com.lesports.search.qmt.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.transcode.LiveToVideoTaskIndex;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.LiveToVideoTaskIndexSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Component
@Path("/s/qmt")
public class LiveToVideoTaskResource extends AbstractSearchResource<LiveToVideoTaskIndexSearchParam> {

	@SuppressWarnings("unchecked")
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/transcode_live_task/")
	public PageResult<LiveToVideoTaskIndex> search(@BeanParam LiveToVideoTaskIndexSearchParam param) {
		return (PageResult<LiveToVideoTaskIndex>) super.search(param, IndexType.TRANSCODE_LIVE_TASK);
	}

}
