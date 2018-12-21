package com.lesports.crawler.utils;

import org.junit.Assert;
import org.junit.Test;

public class CrawlerUnitsTest {

    @Test
    public void testGetSite() {
        Assert.assertEquals("zhibo8", CrawlerUtils.getSite("http://www.zhibo8.cc/zhibo/other/2015/120461858.htm"));
        Assert.assertEquals("cntv", CrawlerUtils.getSite("http://sports.cntv.cn/live/cctv5/index.shtml?zhibo8"));
        Assert.assertEquals("firstrowas", CrawlerUtils.getSite("http://firstrowas.eu/watch/392444/1/watch-panachaiki-vs-panathinaikos-athens.html"));
    }
    
    @Test
    public void testGetId() {
        Assert.assertEquals("035822", CrawlerUtils.getIdFromUrl("http://sports.qq.com/a/20151130/035822.htm"));
        Assert.assertEquals("doc-ifxnkkux0915471", CrawlerUtils.getIdFromUrl("http://sports.sina.com.cn/china/j/2016-01-07/doc-ifxnkkux0915471.shtml"));
        Assert.assertEquals("568e0ceb9b1f2", CrawlerUtils.getIdFromUrl("http://news.zhibo8.cc/nba/2016-01-07/568e0ceb9b1f2.htm"));
    }
}
