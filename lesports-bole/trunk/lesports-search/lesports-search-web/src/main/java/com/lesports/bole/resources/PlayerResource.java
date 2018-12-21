package com.lesports.bole.resources;

import com.lesports.bole.index.PlayerIndex;
import com.lesports.bole.service.PlayerIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/9/18
 */
@Path("/")
public class PlayerResource {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerResource.class);
	@Inject
	private PlayerIndexService playerIndexService;

	/**
	 * @return
	 */
	@GET
	@LJSONP
	@Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
	@Path("/s/sms/players/")
	public PageResult<PlayerIndex> search(@QueryParam("id") long id,
										   @QueryParam("name") String name,
										   @QueryParam("englishName") String englishName,
										   @QueryParam("cid") Long cid,
										   @QueryParam("gameFType") Long gameFType,
										   @QueryParam("country") Integer countryCode,
										   @QueryParam("page") @DefaultValue("0") int page,
										   @QueryParam("count") @DefaultValue("10") int count) {
		PageResult<PlayerIndex> result = null;

		try {
			result = playerIndexService.findByParams(id, name, englishName, cid, gameFType, countryCode, page, count);
		} catch (Exception e) {
			LOG.error("fail to execute search players, id : {}, name : {}, englishName:{},cid : {}, gameFType:{}, countryCode : {}, page : {}, count : {}",
					id, name, englishName, cid, gameFType, countryCode, page, count, e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return result;
	}
}
