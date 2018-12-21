package com.lesports.bole.resources;

import com.lesports.bole.index.MatchIndex;
import com.lesports.bole.service.MatchIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/9/18
 */
@Path("/")
public class MatchResource {
    private static final Logger LOG = LoggerFactory.getLogger(MatchResource.class);
	@Inject
	private MatchIndexService matchIndexService;

	/**
	 * @return
	 */
	@GET
	@LJSONP
	@Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
	@Path("/s/sms/matches/")
	public PageResult<MatchIndex> search(@QueryParam("id") long id,
										   @QueryParam("name") String name,
										   @QueryParam("cid") Long cid,
										   @QueryParam("csid") Long csid,
										   @QueryParam("startTime") List<String> startTime,
										   @QueryParam("country") Integer countryCode,
										   @QueryParam("page") @DefaultValue("0") int page,
										   @QueryParam("count") @DefaultValue("10") int count) {
		PageResult<MatchIndex> result = null;

		try {
			result = matchIndexService.findByParams(id, name, cid, csid, startTime, countryCode, page, count);
		} catch (Exception e) {
			LOG.error("fail to execute search matches, id : {}, name : {}, cid : {}, csid : {}, startTime : {}, countryCode : {}, page : {}, count : {}",
					id, name, cid, csid, startTime, countryCode, page, count, e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return result;
	}
}
