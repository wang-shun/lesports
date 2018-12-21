package com.lesports.scheduler.job;

import com.alibaba.fastjson.JSON;
import com.lesports.api.common.Alarm;
import com.lesports.api.common.AlarmStatus;
import com.lesports.scheduler.core.CatAlarmApis;
import com.lesports.scheduler.job.support.AbstractJob;
import com.lesports.sms.client.SopsInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 赛事后台相关状态同步异常扫描&报警
 * Created by zhangdeqiang on 2017/2/21.
 */
@Component
public class SmsStatusConsistentJob extends AbstractJob {
    private static final Logger LOG = LoggerFactory.getLogger(SmsStatusConsistentJob.class);

    @Scheduled(cron = "0 0/10 * * * *") //每10分钟执行一次 [状态不一致时CAT报警]
    public void syncSmsStatusConsistent() {
        Alarm alarm = null;
        try {
            LOG.info("begin to compare sms status [syncSmsStatusConsistent]");
            alarm = SopsInternalApis.episodeStatusAlarm();
            LOG.info("result： " + JSON.toJSONString(alarm));
            if (!AlarmStatus.NORMAL.equals(alarm.getStatus())) {
                CatAlarmApis.errorAlert(alarm);
            }
        } catch (Exception e) {
            LOG.error("fail to syncSmsStatusConsistent. error : {}", e);
            if (alarm == null) {
                alarm = new Alarm();
                alarm.setSubject("异常报警：状态扫描时报错");
                alarm.setServiceName(e.getMessage());
                alarm.setApi("SopsInternalApis.episodeStatusAlarm()");
            }
            CatAlarmApis.exceptionAlert(alarm, e.fillInStackTrace());
        }
    }

}
