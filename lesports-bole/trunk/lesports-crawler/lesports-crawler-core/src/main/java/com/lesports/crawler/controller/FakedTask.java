package com.lesports.crawler.controller;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
public class FakedTask implements Task {
    private String uuid;
    private Site site;

    public FakedTask(String uuid, Site site) {
        this.uuid = uuid;
        this.site = site;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public Site getSite() {
        return site;
    }
}
