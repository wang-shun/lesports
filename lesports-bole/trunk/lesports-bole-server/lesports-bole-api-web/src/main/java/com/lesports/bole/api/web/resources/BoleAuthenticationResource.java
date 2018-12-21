package com.lesports.bole.api.web.resources;

import com.google.common.collect.Lists;
import com.lesports.cas.pojo.Operation;
import com.lesports.cas.pojo.Permission;
import com.lesports.cas.pojo.User;
import com.lesports.cas.service.Authentication;
import com.lesports.cas.service.impl.AuthenticationImpl;
import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * BoleAuthenticationResource
 *
 * @author denghui
 */
@Path("/auth")
public class BoleAuthenticationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoleAuthenticationResource.class);

    @LJSONP
    @GET
    @Path("/auth")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public Authentication authenticateByTicket(@QueryParam("cas_ticket") String ticket,
                                               @QueryParam("caller") long callerId) {
        try {
            checkNotNull(ticket, "ticket is null");
//            AuthenticationFactory factory = AuthenticationFactory.getInstance();
//            Authentication auth = factory.authenticateByTicket(ticket);
            return new AuthenticationImpl(new User(), Lists.newArrayList(new Permission()));
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @LJSONP
    @GET
    @Path("/check")
    @Produces(AlternateMediaType.UTF_8_APPLICATION_JSON)
    public void check(@QueryParam("path") String path,
                      @QueryParam("permission") String permission,
                      @QueryParam("caller") long callerId) {
        try {
            checkNotNull(path, "path is null");
            checkNotNull(permission, "permission is null");
            String node = "letv.bole." + path;
//            List<Permission> permissions = JSON.parseArray(permission, Permission.class);
//            if (!hasPermission(node, permissions)) {
//                throw new LeWebApplicationException("authenticate failed", LeStatus.FORBIDDEN);
//            }
        } catch (LeWebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new LeWebApplicationException(e.getMessage(), LeStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean hasPermission(String node, List<Permission> perms) {
        // copied from AuthenticationImpl.java
        int code = Operation.VIEWACTION;
        for (Permission p : perms) {
            if ((node.equals(p.getName()) && ((p.getType() & 1) > 0)) ||
                    (node.startsWith(p.getName() + ".") && ((p.getType() & 2) > 0))) {
                code = code - (code & p.getCode());
                if (code == 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
