/**
 * 
 */
package com.lesports.search.qmt.resources;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.param.IdsSearchParam;
import com.lesports.search.qmt.param.TypeSearchParam;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.service.FlexibleSearchService;
import com.lesports.search.qmt.utils.PageResult;

/**
 * @author sunyue7
 *
 */
@Path("/s/qmt")
public class FlexibleSearchResource {

	@Inject
	private FlexibleSearchService flexibleSearchService;

	@Inject
	protected CommonSearchRepository commonSearchRepository;

	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/searchfields/")
	public PageResult<Indexable> searchByFields(@BeanParam IdsSearchParam param) {
		param.validate();
		return flexibleSearchService.findByFields(param);
	}

	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/searchtypes/")
	public PageResult<Indexable> searchTypes(@BeanParam TypeSearchParam param) {
		param.validate();
		return flexibleSearchService.findByTypes(param);
	}

}
