package com.lesports.sms.data.job;

import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.F1StatsParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.utils.LeProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ruiyuansheng on 2015/7/13.
 */
@Service("f1StatsJob")
public class F1StatsJob extends ThirdDataJob {
    private String f1Stats = LeProperties.getString("f1Stats.files");
    @Resource
    private F1StatsParser f1StatsParser;

    public void run() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        super.downloadAndParseData(ftpUtil, "f1StatsJob", f1Stats, Constants.sportradarRootPath, "//Sport//", f1StatsParser);
    }
}
