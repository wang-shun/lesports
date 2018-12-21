package com.lesports.bole.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.service.EpisodeIndexService;
import com.lesports.bole.utils.PageResult;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/9/18
 */
@Path("/")
public class EpisodeResource {

	private static final Logger LOG = LoggerFactory.getLogger(EpisodeResource.class);

	@Inject
	private EpisodeIndexService episodeIndexService;

	/**
	 * @return
	 */
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/s/sms/episodes/")
	public PageResult<EpisodeIndex> search(@QueryParam("id") long id, @QueryParam("name") String name,
			@QueryParam("mid") Long mid, @QueryParam("aid") Long aid, @QueryParam("type") Integer type,
			@QueryParam("hasLive") Boolean hasLive, @QueryParam("startTime") List<String> startTime,
			@QueryParam("country") Integer countryCode, @QueryParam("language") Integer languageCode,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("count") @DefaultValue("10") int count,
			@QueryParam("isLephoneChannelMatch") Boolean isLephoneChannelMatch,
			@QueryParam("isLephoneMatch") Boolean isLephoneMatch) {
		PageResult<EpisodeIndex> result = null;

		try {
			result = episodeIndexService.findByParams(id, name, mid, aid, type, hasLive, startTime, countryCode,
					languageCode, page, count, isLephoneChannelMatch, isLephoneMatch);
		} catch (Exception e) {
			LOG.error(
					"fail to execute search episodes, id : {}, name : {}, mid : {}, aid : {}, type : {}, hasLive : {}, startTime : {}, countryCode : {}, languageCode : {}, page : {}, count : {},isLephoneChannelMatch:{},isLephoneMatch:{}",
					id, name, mid, aid, type, hasLive, startTime, countryCode, languageCode, page, count,
					isLephoneChannelMatch, isLephoneMatch, e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return result;
	}

}
