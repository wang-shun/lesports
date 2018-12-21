package com.lesports.sms.cooperation.job;

import com.lesports.sms.cooperation.service.alad.*;
import com.lesports.sms.cooperation.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/5/26.
 */
@Service("aladCooperationJob")
public class AladCooperationJob {

    private static Logger logger = LoggerFactory.getLogger(AladCooperationJob.class);
    @Resource
    private OneboxFBStatsService oneboxFBStatsService;
    @Resource
    private OneboxMLBStatsService oneboxMLBStatsService;
    @Resource
    private BaiduMLBStatsService baiduMLBStatsService;
    @Resource
    private SogouPcCslStatsService sogouPcCslStatsService;
    @Resource
    private SogouMobCslStatsService sogouMobCslStatsService;
    @Resource
    private BaiduCslStatsService baiduCslStatsService;
    @Resource
    private BaiduEplStatsService baiduEplStatsService;
    @Resource
    private BaiduSerieaStatsService baiduSerieaStatsService;
    @Resource
    private OneboxS6StatsService oneboxS6StatsService;
    @Resource
    private BaiduNFLStatsService baiduNFLStatsService;
    @Resource
    private BaiduNFLMobStatsService baiduNFLMobStatsService;

    /**
     *百度中超阿拉丁
     */
    public void baiduCslStats() {
        if(Constants.quarzflag){
            logger.info("【baiduCsl Stats】 start");
            baiduCslStatsService.baiduCslStats();
            logger.info("【baiduCsl Stats】 end");
        }
    }

    /**
     *百度英超阿拉丁
     */
    public void baiduEplStats() {
        if(Constants.quarzflag){
            logger.info("【baiduEpl Stats】 start");
            baiduEplStatsService.baiduEplStats();
            logger.info("【baiduEpl Stats】 end");
        }
    }

    /**
     *百度意甲阿拉丁
     */
    public void baiduSerieaStats() {
        if(Constants.quarzflag){
            logger.info("【baiduSeriea Stats】 start");
            baiduSerieaStatsService.baiduSerieaStats();
            logger.info("【baiduSeriea Stats】 end");
        }
    }

    /**
     * 百度mlb阿拉丁
     */
    public void baiduMLBStats() {
        if(Constants.quarzflag) {
            logger.info("【baiduMLB Stats】 start");
            baiduMLBStatsService.baiduMlbStats();
            logger.info("【baiduMLB Stats】 end");
        }
    }

    /**
     *  baidu nfl  pc数据
     */
    public void baiduNflStats() {
        logger.info("【baidu nfl   Stats】 start");
        baiduNFLStatsService.baiduNFLStats();
        logger.info("【baidu nfl   Stats】 end");
    }

    /**
     *  baidu nfl  mob数据
     */
    public void baiduNflMobStats() {
        logger.info("【baidu nfl mob   Stats】 start");
        baiduNFLMobStatsService.baiduNFLStats();
        logger.info("【baidu nfl mob   Stats】 end");
    }

    /**
     * 360中超阿拉丁
     */
    public void oneboxCslStats() {
        if(Constants.quarzflag){
            logger.info("【oneboxCsl Stats】 start");
            oneboxFBStatsService.oneboxFbStats(47001L,2);
            logger.info("【oneboxCsl Stats】 end");
        }
    }

    /**
     * 360英超阿拉丁
     */
    public void oneboxEplStats() {
        if(Constants.quarzflag){
            logger.info("【oneboxEpl Stats】 start");
            oneboxFBStatsService.oneboxFbStats(20001L,4);
            logger.info("【oneboxEpl Stats】 end");
        }
    }

    /**
     * 360意甲阿拉丁
     */
    public void oneboxSerieaStats() {
        if(Constants.quarzflag){
            logger.info("【oneboxSeriea Stats】 start");
            oneboxFBStatsService.oneboxFbStats(29001L,4);
            logger.info("【oneboxSeriea Stats】 end");
        }
    }

    /**
     * 360MLB阿拉丁
     */
    public void oneboxMLBStats() {
        if(Constants.quarzflag) {
            logger.info("【oneboxMLB Stats】 start");
            oneboxMLBStatsService.oneboxMlbStats();
            logger.info("【oneboxMLB Stats】 end");
        }
    }

    /**
     * 360S6阿拉丁
     */
    public void oneboxS6Stats() {
        if(Constants.quarzflag){
            logger.info("【oneboxS6 Stats】 start");
            oneboxS6StatsService.oneboxS6Stats();
            logger.info("【oneboxS6 Stats】 end");
        }
    }



    /**
     * sogou赛程数据
     */
    public void sogouCslScheduleStats() {
        logger.info("【sogou  csl schedule Stats】 start");
        sogouPcCslStatsService.sogouCslScheduleStats();
        logger.info("【sogou  csl schedule Stats】 end");
    }

    /**
     * sogou积分榜数据
     */
    public void sogouCslScoreStats() {
        logger.info("【sogou  csl score Stats】 start");
        sogouPcCslStatsService.sogouCslScoreStats();
        logger.info("【sogou  csl score Stats】 end");
    }

    /**
     * sogou射手榜数据
     */
    public void sogouCslShotStats() {
        logger.info("【sogou csl shot Stats】 start");
        sogouPcCslStatsService.sogouCslShotStats();
        logger.info("【sogou csl shot Stats】 end");
    }

    /**
     *  sogou移动数据
     */
    public void sogouCslStats() {
        logger.info("【sogou csl   Stats】 start");
        sogouMobCslStatsService.sogouCslStats();
        logger.info("【sogou csl   Stats】 end");
    }



}
