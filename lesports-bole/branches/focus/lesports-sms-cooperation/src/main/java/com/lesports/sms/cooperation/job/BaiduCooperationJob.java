package com.lesports.sms.cooperation.job;

import com.lesports.sms.cooperation.service.baidu.*;
import com.lesports.sms.cooperation.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/5/26.
 */
@Service("baiduCooperationJob")
public class BaiduCooperationJob {
    private static Logger logger = LoggerFactory.getLogger(BaiduCooperationJob.class);
    @Resource
    private BaiduSearchHistory baiduSearchHistory;
    @Resource
    private BaiduSearchUpdate baiduSearchUpdate;
    @Resource
    private BaiduSearchHot baiduSearchHot;
    @Resource
    private BaiduVideoCompetition baiduVideoCompetition;
    @Resource
    private BaiduVideoTeam baiduVideoTeam;
    @Resource
    private BaiduAlbum baiduAlbum;
    @Resource
    private BaiduAlbumItem baiduAlbumItem;
    @Resource
    private BaiduLiveMatch baiduLiveMatch;



    /**
     * 百度搜索增量接口
     */
    public void baiduSearchHistory() {
        if(Constants.quarzflag){
            logger.info("【baiduSearch History】 start");
            baiduSearchHistory.createSearchHistoryXml();
            logger.info("【baiduSearch History】 end");
        }

    }

    /**
     * 百度搜索增量接口
     */
    public void baiduSearchUpadate() {
        if(Constants.quarzflag) {
            logger.info("【baiduSearch Upadate】 start");
            baiduSearchUpdate.createSearchUpdateXml();
            logger.info("【baiduSearch Upadate】 end");
        }
    }

    /**
     * 百度搜索最热接口
     */
    public void baiduSearchHot() {
        if(Constants.quarzflag) {
            logger.info("【baiduSearch hot】 start");
            baiduSearchHot.createSearchHotXml();
            logger.info("【baiduSearch hot】 end");
        }
    }

    /**
     * 百度搜索最热接口
     */
    public void baiduVideoCompetition() {
        if(Constants.quarzflag) {
            logger.info("【baiduVideo competition】 start");
            baiduVideoCompetition.baiduVideoCompetition();
            logger.info("【baiduVideo competition】 end");
        }
    }

    /**
     * 百度搜索最热接口
     */
    public void baiduVideoTeam() {
        if(Constants.quarzflag) {
            logger.info("【baiduVideo team】 start");
            baiduVideoTeam.baiduVideoTeam();
            logger.info("【baiduVideo team】 end");
        }
    }

    /**
     * 百度自制节目接口
     */
    public void baiduAlbum() {
        if(Constants.quarzflag) {
            logger.info("【baidu album】 start");
            baiduAlbum.baiduAlbumStats();
            logger.info("【baidu album】 end");
        }
    }

    /**
     * 百度自制节目单期
     */
    public void baiduAlbumItem() {
        if(Constants.quarzflag) {
            logger.info("【baidu albumItem】 start");
            baiduAlbumItem.baiduAlbumItemStats();
            logger.info("【baidu albumItem】 end");
        }
    }

    /**
     * 百度搜索最热接口
     */
    public void baiduLiveMatch() {
        if(Constants.quarzflag) {
            logger.info("【baidu live match】 start");
            baiduLiveMatch.baiduLiveStats();
            logger.info("【baidu live match】 end");
        }
    }
}
