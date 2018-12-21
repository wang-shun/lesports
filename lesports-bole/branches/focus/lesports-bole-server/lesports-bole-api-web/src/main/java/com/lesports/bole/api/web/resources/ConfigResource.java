package com.lesports.bole.api.web.resources;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.lesports.crawler.model.config.CrawlerConfig;
import com.lesports.crawler.model.config.OutputConfig;
import com.lesports.crawler.model.config.OutputOption;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.repository.CrawlerConfigRepository;
import com.lesports.crawler.repository.OutputConfigRepository;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CrawlerResource
 *
 * @author denghui
 */
@Path("/config")
public class ConfigResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigResource.class);
    @Inject
    private CrawlerConfigRepository crawlerConfigRepository;
    @Inject
    private OutputConfigRepository outputConfigRepository;

    @LJSONP
    @PUT
    @Path("/crawler")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public CrawlerConfig configCrawler(@QueryParam("source") int source,
                                       @QueryParam("content") int content,
                                       // 爬虫配置
                                       @QueryParam("enabled") Boolean enabled,
                                       @QueryParam("caller") long callerId) {
        try {
            CrawlerConfig config = crawlerConfigRepository.getConfig(Source.fromInt(source), Content.fromInt(content));
            Preconditions.checkNotNull(config, "config not found");
            if (enabled != null) {
                config.setEnabled(enabled);
                crawlerConfigRepository.save(config);
            }
            return config;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @GET
    @Path("/crawler")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<CrawlerConfig> getCrawlerConfigs(@QueryParam("caller") long callerId) {
        try {
            return crawlerConfigRepository.getAllConfigs();
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @PUT
    @Path("/output")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public OutputConfig configOutput(@QueryParam("site") String site,
                                     @QueryParam("content") int content,
                                     // 输出配置
                                     @QueryParam("output_option") Integer optionInteger,
                                     @QueryParam("cids") String cids,
                                     @QueryParam("priority") Short priority,
                                     @QueryParam("icon_name") String iconName,
                                     @QueryParam("icon_url") String iconUrl,
                                     @QueryParam("caller") long callerId) {
        try {
            OutputConfig config = outputConfigRepository.getConfig(site, Content.fromInt(content));
            Preconditions.checkNotNull(config, "config not found");
            if (optionInteger != null) {
                OutputOption option = OutputOption.fromInt(optionInteger);
                config.setOutputOption(option);
                if ((option == OutputOption.BLACKLIST || option == OutputOption.WHITELIST) && !Strings.isNullOrEmpty(cids)) {
                    List<Long> cidsList = new ArrayList<>();
                    for (String item : cids.split(",")) {
                        cidsList.add(Long.valueOf(item));
                    }
                    config.setCids(cidsList);
                }
            }
            if (priority != null) {
                config.setPriority(priority > 0 ? priority : null);
            }
            config.setIconName(iconName);
            config.setIconUrl(iconUrl);
            outputConfigRepository.save(config);
            return config;
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @GET
    @Path("/output")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public List<OutputConfig> getOutputConfigs(@QueryParam("caller") long callerId) {
        try {
            return outputConfigRepository.getAllConfigs();
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @DELETE
    @Path("/output")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public void deleteOutputConifg(@QueryParam("site") String site,
                                   @QueryParam("content") int content,
                                   @QueryParam("caller") long callerId) {
        try {
            OutputConfig config = outputConfigRepository.getConfig(site, Content.fromInt(content));
            Preconditions.checkNotNull(config, "config not found");
            config.setDeleted(true);
            outputConfigRepository.save(config);
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
