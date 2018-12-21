package com.lesports.msg.web.resources;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;

/**
 * User: ellios
 * Time: 15-6-28 : 下午9:05
 */
@Path("messages")
public class MessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);


    @POST
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    public boolean processMessage(@QueryParam("entityId") long entityId,
                                             @QueryParam("idType") int idType,
                                             @QueryParam("content") String content) {
        try {
//            Message message = Message.createMessage(entityId, idType, content);
//            messageProcessContext.process(message);
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
