package com.lesports.sms.data.resources;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.DataImportConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * User: ellios
 * Time: 15-6-19 : 下午8:12
 */
@Path("/configs")
public class DataImportConfigResource {


    private static final Logger LOG = LoggerFactory.getLogger(DataImportConfigResource.class);
    @GET
    @Path("/")
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    public List<DataImportConfig> getAll(@QueryParam("page") int page, @QueryParam("count") int count) {
        try {
            page = page > 1 ? page-1 : 0;
            PageRequest pageRequest = new PageRequest(page, count, Sort.Direction.DESC, "update_at");
            return null;
        }catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
