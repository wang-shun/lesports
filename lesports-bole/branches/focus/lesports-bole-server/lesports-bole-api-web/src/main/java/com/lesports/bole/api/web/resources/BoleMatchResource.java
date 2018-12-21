package com.lesports.bole.api.web.resources;

import com.google.common.base.Strings;
import com.lesports.bole.model.BoleLive;
import com.lesports.bole.model.BoleMatch;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.service.BoleLiveService;
import com.lesports.bole.service.BoleMatchService;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.repository.SourceMatchRepository;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * BoleCompetitionResource
 *
 * @author denghui
 */
@Path("/matches")
public class BoleMatchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoleMatchResource.class);
    @Inject
    private BoleMatchService matchService;
    @Inject
    private BoleLiveService liveService;
    @Inject
    protected SourceMatchRepository sourceMatchRepository;

    /**
     * 根据ID获取比赛
     *
     * @param id
     * @param callerId
     * @return
     */
    @LJSONP
    @GET
    @Path("/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> getById(@PathParam("id") long id,
                                       @QueryParam("caller") long callerId) {

        try {
            checkArgument(id > 0, "赛事ID无效");
            BoleMatch match = matchService.getById(id);
            checkNotNull(match, "id %s not exists", id);

            List<BoleLive> lives = liveService.getByMatchId(id);
            SourceMatch sourceMatch = null;
            if (!Strings.isNullOrEmpty(match.getSourceMatchId())) {
                sourceMatch = sourceMatchRepository.findOne(match.getSourceMatchId());
            }
            return buildResult(match, lives, sourceMatch);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * BoleMatch更新
     *
     * @param id
     * @param attachTo
     * @param callerId
     * @return
     */
    @LJSONP
    @PUT
    @Path("/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public BoleMatch update(@PathParam("id") long id,
                            @QueryParam("status") Integer status,
                            @QueryParam("attach_to") Long attachTo,
                            @QueryParam("caller") long callerId) {

        try {
            checkArgument(id > 0, "ID无效");
            // status, attach_to不能同时指定
            if ((status == null && attachTo == null) || (status != null && attachTo != null)) {
                throw new LeWebApplicationException("please set status or attach_to", LeStatus.PARAM_INVALID);
            }

            if (status != null) {
                return matchService.updateStatus(id, BoleStatus.fromInt(status));
            } else {
                if (attachTo > 0) {
                    matchService.attachSms(id, attachTo);
                } else {
                    matchService.cancelAttachSms(id);
                }
                return matchService.getById(id);
            }
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @GET
    @Path("/export")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<BoleMatch> getForExport(@QueryParam("start_time") String startTime,
                                        @QueryParam("com1") long com1,
                                        @QueryParam("com2") long com2,
                                        @QueryParam("caller") long callerId) {

        try {
            checkNotNull(startTime, "start_time is null");
            List<BoleMatch> matches = matchService.getExportable(startTime, com1, com2);
            return matches;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @PUT
    @Path("/export")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<BoleMatch> export(@QueryParam("id") List<Long> ids,
                                  @QueryParam("caller") long callerId) {

        try {
            checkNotNull(ids, "id is null");
            List<BoleMatch> matches = matchService.export(ids);
            return matches;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("serial")
    private Map<String, Object> buildResult(final BoleMatch match, final List<BoleLive> lives, final SourceMatch sourceMatch) {
        return new HashMap<String, Object>() {
            {
                put("match", match);
                put("lives", lives);
                put("sourceUrl", getSourceUrl(sourceMatch));
            }
        };
    }

    private String getSourceUrl(SourceMatch sourceMatch) {
        if (sourceMatch == null) {
            return "无";
        }

        // 返回LiveUrl以方便编辑观察
        if (!sourceMatch.getLives().isEmpty() && (sourceMatch.getSource() == Source.QQ || sourceMatch.getSource() == Source.SINA)) {
            return sourceMatch.getLives().get(0).getUrl();
        }

        return sourceMatch.getSourceUrl();
    }
}
