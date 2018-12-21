package com.lesports.search.qmt.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.sbd.PlayerIndex;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.PlayerIndexSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Component
@Path("/s/qmt")
public class PlayerResource extends AbstractSearchResource<PlayerIndexSearchParam> {

	@SuppressWarnings("unchecked")
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/player/")
	public PageResult<PlayerIndex> search(@BeanParam PlayerIndexSearchParam param) {
		return (PageResult<PlayerIndex>) super.search(param, IndexType.PLAYER);
	}

}
