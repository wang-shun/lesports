package com.lesports.sms.cooperation.job;

import com.lesports.sms.cooperation.service.Top12.*;
import com.lesports.sms.cooperation.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/8/23.
 */
@Service("top12CooperationJob")
public class Top12CooperationJob {
    private static Logger logger = LoggerFactory.getLogger(Top12CooperationJob.class);

    @Resource
    private Top12BaiduPcScheduleService top12BaiduPcScheduleService;
    @Resource
    private Top12BaiduVideosService baiduVideosService;
    @Resource
    private Top12SogouPcScheduleService top12SogouPcScheduleService;
    @Resource
    private Top12SogouMobScheduleService top12SogouMobScheduleService;
    @Resource
    private Top12SogouPcScoreService top12SogouPcScoreService;
    @Resource
    private Top12OneboxPcService top12OneboxPcService;
    @Resource
    private Top12OneboxMobService top12OneboxMobService;
    @Resource
    private Baidu_mobileaganistGenerator baidu_mobileaganistGenerator;
    @Resource
    private Baidu_mobileSimplescheduleGenerator baiduMobileSimplescheduleGenerator;
    @Resource
    private Baidu_mobilescheduleGenerator baidu_mobilescheduleGenerator;
    @Resource
    private Top12OneboxPcSingleService top12OneboxPcSingleService;

    /**
     * baidu yidong duizhenka
     */
    public void baiduAganist() {
        logger.info("【Baidu aganist update】 start");
        baidu_mobileaganistGenerator.uploadXmlFile();
        logger.info("【Baidu aganist update】 start】 end");
    }

    /**
     * 360美洲杯赛程积分榜数据
     */
    public void BaiduSimpleSchedule() {
        logger.info("【Baidu simple schedule update】 start");
        baiduMobileSimplescheduleGenerator.uploadXmlFile();
        logger.info("【Baidu aganist update】 start】 end");
    }

    /**
     * 360美洲杯移动端赛程积分榜数据
     */
    public void BaiduSchedule() {
        logger.info("【baidu mobile  Schedule update】 start");
        baidu_mobilescheduleGenerator.uploadXmlFile();
        logger.info("【baidu mobile Schedule update】 start】 end");
    }


    /**
     * 百度12强 pc端
     */
    public void baiduTop12PcStats() {
        if (Constants.quarzflag) {
            logger.info("【baidu top12 pc Stats】 start");
            top12BaiduPcScheduleService.top12PcStats();
            logger.info("【baidu top12 pc Stats】 end");
        }
    }

    /**
     * 百度 12强  视频
     */
    public void baiduTop12VideosStats() {
        if (Constants.quarzflag) {
            logger.info("【baidu top12 video Stats】 start");
            baiduVideosService.baiduTop12Stats();
            logger.info("【baidu top12 video Stats】 end");
        }
    }

    /**
     * 搜狗 12强 pc端
     */
    public void sogouTop12PcStats() {
        if (Constants.quarzflag) {
            logger.info("【sogou top12 pc Stats】 start");
            top12SogouPcScheduleService.top12PcStats();
            logger.info("【sogou top12 pc Stats】 end");
        }
    }

    /**
     * 搜狗 12强 移动端
     */
    public void sogouTop12MobStats() {
        if (Constants.quarzflag) {
            logger.info("【sogou top12 mob Stats】 start");
            top12SogouMobScheduleService.sogouTop12Stats();
            logger.info("【sogou top12 mob Stats】 end");
        }
    }

    /**
     * 搜狗 12强 射手榜
     */
    public void sogouTop12ScorerStats() {
        if (Constants.quarzflag) {
            logger.info("【sogou top12 scorer Stats】 start");
            top12SogouPcScoreService.top12ScoreStats();
            logger.info("【sogou top12 scorer Stats】 end");
        }
    }

    /**
     * 360 12强 pc
     */
    public void oneboxPcStats() {
        if (Constants.quarzflag) {
            logger.info("【onebox top12 pc Stats】 start");
            top12OneboxPcService.oneboxTop12Stats();
            logger.info("【onebox top12 pc Stats】 end");
        }
    }

    /**
     * 360 12强 移动端
     */
    public void oneboxMobStats() {
        logger.info("【onebox top12 mob Stats】 start");
        top12OneboxMobService.oneboxTop12Stats();
        logger.info("【onebox top12 mob Stats】 end");
    }

    /**
     * 360 12强 移动端
     */
    public void oneboxSingleStats() {
        logger.info("【onebox top12 single Stats】 start");
        top12OneboxPcSingleService.oneboxTop12SingleStats();
        logger.info("【onebox top12 single Stats】 end");
    }
}
