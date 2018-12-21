package com.lesports.monitor.cat.filter;

import com.alibaba.fastjson.JSON;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.lesports.monitor.cat.constant.CatMonitorAnnotation;
import com.lesports.monitor.cat.constant.JerseyResponseException;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangdeqiang on 2017/3/8.
 */
public class CatJerseyFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatJerseyFilter.class);
    private static final String TYPE_RESPONSE ="JerseyResponse";

    @Context
    private ResourceInfo resourceInfo;

    public CatJerseyFilter() {
    }

    //校验http接口返回值或者返回值内容的核心字段是否为空，若为空则发送cat事件及异常
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // 处理完请求
        //LOGGER.info("==================================CatJerseyFilter==================================");
        //LOGGER.info(String.valueOf(responseContext.getStatusInfo()));

        Annotation annotation = null;
        try {
            annotation = resourceInfo.getResourceMethod().getAnnotation(CatMonitorAnnotation.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (annotation != null) {
            if (200 != responseContext.getStatus()) {
                this.processCatEvent((ContainerRequest) requestContext, responseContext);
            } else {
                Object entry = responseContext.getEntity();
                if (entry == null) {
                    this.processCatEvent((ContainerRequest) requestContext, responseContext);
                } else if (entry instanceof HashMap) {
                    Object value = ((HashMap) entry).get("entries");
                    if (value == null || ((List) value).size() < 1) {
                        this.processCatEvent((ContainerRequest) requestContext, responseContext);
                    }
                }
            }
        }
    }

    private void processCatEvent(ContainerRequest requestContext, ContainerResponseContext responseContext) {
        LOGGER.info("==================================processCatEvent==================================");
        try {
            //doCatTransaction(requestContext, responseContext);
            String serverHost = requestContext.getBaseUri().getAuthority();
            final String methodName = resourceInfo.getResourceClass().getName() + "." + resourceInfo.getResourceMethod().getName();
            Cat.logEvent("TYPE_RESPONSE", methodName);
            Cat.logEvent(CatConstants.TYPE_REQUEST, requestContext.getRequestUri().toString());
            String errorMessage = "[" + serverHost + "|" + methodName + "]请求的返回值为空，返回信息：" + String.valueOf(responseContext.getStatusInfo());
            Cat.logError(new JerseyResponseException(errorMessage));
        } catch (Exception e) {
            LOGGER.info(JSON.toJSONString(responseContext.getEntity()));
            LOGGER.error(e.getMessage(), e);
            Cat.logError(e);
        }
    }

    private void doCatTransaction(ContainerRequest requestContext, ContainerResponseContext responseContext) {
        Transaction t = Cat.newTransaction(TYPE_RESPONSE, "cat-monitor");
        final String methodName = resourceInfo.getResourceClass().getName() + "." + resourceInfo.getResourceMethod().getName();
        try {
            String serverHost = requestContext.getBaseUri().getAuthority();
            Cat.logEvent("TYPE_RESPONSE", methodName);
            Cat.logEvent(CatConstants.TYPE_REQUEST, requestContext.getRequestUri().toString());
            String errorMessage = "[" + serverHost + "|" + methodName + "]请求的返回值为空，返回信息：" + String.valueOf(responseContext.getStatusInfo());
            Cat.logError(new JerseyResponseException(errorMessage));
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Cat.logError(methodName, e);
            t.setStatus(e.getClass().getSimpleName());
        } finally {
            t.complete();
        }
    }

}
