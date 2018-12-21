package com.lesports.scheduler.job;

import com.lesports.api.common.CallerParam;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.scheduler.constant.JobConstants;
import com.lesports.scheduler.job.support.AbstractJob;
import com.lesports.sms.client.SbdsInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by zhangdeqiang on 2016/7/19.
 */
@Component
public class SmsOlympicSyncJob extends AbstractJob {
    private static final Logger LOG = LoggerFactory.getLogger(SmsOlympicSyncJob.class);

    //@Scheduled(cron="0 0/15 * * * *") //每15分钟执行一次 [奥运奖牌写入缓存]
    public void syncMedalRankingToCache(){// 项目奖牌榜
        try {
            LOG.info("begin to sync medal ranking to cache");
            CallerParam callerParam = new CallerParam();
            callerParam.setCallerId(JobConstants.JOB_CALLER_ID);
            callerParam.setCountry(CountryCode.CN);
            callerParam.setLanguage(LanguageCode.ZH_CN);
            SbdsInternalApis.syncMedalRankingToCache(callerParam);
        } catch (Exception e) {
            LOG.error("fail to syncMedalRankingToCache. error : {}", e);
        }
    }

    //【优化方案，总榜更新异步发消息刷新分项奖牌榜】
    //@Scheduled(cron="0 0/1 * * * *") //每分钟执行一次 [奥运奖牌总榜写入缓存]
    public void syncFullMedalRankingToCache(){ //总奖牌榜
        try {
            LOG.info("begin to syncFullMedalRankingToCache to cache");
            CallerParam callerParam = new CallerParam();
            callerParam.setCallerId(JobConstants.JOB_CALLER_ID);
            callerParam.setCountry(CountryCode.CN);
            callerParam.setLanguage(LanguageCode.ZH_CN);
            boolean result = SbdsInternalApis.syncFullMedalRankingToCache(callerParam);
            LOG.info("syncFullMedalRankingToCache. result : {}", result);
        } catch (Exception e) {
            LOG.error("fail to syncFullMedalRankingToCache. error : {}", e);
        }
    }

    //@Scheduled(cron="0 0 07 * * ?") //每天执行一次[奥运刷新赛事表缓存]
    public void syncTTotalCompetitionToCache(){
        try {
            LOG.info("begin to syncTTotalCompetitionToCache");
            CallerParam callerParam = new CallerParam();
            callerParam.setCallerId(JobConstants.JOB_CALLER_ID);
            callerParam.setCountry(CountryCode.CN);
            callerParam.setLanguage(LanguageCode.ZH_CN);
            SbdsInternalApis.syncTTotalCompetitionToCache(callerParam);
        } catch (Exception e) {
            LOG.error("fail to syncTTotalCompetitionToCache. error : {}", e);
        }
    }
}
