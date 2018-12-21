package com.lesports.id.service.thrift;

import com.lesports.id.api.IdType;
import com.lesports.id.api.TIdService;
import com.lesports.id.service.IdService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static me.ellios.hedwig.http.mediatype.ExtendedMediaType.APPLICATION_X_THRIFT;

/**
 * User: ellios
 * Time: 15-5-17 : 下午4:13
 */
@Service("thriftIdService")
@Path("/r/ids")
@Produces({APPLICATION_X_THRIFT})
public class TIdServiceAdapter implements TIdService.Iface {

    private static final Logger LOG = LoggerFactory.getLogger(TIdServiceAdapter.class);

    @Inject
    private IdService idService;


    @Override
    public long nextId(IdType type) throws TException {
        try {
            return idService.nextId(type);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
    }

    @GET
    @Path("/next/{type:\\d+}")
    public long nextId(@PathParam("type") int number) throws Exception {
        IdType type = IdType.findByValue(number);
        return nextId(type);
    }
}
