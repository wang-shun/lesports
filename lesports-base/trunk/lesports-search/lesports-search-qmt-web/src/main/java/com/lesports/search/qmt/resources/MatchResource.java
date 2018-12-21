package com.lesports.search.qmt.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.sbd.MatchIndex;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.MatchIndexSearchParam;
import com.lesports.search.qmt.utils.PageResult;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
@Component
@Path("/s/qmt")
public class MatchResource extends AbstractSearchResource<MatchIndexSearchParam> {

	@SuppressWarnings("unchecked")
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/match/")
	public PageResult<MatchIndex> search(@BeanParam MatchIndexSearchParam param) {
		return (PageResult<MatchIndex>) super.search(param, IndexType.MATCH);
	}

}
