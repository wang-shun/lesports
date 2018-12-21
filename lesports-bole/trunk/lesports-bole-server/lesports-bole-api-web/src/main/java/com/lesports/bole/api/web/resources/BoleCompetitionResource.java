package com.lesports.bole.api.web.resources;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.bole.model.BoleCompetition;
import com.lesports.bole.model.BoleStatus;
import com.lesports.bole.repository.BoleCompetitionRepository;
import com.lesports.bole.service.BoleCompetitionService;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.repository.SourceMatchRepository;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Competition;
import org.apache.commons.collections.CollectionUtils;
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
@Path("/competitions")
public class BoleCompetitionResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoleCompetitionResource.class);
    @Inject
    private BoleCompetitionService competitionService;
    @Inject
    private BoleCompetitionRepository competitionRepository;
    @Inject
    protected SourceMatchRepository sourceMatchRepository;

    /**
     * 根据ID获取赛事
     *
     * @param id
     * @param callerId
     * @return
     */
    @LJSONP
    @GET
    @Path("/files/{cid}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<BoleCompetition.DataImportFile> getfileBymId(@PathParam("cid") long id,
                                                             @QueryParam("caller") long callerId) {

        try {
            checkArgument(id > 0, "ID无效");
            BoleCompetition competition = competitionService.getBCompetitionBySmsId(id);
            if (competition == null) {
                Competition competition2 = SbdsInternalApis.getCompetitionById(id);
                BoleCompetition competition1 = new BoleCompetition();
                competition1.setSmsId(id);
                competition1.setName(competition2.getName());
                competition1.setVs(true);
                competition1.setGameFType(competition2.getGameFType());
                competitionRepository.save(competition1);
                return Lists.newArrayList();
            }
            return competition.getFiles();
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 根据ID获取赛事
     *
     * @param id
     * @param callerId
     * @return
     */
    @LJSONP
    @PUT
    @Path("/files/offline")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> offlinefileBymId(@QueryParam("cid") long id,
                                                @QueryParam("file_name") String fileName,
                                                @QueryParam("caller") long callerId) {
        Map<String, Object> result = Maps.newHashMap();

        try {
            checkArgument(id > 0, "ID无效");
            BoleCompetition competition = competitionService.getBCompetitionBySmsId(id);
            checkNotNull(competition, "id %s not exists", id);
            if (CollectionUtils.isNotEmpty(competition.getFiles())) {
                List<BoleCompetition.DataImportFile> files = competition.getFiles();
                for (BoleCompetition.DataImportFile currentFile : competition.getFiles()) {
                    if (currentFile.getFileName().equals(fileName)) {
                        BoleCompetition.DataImportFile newFile = currentFile;
                        newFile.setIsOnline(false);
                        files.remove(currentFile);
                        files.add(newFile);
                    }
                }
                competition.setFiles(files);
                Boolean flag = competitionRepository.save(competition);
                result.put("result", flag);
            }
            return result;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID获取赛事
     *
     * @param id
     * @param callerId
     * @return
     */
    @LJSONP
    @PUT
    @Path("/files/online")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> onlinefileBymId(@QueryParam("cid") long id,
                                               @QueryParam("file_name") String fileName,
                                               @QueryParam("caller") long callerId) {
        Map<String, Object> result = Maps.newHashMap();

        try {
            checkArgument(id > 0, "ID无效");
            BoleCompetition competition = competitionService.getBCompetitionBySmsId(id);
            checkNotNull(competition, "id %s not exists", id);
            if (CollectionUtils.isNotEmpty(competition.getFiles())) {
                List<BoleCompetition.DataImportFile> files = competition.getFiles();
                for (BoleCompetition.DataImportFile currentFile : competition.getFiles()) {
                    if (currentFile.getFileName().equals(fileName)) {
                        BoleCompetition.DataImportFile newFile = currentFile;
                        newFile.setIsOnline(true);
                        files.remove(currentFile);
                        files.add(newFile);
                    }
                }
                competition.setFiles(files);
                Boolean flag = competitionRepository.save(competition);
                result.put("result", flag);
            }
            return result;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 根据ID获取赛事
     *
     * @param id
     * @param callerId
     * @return
     */
    @LJSONP
    @POST
    @Path("/files")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Map<String, Object> savefileBymId(@QueryParam("cid") long id,
                                             @QueryParam("file_type") String fileType,
                                             @QueryParam("partner_type") String partnerType,
                                             @QueryParam("file_name") String fileName,
                                             @QueryParam("caller") long callerId) {
        Map<String, Object> result = Maps.newHashMap();

        try {
            checkArgument(id > 0, "ID无效");
            BoleCompetition competition = competitionService.getBCompetitionBySmsId(id);
            checkNotNull(competition, "id %s not exists", id);
            List<BoleCompetition.DataImportFile> files = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(competition.getFiles())) {
                files = competition.getFiles();
            }
            BoleCompetition.DataImportFile newFile = new BoleCompetition.DataImportFile();
            newFile.setCreateAt("20160713160900");
            newFile.setUpdateAt("20160713160900");
            newFile.setFileType(fileType);
            newFile.setPartner_type(partnerType);
            newFile.setFileName(fileName);
            newFile.setIsOnline(false);
            files.add(newFile);
            competition.setFiles(files);
            Boolean flag = competitionRepository.save(competition);
            result.put("result", flag);

            return result;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 根据ID获取赛事
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
            checkArgument(id > 0, "ID无效");
            BoleCompetition competition = competitionService.getById(id);
            checkNotNull(competition, "id %s not exists", id);

            SourceMatch sourceMatch = null;
            if (!Strings.isNullOrEmpty(competition.getSourceMatchId())) {
                sourceMatch = sourceMatchRepository.findOne(competition.getSourceMatchId());
            }
            return buildResult(competition, sourceMatch);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * BoleCompetition更新
     *
     * @param id
     * @param status
     * @param attachTo
     * @param callerId
     * @return
     */
    @LJSONP
    @PUT
    @Path("/{id}")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public BoleCompetition update(@PathParam("id") long id,
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
                return competitionService.updateStatus(id, BoleStatus.fromInt(status));
            } else {
                if (attachTo > 0) {
                    competitionService.attachSms(id, attachTo);
                } else {
                    competitionService.cancelAttachSms(id);
                }
                return competitionService.getById(id);
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
    public List<BoleCompetition> getForExport(@QueryParam("name") String name,
                                              @QueryParam("caller") long callerId) {

        try {
            List<BoleCompetition> competitions = competitionService.getExportable(name);
            return competitions;
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
    public List<BoleCompetition> export(@QueryParam("id") List<Long> ids,
                                        @QueryParam("caller") long callerId) {

        try {
            checkNotNull(ids, "id is null");
            List<BoleCompetition> competitions = competitionService.export(ids);
            return competitions;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 设置输出顺序
     *
     * @param id
     * @param order
     * @param order
     * @param callerId
     * @return
     */
    @LJSONP
    @PUT
    @Path("/{id}/order")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public BoleCompetition setSiteOrder(@PathParam("id") long id,
                                        @QueryParam("order") List<String> order,
                                        @QueryParam("default") Boolean useDefault,
                                        @QueryParam("caller") long callerId) {

        try {
            BoleCompetition competition = competitionService.getById(id);
            checkArgument(competition != null, "id %d not exists", id);
            if (Boolean.TRUE.equals(useDefault)) {
                competition.setSiteOrder(null);
            } else if (order != null) {
                competition.setSiteOrder(order);
            }
            competitionRepository.save(competition);
            return competition;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("serial")
    private Map<String, Object> buildResult(final BoleCompetition competition, final SourceMatch sourceMatch) {
        return new HashMap<String, Object>() {
            {
                put("competition", competition);
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
