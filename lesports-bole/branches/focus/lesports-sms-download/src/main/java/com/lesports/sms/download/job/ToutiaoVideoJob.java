package com.lesports.sms.download.job;

import com.lesports.sms.download.service.toutiao.ToutiaoCslVideoService;
import com.lesports.sms.download.service.toutiao.ToutiaoTop12Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/5/26.
 */
@Service("toutiaoVideoJob")
public class ToutiaoVideoJob {

    private static Logger logger = LoggerFactory.getLogger(ToutiaoVideoJob.class);
    @Resource
    private ToutiaoCslVideoService toutiaoCslVideoService;
    @Resource
    private ToutiaoTop12Service toutiaoTop12Service;

    /**
     * 给头条的csl视频
     */
    public void toutiaoCslVideo() {
        logger.info("【toutiaoCslVideos】 start");
        toutiaoCslVideoService.toutiaoCslVideos();
        logger.info("【toutiaoCslVideos】 end");
    }

    /**
     * 给头条的top12视频
     */
    public void toutiaoTop12Video() {
        logger.info("【toutiaoTop12Videos】 start");
        toutiaoTop12Service.toutiaoTop12Videos();
        logger.info("【toutiaoTop12Videos】 end");
    }
}
