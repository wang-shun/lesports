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

import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.service.NewsIndexService;
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
public class NewsResource {
	private static final Logger LOG = LoggerFactory.getLogger(NewsResource.class);
	@Inject
	private NewsIndexService newsIndexService;

	/**
	 * @return
	 */
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/s/sms/news/")
	public PageResult<NewsIndex> search(@QueryParam("id") long id, @QueryParam("name") String name,
			@QueryParam("newsType") Integer newsType, @QueryParam("onlineStatus") Integer onlineStatus,
			@QueryParam("mid") Integer mid, @QueryParam("platform") Integer platform,
			@QueryParam("publishAt") List<String> publishAt, @QueryParam("country") Integer countryCode,
			@QueryParam("language") Integer languageCode, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("count") @DefaultValue("10") int count, @QueryParam("supportLicence") Integer supportLicence) {
		PageResult<NewsIndex> result = null;

		try {
			result = newsIndexService.findByParams(id, name, newsType, onlineStatus, mid, platform, supportLicence,
					publishAt, countryCode, languageCode, page, count);
		} catch (Exception e) {
			LOG.error(
					"fail to execute search news, id : {}, name : {}, newsType : {}, onlineStatus : {}, mid : {}, platform : {}, publishAt : {}, countryCode : {}, languageCode : {}, page : {}, count : {}",
					id, name, newsType, onlineStatus, mid, platform, publishAt, countryCode, languageCode, page, count,
					e.getMessage(), e);
			throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
		}
		return result;
	}

}
