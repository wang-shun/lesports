package com.lesports.crawler.model;

import com.lesports.crawler.controller.FakedTask;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.Map;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public class RequestMessage {
    private String url;
    private Map<String, Object> extras;
    private Integer priority;
    private String method;
    private String uuid;

    public RequestMessage() {
    }

    public static RequestMessage newInstance(Request request, Task task) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setUrl(request.getUrl());
        requestMessage.setExtras(request.getExtras());
        requestMessage.setMethod(request.getMethod());
        requestMessage.setPriority((int)request.getPriority());
        requestMessage.setUuid(task.getUUID());
        return requestMessage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Request toRequest() {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Request request = new Request();
        request.setUrl(url);
        request.setMethod(method);
        request.setExtras(extras);
        request.setPriority(priority);
        return request;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Task toTask() {
        Task task = new FakedTask(uuid, Site.me().setDomain(UrlUtils.getDomain(url)));
        return task;
    }
}
