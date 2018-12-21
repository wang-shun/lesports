package com.lesports.sms.data.job.Top12Job;

import com.lesports.sms.data.job.ThirdDataJob;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.top12.Top12PlayerGoalParser;
import com.lesports.sms.data.service.soda.top12.Top12ScheduleParser;
import com.lesports.sms.data.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/9/18.
 */
@Service("top12ScheduleJob")
public class Top12ScheduleJob  extends ThirdDataJob {
    @Resource
    private Top12ScheduleParser top12ScheduleParser;
    private static Logger LOG = LoggerFactory.getLogger(Top12ScheduleJob.class);

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        super.downloadAndParseData(ftpUtil, "top12ScheduleJob", "s1-412-2018-result.xml", Constants.SODA_SCHEDULE_PATH, "//soda//412//fixtures//", top12ScheduleParser);
    }
}
