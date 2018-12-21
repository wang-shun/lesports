package com.lesports.bole.resources;

import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.service.BoleNewsService;
import com.lesports.bole.utils.PageResult;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/1/13
 */
@Path("/news")
public class BoleNewsResource {
    private static final Logger LOG = LoggerFactory.getLogger(BoleNewsResource.class);

    @Inject
    private BoleNewsService boleNewsService;

    /**
     * 新闻列表数据
     *
     * @param page  第多少页
     * @param count 每页多少个
     * @return
     */
    @GET
    @LJSONP
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    @Path("/")
    public PageResult<SearchIndex> search(@QueryParam("query") @DefaultValue("") String query,
                                          @QueryParam("page") @DefaultValue("0") int page,
                                          @QueryParam("count") @DefaultValue("10") int count,
                                          @QueryParam("source") @DefaultValue("false") boolean source,
                                          @QueryParam("phrase") @DefaultValue("true") boolean phrase) {
        try {
            if (StringUtils.isEmpty(query)) {
                return boleNewsService.list(page, count, source);
            } else if (phrase){
                return boleNewsService.matchPhrase(query, page, count, source);
            } else {
                return boleNewsService.match(query, page, count, source);
            }
        } catch (Exception e) {
            LOG.error("fail to get bole news list data, page : {}, count : {}. error : {}", page, count, e.getMessage(), e);
            throw new LeWebApplicationException("", LeStatus.EXPECTATION_FAILED);
        }
    }
}
