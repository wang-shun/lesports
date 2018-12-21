/**
 * 
 */
package com.lesports.search.qmt.resources;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
@Component
@Path("/s/qmt")
public class FlexibleSearchResource {

	private static final Logger LOG = LoggerFactory.getLogger(FlexibleSearchResource.class);

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
		try {
			return flexibleSearchService.findByFields(param);
		} catch (Exception e) {
			LOG.error("Fail to search by IDs or tagIds, message : {}", e.getMessage(), e);
		}
		return new PageResult<Indexable>();
	}

	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/searchtypes/")
	public PageResult<Indexable> searchTypes(@BeanParam TypeSearchParam param) {
		param.validate();
		try {
			return flexibleSearchService.findByTypes(param);
		} catch (Exception e) {
			LOG.error("Fail to search by types, message : {}", e.getMessage(), e);
		}
		return new PageResult<Indexable>();
	}

}
