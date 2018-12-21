package com.lesports.search.qmt.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.sbc.EpisodeIndex;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.EpisodeIndexSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Component
@Path("/s/qmt")
public class EpisodeResource extends AbstractSearchResource<EpisodeIndexSearchParam> {

	@SuppressWarnings("unchecked")
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/episode/")
	public PageResult<EpisodeIndex> search(@BeanParam EpisodeIndexSearchParam param) {
		return (PageResult<EpisodeIndex>) super.search(param, IndexType.EPISODE);
	}

}
