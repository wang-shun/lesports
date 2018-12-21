package com.lesports.crawler.controller;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

import java.util.UUID;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/16
 */
public class DomainTask implements Task {
    protected String uuid;
    private Site site;

    private DomainTask me() {
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public DomainTask(Site site) {
        this.site = site;
    }


    @Override
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        if (site != null) {
            return site.getDomain();
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

}
