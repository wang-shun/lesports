package com.lesports.bole.api.web.resources;

import com.lesports.bole.model.BoleCompetitionSeason;
import com.lesports.bole.service.BoleCompetitionSeasonService;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 赛季Resource
 *
 * @author denghui
 */
@Path("/competitionseasons")
public class BoleCompetitionSeasonResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoleCompetitionSeasonResource.class);
    @Inject
    private BoleCompetitionSeasonService seasonService;

    /**
     * 根据ID获取赛季
     *
     * @param id
     * @param callerId
     * @return
     */
    @LJSONP
    @GET
    @Path("/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public BoleCompetitionSeason getById(@PathParam("id") long id,
                                         @QueryParam("caller") long callerId) {

        try {
            checkArgument(id > 0, "ID无效");
            return seasonService.getById(id);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
