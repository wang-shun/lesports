package com.lesports.crawler.resources;

import com.lesports.jersey.AlternateMediaType;
import com.lesports.jersey.annotation.LJSONP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/9/18
 */
@Path("/")
public class CrawlerResource {
    private static final Logger LOG = LoggerFactory.getLogger(CrawlerResource.class);

    /**
     * @return
     */
    @GET
    @LJSONP
    @Produces({AlternateMediaType.UTF_8_APPLICATION_JSON})
    @Path("/")
    public void crawl() {
//        OOSpider.create(Site.me().setSleepTime(1000)
//                , new ConsolePageModelPipeline(), SinaSportsRepo.class).addPipeline(new ElasticSearchPipeline("sina", "sports"))
//                .addUrl("http://sports.sina.com.cn/").thread(5).runAsync();
    }

}
