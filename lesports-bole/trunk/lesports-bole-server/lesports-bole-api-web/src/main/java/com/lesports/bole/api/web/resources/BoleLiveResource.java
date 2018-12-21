package com.lesports.bole.api.web.resources;

import com.google.common.base.Strings;
import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleLiveStatus;
import com.lesports.bole.repository.BoleLiveRepository;
import com.lesports.bole.service.BoleLiveService;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * BoleLiveResource
 *
 * @author denghui
 */
@Path("/lives")
public class BoleLiveResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoleLiveResource.class);
    @Inject
    private BoleLiveRepository liveRepository;
    @Inject
    private BoleLiveService liveService;

    @LJSONP
    @PUT
    @Path("/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public BoleLive update(@PathParam("id") long id,
                           @QueryParam("status") String status,
                           @QueryParam("caller") long callerId) {
        try {
            BoleLive live = null;
            if (!Strings.isNullOrEmpty(status)) {
                live = liveService.updateStatus(id, BoleLiveStatus.valueOf(status));
            }
            return live;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @GET
    @Path("/sites")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<String> getSitesByCid(@QueryParam("cid") long cid,
                                      @QueryParam("caller") long callerId) {
        try {
            checkArgument(cid > 0, "cid must be positive");
            List<String> sites = liveRepository.getSitesByCid(cid);
            return sites;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
