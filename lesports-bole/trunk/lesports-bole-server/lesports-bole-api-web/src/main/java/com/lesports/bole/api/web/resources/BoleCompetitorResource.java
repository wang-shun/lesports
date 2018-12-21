package com.lesports.bole.api.web.resources;

import com.google.common.base.Strings;
import com.lesports.bole.model.BoleCompetitor;
import com.lesports.bole.model.BoleCompetitorType;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.service.BoleCompetitorService;
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
@Path("/competitors")
public class BoleCompetitorResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoleCompetitorResource.class);
    @Inject
    private BoleCompetitorService competitorService;
    @Inject
    protected SourceMatchRepository sourceMatchRepository;

    /**
     * 根据ID获取对阵
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
            BoleCompetitor competitor = competitorService.getById(id);
            checkNotNull(competitor, "id %s not exists", id);

            SourceMatch sourceMatch = null;
            if (!Strings.isNullOrEmpty(competitor.getSourceMatchId())) {
                sourceMatch = sourceMatchRepository.findOne(competitor.getSourceMatchId());
            }
            return buildResult(competitor, sourceMatch);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * BoleCompetitor更新
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
    public BoleCompetitor update(@PathParam("id") long id,
                                 @QueryParam("status") Integer status,
                                 @QueryParam("attach_to") Long attachTo,
                                 @QueryParam("type") String type,
                                 @QueryParam("caller") long callerId) {

        try {
            checkArgument(id > 0, "ID无效");
            // status, attach_to不能同时指定
            if ((status == null && attachTo == null) || (status != null && attachTo != null)) {
                throw new LeWebApplicationException("please set status or attach_to", LeStatus.PARAM_INVALID);
            }

            if (status != null) {
                return competitorService.updateStatus(id, BoleStatus.fromInt(status), BoleCompetitorType.valueOf(type));
            } else {
                if (attachTo > 0) {
                    competitorService.attachSms(id, attachTo);
                } else {
                    competitorService.cancelAttachSms(id);
                }
                return competitorService.getById(id);
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
    public List<BoleCompetitor> getForExport(@QueryParam("name") String name,
                                             @QueryParam("caller") long callerId) {

        try {
            List<BoleCompetitor> competitors = competitorService.getExportable(name);
            return competitors;
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
    public List<BoleCompetitor> export(@QueryParam("id") List<Long> ids,
                                       @QueryParam("type") String type,
                                       @QueryParam("caller") long callerId) {

        try {
            checkNotNull(ids, "id is null");
            List<BoleCompetitor> competitors = competitorService.export(ids, type);
            return competitors;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("serial")
    private Map<String, Object> buildResult(final BoleCompetitor competitor, final SourceMatch sourceMatch) {
        return new HashMap<String, Object>() {
            {
                put("competitor", competitor);
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
