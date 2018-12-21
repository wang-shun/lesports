package com.lesports.sms.cooperation.job;

import com.lesports.sms.cooperation.service.copaAmerican.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qiaohongxin on 2016/5/26.
 */
@Service("copaAmericanCooperationJob")
public class CopaAmericanCooperationJob {
    private static Logger logger = LoggerFactory.getLogger(CopaAmericanCooperationJob.class);
    @Resource
    SogoGenerator sogoGenerator;
    @Resource
    ThreeGenerator threeGenerator;
    @Resource
    Baidu_scheduleGenerator baiduScheduleGenerator;
    @Resource
    Baidu_aganistGenerator baiduAganistGenerator;
    @Resource
    Baidu_team_aganistGenerator baidu_team_aganistGenerator;
    @Resource
    Baidu_rankingGenerator rangkingGenerator;
    @Resource
    Baidu_player_rankingGenerator player_rankingGenerator;
    @Resource
    BaiduPC_scheduleGenerator baiduPCScheduleGenerator;
    @Resource
    BaiduPC_agamistGenerator baiduPC_agamistGenerator;
    @Resource
    BaiduPC_teamAganistGenerator baiduPC_teamAganistGenerator;
    @Resource
    Three_mobile_Generatator three_mobile_generatator;
    @Resource
    Baidu_LiveGenerator liveGenerator;
    @Resource
    Baidu_statsGenerator statsGenerator;
    @Resource
    Baidu_squadsGenerator squadsGenerator;
    @Resource
    Baidu_eventGenerator eventGenerator;
    /**
     * sogo美洲杯赛程积分榜数据
     */
    public void sogoSchedule() {
        logger.info("【Sogo Schedule and TeamRnking update】 start");
        sogoGenerator.uploadXmlFile();
        logger.info("【Sogo Schedule and TeamRnking update】 start】 end");
    }
    /**
     * 360美洲杯赛程积分榜数据
     */
    public void threeBoxSchedule() {
        logger.info("【360 Schedule and TeamRnking update】 start");
        threeGenerator.uploadXmlFile();
        logger.info("【360 Schedule and TeamRnking update】 start】 end");
    }
    /**
     * 360美洲杯移动端赛程积分榜数据
     */
    public void threeBoxMobileSchedule() {
        logger.info("【360 mobile  Schedule and TeamRnking update】 start");
        three_mobile_generatator.uploadXmlFile();
        logger.info("【360 mobile Schedule and TeamRnking update】 start】 end");
    }
    /**
     * 百度美洲杯PC赛程
     */
    public void baiduPCSchedule() {
        logger.info("【baiduPCSchedule update】 start");
        baiduPCScheduleGenerator.uploadXmlFile();
        logger.info("【baiduPCSchedule update】 start】 end");
    }
    /**
     * 百度美洲杯PC对阵
     */
    public void baiduPCAganist() {
        logger.info("【baiduPCAganist update】 start");
        baiduPC_agamistGenerator.uploadXmlFile();
        logger.info("【baiduPCAganist update】 start】 end");
    }
    /**
     * 百度美洲杯PC基于球队的对阵
     */
    public void baiduPCTeamAganist() {
        logger.info("【baiduPCTeamAganist update】 start");
        baiduPC_teamAganistGenerator.uploadXmlFile();
        logger.info("【baiduPCTeamAganist update】 start】 end");
    }
    /**
     * 百度美洲杯Mobile基于球队的对阵
     */
    public void baiduMobileSchedule() {
        logger.info("【baiduMobileSchedule update】 start");
        baiduScheduleGenerator.uploadXmlFile();
        logger.info("【baiduMobileSchedule update】 start】 end");
    }
    /**
     * 百度美洲杯Mobile对阵
     */
    public void baiduMobileAganist() {
        logger.info("【baiduMobileAganist update】 start");
        baiduAganistGenerator.uploadXmlFile();
        logger.info("【baiduMobileAganist update】 start】 end");
    }
    /**
     * 百度美洲杯PC基于球队的对阵
     */
    public void baiduMobileTeamAganist() {
        logger.info("【baiduMobileTeamAganist update】 start");
        baidu_team_aganistGenerator.uploadXmlFile();
        logger.info("【baiduMobileTeamAganist update】 start】 end");
    }

    /**
     * 百度Mobile积分排行榜
     */
    public void baiduMobileTeamRanking() {
        logger.info("【baiduMobileTeamRanking Update】 start");
        rangkingGenerator.uploadXmlFile();
        logger.info("【baiduMobileTeamRanking Update】 end");
    }
    /**
     * 百度Mobile今日最佳球员排行榜
     */
    public void baiduMobilePlayerRanking() {
        logger.info("【baiduMobileTeamRanking Update】 start");
        player_rankingGenerator.uploadXmlFile();
        logger.info("【baiduMobileTeamRanking Update】 end");
    }
    /**
     * 百度Mobile直播视频连接
     */
    public void baiduMobileLiveVedio() {
        logger.info("【baiduMobileLiveVedio Update】 start");
        liveGenerator.uploadXmlFile();
        logger.info("【baiduMobileLiveVedio Update】 end");
    }

    /**
     * 百度Mobile直播阵容
     */
    public void baiduMobileLiveSquad() {
        logger.info("【baiduMobileLiveSquad Update】 start");
        squadsGenerator.uploadXmlFile();
        logger.info("【baiduMobileLiveSquad Update】 end");
    }
    /**
     * 百度Mobile直播技术统计
     */
    public void baiduMobileLiveStats() {
        logger.info("【baiduMobileLiveStats Update】 start");
        statsGenerator.uploadXmlFile();
        logger.info("【baiduMobileLiveStats Update】 end");
    }
    /**
     * 百度Mobile直播事件
     */
    public void baiduMobileLiveEvent() {
        logger.info("【baiduMobileLiveEvent Update】 start");
        eventGenerator.uploadXmlFile();
        logger.info("【baiduMobileLiveEvent Update】 end");
    }

}
