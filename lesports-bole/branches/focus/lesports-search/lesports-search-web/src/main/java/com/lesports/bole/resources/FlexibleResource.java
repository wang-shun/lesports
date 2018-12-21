package com.lesports.bole.resources;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;

import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.service.FlexibleSearchService;
import com.lesports.bole.utils.PageResult;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.sms.api.common.Platform;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * trunk.
 *
 * @author sunyue7
 * @since 2016/12/12
 */
@Path("/")
public class FlexibleResource {

	@Inject
	private FlexibleSearchService focusSearchService;

	@Inject
	private DB mongoDb;

	private Map<Long, Platform> callerMapping;

	@PostConstruct
	public void loadCallerMappings() {
		callerMapping = new HashMap<Long, Platform>();
		DBCollection callerTable = mongoDb.getCollection("callers");
		DBCursor cursor = callerTable.find();
		while (cursor.hasNext()) {
			DBObject caller = cursor.next();
			callerMapping.put((Long) caller.get("_id"), Platform.valueOf((String) caller.get("platform")));
		}
	}

	/**
	 * @return
	 */
	@GET
	@LJSONP
	@Produces({ AlternateMediaType.UTF_8_APPLICATION_JSON })
	@Path("/s/searchrelated/")
	public Map<String, PageResult<SearchIndex<Long>>> searchRelated(@QueryParam("caller") Long caller,
			@QueryParam("focusIds") String focusIds, @QueryParam("types") @DefaultValue("news,episodes") String types,
			@QueryParam("startTime") String startTime, @QueryParam("episodeStatus") String episodeStatus,
			@QueryParam("episodeSort") String episodeSort, @QueryParam("page") @DefaultValue("0") Integer page,
			@QueryParam("count") @DefaultValue("10") Integer count) {
		String[] startTimeRange = null;
		if (StringUtils.isNotEmpty(startTime)) {
			startTimeRange = startTime.split(",");
			if (startTimeRange.length > 2) {
				throw new LeWebApplicationException("startTime can only specify upper and lower limit by two time!",
						LeStatus.PARAM_INVALID);
			}
		}
		String[] typeArray = null;
		if (StringUtils.isNotEmpty(types)) {
			typeArray = types.split(",");
		} else {
			typeArray = new String[] { "news", "episodes" };
		}
		Integer[] statusArray = null;
		if (StringUtils.isNotEmpty(episodeStatus)) {
			String[] array = episodeStatus.split(",");
			statusArray = new Integer[array.length];
			for (int i = 0; i < array.length; i++) {
				statusArray[i] = Integer.valueOf(array[i]);
			}
		}
		try {
			return focusSearchService.findRelated(focusIds, typeArray, callerMapping.get(caller), startTimeRange,
					statusArray, episodeSort, page, count);
		} catch (Exception e) {
			throw new LeWebApplicationException(e.getMessage(), LeStatus.EXPECTATION_FAILED);
		}
	}

}
