package com.lesports.bole.resources;

import com.google.common.collect.Lists;
import com.lesports.bole.index.VideoIndex;
import com.lesports.bole.service.VideoIndexService;
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
public class VideoResource {
    private static final Logger LOG = LoggerFactory.getLogger(VideoResource.class);
    @Inject
    private VideoIndexService videoIndexService;

    /**
     * @return
     */
    @GET
    @LJSONP
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    @Path("/s/sms/videos/")
    public PageResult<VideoIndex> search(@QueryParam("id") long id,
                                         @QueryParam("page") @DefaultValue("0") int page,
                                         @QueryParam("count") @DefaultValue("10") int count,
                                         @QueryParam("updateAt") List<String> updateAt,
                                         @QueryParam("country") Integer countryCode,
                                         @QueryParam("name") String name,
                                         @QueryParam("platform") Integer platform,
                                         @QueryParam("type") Integer type,
                                         @QueryParam("supportLicence") Integer supportLicence) {
        PageResult<VideoIndex> result = null;
        try {
            result = videoIndexService.findByParams(id, name, type, countryCode, platform, supportLicence, updateAt, page, count);
        } catch (Exception e) {
            LOG.error("fail to execute search video index by name : {}, type : {}, updateAt : {}, page : {}, count : {}. error : {}",
                    name, type, updateAt, page, count, e.getMessage(), e);
            throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
        }
        return result;
    }

}
