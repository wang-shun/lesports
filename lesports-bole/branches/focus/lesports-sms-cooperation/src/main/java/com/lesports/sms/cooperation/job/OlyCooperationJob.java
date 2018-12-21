package com.lesports.sms.cooperation.job;

import com.lesports.sms.cooperation.service.olympic.OlyBaiduSearchUpdate;
import com.lesports.sms.cooperation.service.olympic.OlyNews360Service;
import com.lesports.sms.cooperation.service.olympic.OlySMUpdate;
import com.lesports.sms.cooperation.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/7/27.
 */
public class OlyCooperationJob {
    private static Logger logger = LoggerFactory.getLogger(OlyCooperationJob.class);
    @Resource
    private OlyBaiduSearchUpdate baiduSearchOlyUpdate;
    @Resource
    private OlyNews360Service olyNews360Service;
    @Resource
    private OlySMUpdate olySMUpdate;

    /**
     * 百度搜索奥运增量接口
     */
    public void baiduSearchOly() {
        if(Constants.quarzflag) {
            logger.info("【baiduSearch oly update】 start");
            baiduSearchOlyUpdate.createSearchOlyXml();
            logger.info("【baiduSearch oly update】 end");
        }
    }

//    /**
//     * 百度搜索奥运全量接口
//     */
//    public void baiduSearchOlyHistory() {
//        if(Constants.quarzflag) {
//            logger.info("【baiduSearch oly history】 start");
//            baiduSearchOlyHistory.c();
//            logger.info("【baiduSearch oly history】 end");
//        }
//    }

    /**
     * 360奥运新闻接口
     */
    public void olyNews360() {
        if(Constants.quarzflag) {
            logger.info("【360News oly】 start");
            olyNews360Service.olyNews360();
            logger.info("【360News oly】 end");
        }
    }

    /**
     * 360奥运新闻接口
     */
    public void olySmUpdate() {
        if(Constants.quarzflag) {
            logger.info("【sm oly】 start");
            olySMUpdate.createSMOlyXml();
            logger.info("【sm oly】 end");
        }
    }

}
